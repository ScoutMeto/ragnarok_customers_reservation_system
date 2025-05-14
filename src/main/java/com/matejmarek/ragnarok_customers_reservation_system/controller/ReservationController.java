package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@RestController
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    ////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping({"api/createNewReservation/", "api/createNewReservation"})
    public ReservationDTO addReservation(@RequestBody @Valid ReservationDTO reservationDTO) {
        System.out.println("Požadavek na vytvoření nové rezervace (ReservationController, addReservation): " + reservationDTO);
        return reservationService.createReservation(reservationDTO);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    // PUT endpoint pro úpravu rezervace
    @PutMapping({"/api/editReservation/{reservationId}/", "/api/editReservation/{reservationId}"})
    public ReservationDTO editReservation(@PathVariable("reservationId") Long reservationId, @RequestBody @Valid ReservationDTO reservationDTO) {
        System.out.println("Požadavek na úpravu rezervace (ReservationController, editReservation): " + reservationId);
        return reservationService.editReservation(reservationId, reservationDTO);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    // DELETE endpoint pro smazání rezervace
    @DeleteMapping({"/api/deleteReservation/{reservationId}/", "/api/deleteReservation/{reservationId}"})
    public ResponseEntity<String> deleteReservation(@PathVariable("reservationId") Long reservationId) {
        System.out.println("Požadavek na smazání rezervace (ReservationController, deleteReservation): " + reservationId);
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.ok("Rezervace byla úspěšně smazána.");
    }

}
