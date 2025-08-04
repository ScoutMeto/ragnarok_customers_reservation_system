package com.matejmarek.ragnarok_customers_reservation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EntityScan("com.matejmarek.ragnarok_customers_reservation_system.entity")
@EnableJpaRepositories(basePackages = "com.matejmarek.ragnarok_customers_reservation_system.entity.repository")
@SpringBootApplication
public class RagnarokCustomersReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagnarokCustomersReservationSystemApplication.class, args);
	}

}


//pro testování:
//admin@example.com
//Admin123!