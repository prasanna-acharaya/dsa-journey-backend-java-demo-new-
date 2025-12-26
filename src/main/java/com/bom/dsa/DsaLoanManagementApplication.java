package com.bom.dsa;

import com.bom.dsa.client.ApprovalClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DsaLoanManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsaLoanManagementApplication.class, args);
	}

	@Bean
	@org.springframework.context.annotation.Profile("!test")
	public CommandLineRunner pingApprovalService(ApprovalClient approvalClient) {
		return args -> {
			approvalClient.ping().subscribe();
		};
	}

}
