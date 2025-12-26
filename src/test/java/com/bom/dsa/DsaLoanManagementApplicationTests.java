package com.bom.dsa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "services.approval.base-url=http://localhost:8081")
@org.springframework.test.context.ActiveProfiles("test")
class DsaLoanManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
