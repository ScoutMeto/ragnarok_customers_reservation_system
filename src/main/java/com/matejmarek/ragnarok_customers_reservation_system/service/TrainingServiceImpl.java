package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TrainingMapper trainingMapper;


    // Create new training and repeat in interval
    //funkce pro vytvoření tréninku využívající další funkce pro přidání opakování tréninků
    @Override
    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {

        TrainingEntity dtoToEntity = trainingMapper.toEntity(trainingDTO);
        TrainingEntity savedEntity = trainingRepository.save(dtoToEntity);

        // Automatické vytvoření dalších kopií
        if (trainingDTO.getRepeatIntervalInDays() > 0) {
            createRepeatedLessons(trainingDTO, trainingDTO.getRepeatIntervalInDays());
        }

        System.out.println("Trénink uložen: " + trainingDTO + ". Zároveň bylo vytvořeno " + trainingDTO.getNumberOfCopyConcreteTraining() + " kopií vzájemně vzdálených " + trainingDTO.getRepeatIntervalInDays() + " dnů.");

        return trainingDTO;
    }

    // Funkce pro přidání opakovaných tréninků jako kopie zadaného tréninku
    private void createRepeatedLessons(TrainingDTO modelLesson, int repeatIntervalInDays) {
        LocalDateTime nextDate = modelLesson.getDateOfCurrentLesson().plusDays(repeatIntervalInDays);
        for (int i = 0; i < modelLesson.getNumberOfCopyConcreteTraining(); i++) {
            TrainingDTO repeatedLesson = new TrainingDTO();
            repeatedLesson.setNameOfLesson(modelLesson.getNameOfLesson());
            repeatedLesson.setNumberOfFreeSlots(modelLesson.getNumberOfFreeSlots());
            repeatedLesson.setDateOfCurrentLesson(nextDate);
            repeatedLesson.setStartOfCurrentLesson(modelLesson.getStartOfCurrentLesson());
            repeatedLesson.setEndOfCurrentLesson(modelLesson.getEndOfCurrentLesson());
            repeatedLesson.setRepeatIntervalInDays(repeatIntervalInDays);

            TrainingEntity repeatedDtoToEntity = trainingMapper.toEntity(repeatedLesson);
            TrainingEntity savedRepeatedEntity = trainingRepository.save(repeatedDtoToEntity);

            nextDate = nextDate.plusDays(repeatIntervalInDays);
        }
    }





    // Load existing training(s)
}
