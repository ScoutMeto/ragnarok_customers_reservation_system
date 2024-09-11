package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TrainingMapper trainingMapper;


    // Create new training and repeat in an interval (interval is 7 days - so repeating the chosen training lesson next week).
    @Override
    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {

        TrainingEntity dtoToEntity = trainingMapper.toEntity(trainingDTO);
        TrainingEntity savedEntity = trainingRepository.save(dtoToEntity);

        // Admin set the number of copies higher than 0.
        if (trainingDTO.getNumberOfCopyConcreteTraining() > 0) {
            createRepeatedLessons(trainingDTO, trainingDTO.getRepeatIntervalInDays());
        }

        System.out.println("Trénink uložen: " + trainingDTO + ". Zároveň bylo vytvořeno " + trainingDTO.getNumberOfCopyConcreteTraining() + " kopií vzájemně vzdálených " + trainingDTO.getRepeatIntervalInDays() + " dnů.");

        return trainingDTO;
    }

    // Repeat lessons method.
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

    // Je třeba funkci doplnit o: zisk údajů pro proměnnou List<ReservationEntity> reservationsList (každá jednotka)
    @Override
    public Page<TrainingEntity> getTrainingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return trainingRepository.findByDateBetweenMondaySunday(startDate, endDate);
    }

    // Přípravná metoda to delete/edit - zobrazí trénink pro úpravu po kliknutí na přehled v celém týdnu a umožní vybrat z možností: vymzat/upravit (při volbě předá ID další funkci)
    // Je třeba funkci doplnit o: zisk údajů pro proměnnou List<ReservationEntity> reservationsList
    @Override
    public TrainingDTO getOneTrainingById(Long trainingId) {
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného id " + trainingId + " nenalezen v databázi."));
        System.out.println("Trénink s ID: " + trainingId + " načten.");
        return trainingMapper.toDTO(trainingEntity);
    }

    // Remove one training.
    @Transactional
    @Override
    public void removeOneTraining(Long trainingId) {

        /*
        vymaže zvolený trénink a náležící rezervace
         */
    }

    // Remove all planned trainings after this one.
    @Transactional
    @Override
    public void removeAllPlannedTrainings(Long trainingId) {
        /*vymaže zvolený trénink a všechny následující tréninkové jednotky, které:
        -se nacházejí ve stejném čase
        -o týden později
        -mají stejný název

         */
    }

    //edit - one training
    //edit - all nexts trainings

}
