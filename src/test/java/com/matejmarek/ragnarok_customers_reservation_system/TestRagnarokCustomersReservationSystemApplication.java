package com.matejmarek.ragnarok_customers_reservation_system;

import org.springframework.boot.SpringApplication;

public class TestRagnarokCustomersReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(RagnarokCustomersReservationSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
