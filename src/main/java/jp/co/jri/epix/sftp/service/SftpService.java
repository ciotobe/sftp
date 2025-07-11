package jp.co.jri.epix.sftp.service;

import jp.co.jri.epix.sftp.mapper.ApiAccessMapper;
import jp.co.jri.epix.sftp.model.ApiAccess;
import jp.co.jri.epix.sftp.model.StreamWithFilename;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SftpService {
    private static final Logger logger = LogManager.getLogger(SftpService.class);
    private final MessageChannel toSftpChannel;
    private final ApiAccessMapper apiAccessMapper;

    public SftpService(
            MessageChannel toSftpChannel,
            ApiAccessMapper apiAccessMapper) {
        this.toSftpChannel = toSftpChannel;
        this.apiAccessMapper = apiAccessMapper;
    }

    public void uploadFile(File file) {
        try {
            Message<File> message = MessageBuilder
                    .withPayload(file)
                    .setHeader(FileHeaders.REMOTE_FILE, file.getName())
                    .build();

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
                    .setHeader(FileHeaders.REMOTE_DIRECTORY, remoteDirectory)
                    .setHeader(FileHeaders.REMOTE_FILE, remoteFilename)
                    .build();

            toSftpChannel.send(message);
            logger.info("Uploaded InputStream to remote file: {} {}", remoteDirectory, remoteFilename);
        } catch (Exception e) {
            logger.error("Failed to upload InputStream to {} {}: {}", remoteDirectory, remoteFilename, e.getMessage(), e);
        }
    }

    public void uploadInputStream(List<StreamWithFilename> items, String remoteDirectory) {
        try {
            List<Message<InputStream>> messages = items.stream()
                    .map(item -> MessageBuilder.withPayload(item.getStream())
                            .setHeader(FileHeaders.REMOTE_FILE, item.getFilename())
                            .setHeader(FileHeaders.REMOTE_DIRECTORY, remoteDirectory)
                            .build())
                    .collect(Collectors.toList());

            Message<List<Message<InputStream>>> finalMessage = MessageBuilder.withPayload(messages).build();
            toSftpChannel.send(finalMessage);

            logger.info("Uploaded InputStream to remote file: {} {}", remoteDirectory);
        } catch (Exception e) {
            logger.error("Failed to upload InputStream to {} {}: {}", remoteDirectory, e.getMessage(), e);
        }
    }

    public List<ApiAccess> retrieveAllData(String application) {
        List<ApiAccess> apiAccessList = apiAccessMapper.findAllByApplication("Trade");

        if (apiAccessList.size() > 0) {
            return apiAccessList;
        }

        return null;
    }
}

