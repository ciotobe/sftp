package jp.co.jri.epix.sftp.config;

import com.jcraft.jsch.ChannelSftp;
import jp.co.jri.epix.sftp.mapper.ApiAccessMapper;
import jp.co.jri.epix.sftp.model.ApiAccess;
import jp.co.jri.epix.sftp.service.SftpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.handler.advice.ErrorMessageSendingRecoverer;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.MethodInvocationRecoverer;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class SftpConfig {
    private static final Logger logger = LogManager.getLogger(SftpService.class);
    private final ApiAccessMapper apiAccessMapper;

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    // for user id and password authentication
    @Value("${sftp.password}")
    private String password;
    // <-- for private key authentication
    @Value("${sftp.privateKeyPath}")
    private String privateKeyPath;
    // -->
    @Value("${sftp.privateKeyPassphrase:}")
    private String privateKeyPassphrase;
    @Value("${sftp.remote-directory}")
    private String remoteDirectory;
    @Value("${sftp.local-directory}")
    private String localDirectory;

    @Value("${sftp.retry.max-attempts}")
    private int maxAttempts;

    @Value("${sftp.retry.delay}")
    private long retryDelay;

    public SftpConfig(ApiAccessMapper apiAccessMapper) {
        this.apiAccessMapper = apiAccessMapper;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    @Bean
    public CachingSessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
//        ApiAccess apiAccess = apiAccessMapper.findApiAccessByApplication("Trade");
//        logger.debug("application=" + apiAccess.getApplication());
//        logger.debug("component=" + apiAccess.getComponent());
//        logger.debug("apiKey=" + apiAccess.getApiKey());
//        logger.debug("expiryDate=" + apiAccess.getExpiryDate());
//        logger.debug("neverExpired=" + apiAccess.getNeverExpired());

        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);

        // for user id and password authentication
        factory.setPassword(password);

        // for private key authentication
//        factory.setPrivateKey(new FileSystemResource(privateKeyPath));
//        logger.debug("privateKeyPath=" + privateKeyPath);
//        if (privateKeyPassphrase != null && !privateKeyPassphrase.isEmpty()) {
//            factory.setPrivateKeyPassphrase(privateKeyPassphrase);
//            logger.debug("privateKeyPassphrase=" + privateKeyPassphrase);
//        }

        factory.setAllowUnknownKeys(true); // optionally disable strict host key checking
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    public RemoteFileTemplate<ChannelSftp.LsEntry> remoteFileTemplate() {
        RemoteFileTemplate<ChannelSftp.LsEntry> template = new RemoteFileTemplate<>(sftpSessionFactory());
        template.setRemoteDirectoryExpression(new LiteralExpression(remoteDirectory));
        template.setAutoCreateDirectory(true);
        return template;
    }

    @Bean
    public MessageHandler sftpMessageHandler() {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
        handler.setRemoteDirectoryExpression(new LiteralExpression(remoteDirectory));
        handler.setAutoCreateDirectory(true);
        handler.setFileNameGenerator(message -> (String) message.getHeaders().get(FileHeaders.REMOTE_FILE));
        return handler;
    }

    @Bean
    public MessageChannel toSftpChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow sftpUploadFlow() {
        return IntegrationFlows.from(toSftpChannel())
                .split()
                .handle(sftpMessageHandler(), e -> e.advice(sftpRetryAdvice()))
                .get();
    }

    @Bean
    public RequestHandlerRetryAdvice sftpRetryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();

        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryDelay); // in ms

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        advice.setRetryTemplate(retryTemplate);

        // Optional: log or handle final failure
        advice.setRecoveryCallback(context -> {
            Message<?> failedMessage = (Message<?>) context.getAttribute("message");
            System.err.println("SFTP upload permanently failed: " + failedMessage);
            return null;
        });

        return advice;
    }
}

