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

//
// Je třeba metody otestovat ->
//vše přes POSTMANa.
// Pak se budeme zabývat basic frontendem.
//POZN.: některé metody už navracejí ještě neexistující xxx.html stránky. Vytvoř pro účely testování nějaké šablony.

//pro testování:
//admin@example.com
//Admin123!