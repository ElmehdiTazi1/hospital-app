package org.mql.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.mql.hospital.entities")

public class HospitalAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(HospitalAppApplication.class, args);
	}
}