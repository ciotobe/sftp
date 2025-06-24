package jp.co.jri.epix.sftp;

import jp.co.jri.epix.sftp.service.SftpService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SftpApplication implements CommandLineRunner {

	private final SftpService sftpService;

	public SftpApplication(SftpService sftpService) {
		this.sftpService = sftpService;
	}

	public static void main(String[] args) {
		SpringApplication.run(SftpApplication.class, args);
	}

	@Override
	public void run(String... args) {
		File file = new File(sftpService.getUploadFilePath());
		if (file.exists()) {
			sftpService.uploadFile(file);
			System.out.println("✅ Upload triggered for: " + file.getAbsolutePath());
		} else {
			System.out.println("❌ File not found: " + file.getAbsolutePath());
		}
	}
}
