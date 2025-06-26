package jp.co.jri.epix.sftp.service;

import com.jcraft.jsch.ChannelSftp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class SftpService {
    private static final Logger logger = LogManager.getLogger(SftpService.class);
    private final MessageChannel toSftpChannel;
    private final RemoteFileTemplate<ChannelSftp.LsEntry> remoteFileTemplate;

    public SftpService(
            MessageChannel toSftpChannel,
            RemoteFileTemplate<ChannelSftp.LsEntry> remoteFileTemplate) {
        this.toSftpChannel = toSftpChannel;
        this.remoteFileTemplate = remoteFileTemplate;
    }

    public void uploadFile(File file) {
        try {
            Message<File> message = MessageBuilder.withPayload(file).build();
            toSftpChannel.send(message);
            logger.debug("Upload message sent for: {}", file.getName());
        } catch (Exception ex) {
            logger.error("Upload failed for {}: {}", file.getName(), ex.getMessage(), ex);
        }
    }

    public void uploadInputStream(InputStream inputStream, String remoteDirectory, String remoteFilename) {
        try {
            Message<InputStream> message = MessageBuilder
                    .withPayload(inputStream)
                    .setHeader("file_remoteDirectory", remoteDirectory)
                    .setHeader("file_remoteFile", remoteFilename)
                    .build();

            remoteFileTemplate.send(message);
            logger.info("Uploaded InputStream to remote file: {}", remoteFilename);
        } catch (Exception e) {
            logger.error("Failed to upload InputStream to {}: {}", remoteFilename, e.getMessage(), e);
        }
    }
}

