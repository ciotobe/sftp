package jp.co.jri.epix.sftp;

import jp.co.jri.epix.sftp.config.SftpConfig;
import jp.co.jri.epix.sftp.model.ApiAccess;
import jp.co.jri.epix.sftp.model.StreamWithFilename;
import jp.co.jri.epix.sftp.service.SftpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SftpApplication implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(SftpApplication.class);

    private final SftpService sftpService;
    private final SftpConfig sftpConfig;

    public SftpApplication(SftpService sftpService, SftpConfig sftpConfig) {
        this.sftpService = sftpService;
        this.sftpConfig = sftpConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(SftpApplication.class, args);
    }

    @Override
    public void run(String... args) {
//        String uploadFilename = null;
//        String uploadDir = null;
//
//        for (String arg : args) {
//            if (arg.startsWith("--upload.file=")) {
//                uploadFilename = arg.substring("--upload.file=".length());
//            }
//            if (arg.startsWith("--upload.dir=")) {
//                uploadDir = arg.substring("--upload.dir=".length());
//            }
//        }
//
//        if (uploadFilename != null) {
//            File file = new File(sftpConfig.getLocalDirectory() + "\\\\" + uploadFilename);
//            if (!file.exists()) {
//                logger.error("File not found: {}", file.getAbsolutePath());
//            } else {
//                sftpService.uploadFile(file);
//                logger.info("Uploaded file: {}", file.getAbsolutePath());
//            }
//        }
//
//        if (uploadDir != null) {
//            File dir = new File(uploadDir);
//            if (!dir.isDirectory()) {
//                logger.error("Not a directory: {}", dir.getAbsolutePath());
//            } else {
//                File[] files = dir.listFiles(File::isFile);
//
//                if (files == null || files.length == 0) {
//                    logger.warn("No files found in directory: {}", dir.getAbsolutePath());
//                } else {
//                    for (File f : files) {
//                        sftpService.uploadFile(f);
//                        logger.info("Uploaded file: {}", f.getAbsolutePath());
//                    }
//                }
//            }
//        }
//
//        if (uploadFilename == null && uploadDir == null) {
//            logger.warn("You must provide --upload.file or --upload.dir");
//        }

//        String content = "Upload from memory!";
//        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
//        sftpService.uploadInputStream(stream, sftpConfig.getRemoteDirectory(),"upload-from-stream.txt");
        //InputStream stream = new ByteArrayInputStream(sftpService.retrieveAllData("Trade").get(0).getApiKey().getBytes(StandardCharsets.UTF_8));
        List<ApiAccess> apiAccessList = sftpService.retrieveAllData("Trade");

        List<StreamWithFilename> streamList = new ArrayList<>();

        if(apiAccessList.size() > 0){
            for(int i = 0; i < apiAccessList.size(); i++){
                StreamWithFilename stream = new StreamWithFilename();
                stream.setStream(new ByteArrayInputStream(apiAccessList.get(i).getApiKey().getBytes()));
                stream.setFilename(apiAccessList.get(i).getComponent());
                streamList.add(stream);
            }
        }

        sftpService.uploadInputStream(streamList, sftpConfig.getRemoteDirectory());
    }
}
