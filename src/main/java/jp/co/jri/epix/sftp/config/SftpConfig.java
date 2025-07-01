package jp.co.jri.epix.sftp.config;

import com.jcraft.jsch.ChannelSftp;
import jp.co.jri.epix.sftp.entity.ApiAccess;
import jp.co.jri.epix.sftp.repo.ApiAccessRepository;
import jp.co.jri.epix.sftp.service.SftpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class SftpConfig {
    private static final Logger logger = LogManager.getLogger(SftpService.class);
    private final ApiAccessRepository apiAccessRepository;

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

    public SftpConfig(ApiAccessRepository apiAccessRepository) {
        this.apiAccessRepository = apiAccessRepository;
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
        ApiAccess apiAccess = apiAccessRepository.findApiAccessByApplication("Trade").orElseThrow(() -> new IllegalStateException("No SFTP credentials in database"));
        logger.debug("application=" + apiAccess.getApplication());
        logger.debug("component=" + apiAccess.getComponent());
        logger.debug("apiKey=" + apiAccess.getApiKey());
        logger.debug("expiryDate=" + apiAccess.getExpiryDate());
        logger.debug("neverExpired=" + apiAccess.getNeverExpired());

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
                .handle(sftpMessageHandler())
                .get();
    }

    /*
    @Bean
    public IntegrationFlow sftpDownloadFlow() {
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
        synchronizer.setDeleteRemoteFiles(false);
        synchronizer.setRemoteDirectory(remoteDirectory);
        synchronizer.setFilter(new SftpSimplePatternFileListFilter("*.txt"));

        SftpInboundFileSynchronizingMessageSource source =
                new SftpInboundFileSynchronizingMessageSource(synchronizer);
        source.setLocalDirectory(new File(localDirectory));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new org.springframework.integration.file.filters.AcceptOnceFileListFilter<>());

        return IntegrationFlows.from(source, e -> e.poller(Pollers.fixedDelay(5000)))
                .handle(message -> {
                    System.out.println("Downloaded: " + message.getPayload());
                })
                .get();
    }
    */
}

