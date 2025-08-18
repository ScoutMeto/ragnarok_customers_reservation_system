package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.ReservationMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.ReservationRepository;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ReservationMapper reservationMapper;
    @Autowired
    TrainingRepository trainingRepository;

    @Override
    @Transactional
//    @CacheEvict(value = "trainingsByMonth", allEntries = true)
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        TrainingEntity trainingEntity = trainingRepository.findById(reservationDTO.getTrainingId())
                .orElseThrow(() -> new EntityNotFoundException("Trénink s ID " + reservationDTO.getTrainingId() + " nenalezen."));

        ReservationEntity reservationEntity = reservationMapper.toEntity(reservationDTO);
        reservationEntity.setTraining(trainingEntity); // správné přiřazení

        reservationRepository.save(reservationEntity);

        System.out.println("Rezervace uložena: " + reservationDTO);
        return reservationDTO;
    }

    @Override
    @Transactional
//    @CacheEvict(value = "trainingsByMonth", allEntries = true)
    public ReservationDTO editReservation(Long reservationId, ReservationDTO reservationDTO) {
        // Najdeme rezervaci podle ID
        ReservationEntity existingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Rezervace s ID " + reservationId + " nenalezena."));

        // Změníme hodnoty podle DTO
        existingReservation.setFirstName(reservationDTO.getFirstName());
        existingReservation.setSecondName(reservationDTO.getSecondName());
        existingReservation.setUserEmail(reservationDTO.getUserEmail());
        existingReservation.setTelephoneNumber(reservationDTO.getTelephoneNumber());
        existingReservation.setNumberOfBookedEntries(reservationDTO.getNumberOfBookedEntries());
        existingReservation.setTrainingPassedOrDeleted(reservationDTO.isTrainingPassedOrDeleted());

        // Uložíme upravenou rezervaci
        ReservationEntity updatedReservation = reservationRepository.save(existingReservation);

        // Vrátíme upravený DTO
        return reservationMapper.toDTO(updatedReservation);
    }

    @Override
//    @CacheEvict(value = "trainingsByMonth", allEntries = true)
    public void deleteReservation(Long reservationId) {
        int removed = reservationRepository.deleteByReservationIdJPQL(reservationId);
        if (removed == 0) {
            throw new EntityNotFoundException("Rezervace s ID " + reservationId + " nenalezena.");
        }
    }
}
