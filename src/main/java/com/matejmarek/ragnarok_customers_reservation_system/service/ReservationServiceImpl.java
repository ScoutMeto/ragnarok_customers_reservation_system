package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.ReservationMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationMapper reservationMapper;

    @Override
    @Transactional
    public ReservationDTO createReservation (ReservationDTO reservationDTO) {

        ReservationEntity dtoToEntity = reservationMapper.toEntity(reservationDTO);
        ReservationEntity savedEntity = reservationRepository.save(dtoToEntity);

        System.out.println("Rezervace uložena: " + reservationDTO);
        return reservationDTO;
    }
}
