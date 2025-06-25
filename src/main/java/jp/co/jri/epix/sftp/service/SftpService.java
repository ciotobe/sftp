package jp.co.jri.epix.sftp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.file.*;

@Service
public class SftpService {
    private static final Logger logger = LogManager.getLogger(SftpService.class);
    private final MessageChannel toSftpChannel;

    public SftpService(MessageChannel toSftpChannel) {
        this.toSftpChannel = toSftpChannel;
    }

    public void uploadFile(File file) {
        Message<File> message = MessageBuilder.withPayload(file).build();
        toSftpChannel.send(message);
        logger.debug("Upload message sent to channel for file: {}", file.getName());
    }
}

