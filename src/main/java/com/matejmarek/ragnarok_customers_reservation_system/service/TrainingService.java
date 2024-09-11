package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface TrainingService {

    TrainingDTO createTraining(TrainingDTO trainingDTO);

    Page<TrainingEntity> getTrainingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    TrainingDTO getOneTrainingById(Long trainingId);

    void removeOneTraining(Long trainingId);

    void removeAllPlannedTrainings(Long trainingId);
}
