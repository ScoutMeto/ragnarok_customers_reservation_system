package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public interface TrainingService {

    TrainingDTO createTraining(TrainingDTO trainingDTO);
}
