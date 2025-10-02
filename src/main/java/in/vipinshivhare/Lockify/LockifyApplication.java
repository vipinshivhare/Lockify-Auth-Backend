package in.vipinshivhare.Lockify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LockifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockifyApplication.class, args);
	}

}
