package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.ReservationMapper;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TrainingMapper trainingMapper;
    @Autowired
    ReservationMapper reservationMapper;


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

    // (vyřešeno)Zisk údajů pro proměnnou List<ReservationEntity> reservationsList (každá jednotka) - vyřešeno pomocí fetch.EAGER
    @Override
    public Page<TrainingEntity> getTrainingsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return trainingRepository.findByDateBetweenMondaySunday(startDate, endDate, pageable);
    }

    // Přípravná metoda to delete/edit - zobrazí trénink pro úpravu po kliknutí na přehled v celém týdnu a umožní vybrat z možností: vymzat/upravit (při volbě předá ID další funkci)
    // (vyřešeno)Zisk údajů pro proměnnou List<ReservationEntity> reservationsList (každá jednotka) - vyřešeno pomocí fetch.EAGER
    @Override
    public TrainingDTO getOneTrainingById(Long trainingId) {
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného id " + trainingId + " nenalezen v databázi."));
        System.out.println("Trénink s ID: " + trainingId + " načten.");
        return trainingMapper.toDTO(trainingEntity);
    }

    // Remove one training adn all reservations for it.
    // Extension of method "getOneTrainingById"
    @Transactional
    @Override
    public void removeOneTraining(Long trainingId) {
        // Najdi trénink podle ID
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        // Odstraní trénink (kaskádově i všechny jeho rezervace)
        trainingRepository.delete(trainingEntity);
        System.out.println("Trénink ID: " + trainingId + " a jeho rezervace byly vymazány.");
    }

    // Remove all planned trainings after this one.
    // Extension of method "getOneTrainingById"
            /*vymaže zvolený trénink a všechny následující tréninkové jednotky, které:
        -se nacházejí ve stejném čase
        -o týden později
        -mají stejný název
        -vymaže také případné existující rezervace

         */
    @Transactional
    @Override
    public void removeAllPlannedTrainings(Long trainingId) {
        // Najdi výchozí trénink podle ID
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        // Získání údajů z tréninku pro identifikaci opakujících se lekcí
        String lessonName = trainingEntity.getNameOfLesson();
        LocalDateTime lessonDate = trainingEntity.getDateOfCurrentLesson();
        Time startTime = trainingEntity.getStartOfCurrentLesson();
        Time endTime = trainingEntity.getEndOfCurrentLesson();

        // Mazání všech tréninků od daného data včetně, které splňují podmínky (shodný název, stejný čas, opakování po týdnu)
        List<TrainingEntity> plannedTrainings = trainingRepository.findAllByNameOfLessonAndStartOfCurrentLessonAndEndOfCurrentLessonAndDateOfCurrentLessonAfter(
                lessonName, startTime, endTime, lessonDate.minusSeconds(1));  // datum včetně

        for (TrainingEntity training : plannedTrainings) {
            trainingRepository.delete(training);
            System.out.println("Trénink ID: " + training.getTrainingId() + " a jeho rezervace byly vymazány.");
        }
    }

    //edit - one training
    @Transactional
    @Override
    public TrainingDTO editOneTraining(Long trainingId, TrainingDTO trainingDTO) {
        //TrainingEntity trainingEntity = trainingRepository.findById(trainingId);
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        // Přepsání hodnot na základě DTO
        trainingEntity.setNameOfLesson(trainingDTO.getNameOfLesson());
        trainingEntity.setNumberOfReservations(trainingDTO.getNumberOfReservations());
        trainingEntity.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        trainingEntity.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
        trainingEntity.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        trainingEntity.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
        trainingEntity.setRepeatIntervalInDays(trainingDTO.getRepeatIntervalInDays());
        trainingEntity.setNumberOfCopyConcreteTraining(trainingDTO.getNumberOfCopyConcreteTraining());

        // Aktualizace seznamu rezervací
        List<ReservationDTO> reservationDTOs = trainingDTO.getReservations();
        List<ReservationEntity> reservationEntities = reservationMapper.toReservationEntities(reservationDTOs);
        trainingEntity.setReservationsList(reservationEntities);

        // Uložení změněného tréninku zpět do DB
        trainingRepository.save(trainingEntity);

        return trainingMapper.toDTO(trainingEntity);

    }


    //edit - all nexts trainings
    @Transactional
    @Override
    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
        // Získání zadaného tréninku
        TrainingEntity initialTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        // Nalezení všech následujících tréninků, které mají stejný název a čas
        List<TrainingEntity> trainingsToEdit = trainingRepository.findByNameOfLessonAndStartOfCurrentLessonAndDateOfCurrentLessonGreaterThanEqual(
                initialTraining.getNameOfLesson(),
                initialTraining.getStartOfCurrentLesson(),
                initialTraining.getDateOfCurrentLesson()
        );

        // Pro každý z nalezených tréninků aktualizujeme hodnoty na základě DTO
        for (TrainingEntity training : trainingsToEdit) {
            training.setNameOfLesson(trainingDTO.getNameOfLesson());
            training.setNumberOfReservations(trainingDTO.getNumberOfReservations());
            training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
            training.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
            training.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
            training.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
            training.setRepeatIntervalInDays(trainingDTO.getRepeatIntervalInDays());
            training.setNumberOfCopyConcreteTraining(trainingDTO.getNumberOfCopyConcreteTraining());

            // Zachování existujících rezervací
            List<ReservationEntity> existingReservations = training.getReservationsList();
            List<ReservationDTO> updatedReservationsDTOs = trainingDTO.getReservations();
            List<ReservationEntity> updatedReservationsEntities = mergeReservations(existingReservations, updatedReservationsDTOs);
            training.setReservationsList(updatedReservationsEntities);

            // Uložení upraveného tréninku
            trainingRepository.save(training);
        }
    }

    // Pomocná metoda pro zachování existujících rezervací
    private List<ReservationEntity> mergeReservations(List<ReservationEntity> existingReservations, List<ReservationDTO> updatedReservationsDTOs) {
        // Pokud nejsou žádné nové rezervace, vrátí existující
        if (updatedReservationsDTOs == null || updatedReservationsDTOs.isEmpty()) {
            return existingReservations;
        }

        // Pokud existují nové rezervace, přidá je k existujícím
        List<ReservationEntity> mergedReservations = new ArrayList<>(existingReservations);

        for (ReservationDTO reservationDTO : updatedReservationsDTOs) {
            ReservationEntity reservationEntity = reservationMapper.toEntity(reservationDTO);
            mergedReservations.add(reservationEntity);
        }

        return mergedReservations;
    }



}


