package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {

    ReservationDTO createReservation (ReservationDTO reservationDTO);
    ReservationDTO editReservation(Long reservationId, ReservationDTO reservationDTO);
    void deleteReservation(Long reservationId);
}
