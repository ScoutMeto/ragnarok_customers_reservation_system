package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingResponseDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainingService {

    TrainingDTO createTraining(TrainingDTO trainingDTO);

    //Page<TrainingEntity> getTrainingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // (vyřešeno)Zisk údajů pro proměnnou List<ReservationEntity> reservationsList (každá jednotka) - vyřešeno pomocí fetch.EAGER
    List<TrainingResponseDTO> getAllTrainingsAsCalendarEvents(LocalDateTime startTraining, LocalDateTime endTraining);

    TrainingDTO getOneTrainingById(Long trainingId);

    void removeOneTraining(Long trainingId);

    void removeAllPlannedTrainings(Long trainingId);

    TrainingDTO editOneTraining(Long trainingId, TrainingDTO trainingDTO);

    void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO);
}
