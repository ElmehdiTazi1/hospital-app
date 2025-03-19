package org.mql.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.mql.hospital.config.CommonConfig;
@SpringBootApplication
@EntityScan("org.mql.hospital.entities")
@Import(CommonConfig.class)

public class HospitalAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(HospitalAppApplication.class, args);
	}
}