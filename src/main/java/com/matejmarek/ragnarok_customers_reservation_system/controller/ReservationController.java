package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @PostMapping({"api/createNewReservation/", "api/createNewReservation"})
    public ReservationDTO addReservation(@RequestBody @Valid ReservationDTO reservationDTO) {
        System.out.println("Požadavek na vytvoření nové rezervace (ReservationController, addReservation): " + reservationDTO);
        return reservationService.createReservation(reservationDTO);
    }

}
