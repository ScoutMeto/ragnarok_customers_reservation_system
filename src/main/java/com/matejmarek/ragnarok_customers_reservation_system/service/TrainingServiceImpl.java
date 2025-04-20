package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingResponseDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.ReservationMapper;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapperImpl;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TrainingMapperImpl trainingMapper;
    @Autowired
    ReservationMapper reservationMapper;


    // Create new training and repeat in an interval (interval is 7 days - so repeating the chosen training lesson next week).
    @Override
    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {

        TrainingEntity dtoToEntity = trainingMapper.toEntity(trainingDTO);
        TrainingEntity savedEntity = trainingRepository.save(dtoToEntity);

        // Admin set the number of copies higher than 1.
        if (trainingDTO.getNumberOfCopyConcreteTraining() > 1) {
            createRepeatedLessons(trainingDTO, trainingDTO.getRepeatIntervalInDays());
        }

        System.out.println("Trénink uložen: " + trainingDTO + ". Zároveň bylo vytvořeno " + trainingDTO.getNumberOfCopyConcreteTraining() + " kopií vzájemně vzdálených " + trainingDTO.getRepeatIntervalInDays() + " dnů.");

        return trainingDTO;
    }

    // Repeat lessons method.
    private void createRepeatedLessons(TrainingDTO modelLesson, int repeatIntervalInDays) {
        LocalDateTime nextDate = modelLesson.getDateOfCurrentLesson().plusDays(repeatIntervalInDays);
        int remainNumberOfCopy = modelLesson.getNumberOfCopyConcreteTraining()-1;
        for (int i = 0; i < modelLesson.getNumberOfCopyConcreteTraining(); i++) {
            TrainingDTO repeatedLesson = new TrainingDTO();
            repeatedLesson.setNameOfLesson(modelLesson.getNameOfLesson());
            repeatedLesson.setNumberOfFreeSlots(modelLesson.getNumberOfFreeSlots());
            repeatedLesson.setDateOfCurrentLesson(nextDate);
            repeatedLesson.setStartOfCurrentLesson(nextDate);
            repeatedLesson.setEndOfCurrentLesson(nextDate.plusHours(modelLesson.getEndOfCurrentLesson().getHour() - modelLesson.getStartOfCurrentLesson().getHour() + 1));
            repeatedLesson.setRepeatIntervalInDays(repeatIntervalInDays);
            repeatedLesson.setCoachName(modelLesson.getCoachName());
            repeatedLesson.setNumberOfCopyConcreteTraining(remainNumberOfCopy);

            TrainingEntity repeatedDtoToEntity = trainingMapper.toEntity(repeatedLesson);
            TrainingEntity savedRepeatedEntity = trainingRepository.save(repeatedDtoToEntity);

            nextDate = nextDate.plusDays(repeatIntervalInDays);
            remainNumberOfCopy--;
        }
    }

    // (vyřešeno)Zisk údajů pro proměnnou List<ReservationEntity> reservationsList (každá jednotka) - vyřešeno pomocí fetch.EAGER
//    @Override
//    public Page<TrainingEntity> getTrainingsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
//        System.out.println("DEBUG (Service): Hledám tréninky od " + startDate + " do " + endDate);
//        return trainingRepository.findByDateOfCurrentLessonBetween(startDate, endDate, pageable);
//    }

    //upravena metoda výše
    @Override
    public List<TrainingResponseDTO> getAllTrainingsAsCalendarEvents(LocalDateTime startDate, LocalDateTime endDate) {
        List<TrainingEntity> trainings = trainingRepository.findByDateOfCurrentLessonBetween(startDate, endDate);

        return trainings.stream().map(training -> {
            TrainingResponseDTO dto = new TrainingResponseDTO();
            dto.setTrainingId(training.getTrainingId());
            dto.setTitle(training.getNameOfLesson());
            dto.setStart(training.getStartOfCurrentLesson());
            dto.setEnd(training.getEndOfCurrentLesson());

            //načtení rezervací a uložení do DTOs
            List<ReservationDTO> reservationDTOs = training.getReservations().stream()
                    .map(reservationMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setReservations(reservationDTOs);

            // Kontrola počtu rezervovaných míst a jejich případné započítání do celkového počtu rezervací
            int extraReservations = 0;
            for (ReservationDTO reservationDTO : reservationDTOs) {
                int checkingNumberOfReservations = reservationDTO.getNumberOfBookedEntries();
                if (checkingNumberOfReservations > 1) {
                    extraReservations += reservationDTO.getNumberOfBookedEntries()-1;
                }
            }

            // ExtendedProps jako další data, která fullCalendar běžně nedistribuuje a proto musejí být odeslány takto
            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("lessonName", training.getNameOfLesson());
            extendedProps.put("coachName", training.getCoachName());
            extendedProps.put("numberOfFreeSlots", training.getNumberOfFreeSlots());
            extendedProps.put("numberOfReservations", ((training.getReservations().size())) + extraReservations);
            extendedProps.put("reservations", reservationDTOs);
            extendedProps.put("trainingId", training.getTrainingId());


            dto.setExtendedProps(extendedProps);

            return dto;
        }).toList();
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

    // Remove one training and all reservations for it.
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
        String nameOfLesson = trainingEntity.getNameOfLesson();
        LocalDateTime startOfCurrentLesson = trainingEntity.getStartOfCurrentLesson();

        // Mazání všech tréninků od daného data včetně, které splňují podmínky (shodný název, stejný čas, opakování po týdnu)
        List<TrainingEntity> plannedTrainings = trainingRepository.findAllByNameOfLessonAndStartOfCurrentLesson(
                nameOfLesson, startOfCurrentLesson.plusDays(7));  // stejný název, stejný čas začátku, o týden později

        for (TrainingEntity training : plannedTrainings) {
            trainingRepository.delete(training);
            System.out.println("Trénink ID: " + training.getTrainingId() + " a jeho rezervace byly vymazány.");
        }

        trainingRepository.delete(trainingEntity);
    }

    //edit - one training
    @Transactional
    @Override
    public TrainingDTO editOneTraining(Long trainingId, TrainingDTO trainingDTO) {
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        // Přepsání hodnot na základě DTO, zbytek hodnot z databáze (např. seznam rezervací) nedotčených
        trainingEntity.setTrainingId(trainingId);
        trainingEntity.setNameOfLesson(trainingDTO.getNameOfLesson());
        trainingEntity.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        trainingEntity.setDateOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        trainingEntity.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        trainingEntity.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
        trainingEntity.setRepeatIntervalInDays(trainingDTO.getRepeatIntervalInDays());
        trainingEntity.setCoachName(trainingDTO.getCoachName());
        trainingEntity.setNumberOfCopyConcreteTraining(trainingDTO.getNumberOfCopyConcreteTraining());

        // Uložení změněného tréninku zpět do DB
        trainingRepository.save(trainingEntity);

        return trainingMapper.toDTO(trainingEntity);
    }


    //edit - all next's trainings
//    @Transactional
//    @Override
//    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
//        // Získání zadaného tréninku
//        TrainingEntity initialTraining = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));
//
//        // Nalezení všech následujících tréninků, které mají stejný název a čas
//        List<TrainingEntity> trainingsToEdit = trainingRepository.findAllByNameOfLessonAndStartOfCurrentLesson(
//                initialTraining.getNameOfLesson(),
//                initialTraining.getStartOfCurrentLesson().plusDays(7)
//        );
//
//        // Pro každý z nalezených tréninků aktualizujeme hodnoty na základě DTO
//        for (TrainingEntity training : trainingsToEdit) {
//            training.setNameOfLesson(trainingDTO.getNameOfLesson());
//            training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//            training.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
//            training.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
//            training.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
//            training.setCoachName(trainingDTO.getCoachName());
//
//
//            // Uložení upraveného tréninku
//            trainingRepository.save(training);
//        }
//        trainingRepository.save(initialTraining);
//    }
    @Transactional
    @Override
    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
        TrainingEntity initialTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink nenalezen."));

        int repeatInterval = trainingDTO.getRepeatIntervalInDays(); // 7
        int numberOfRepeats = trainingDTO.getNumberOfCopyConcreteTraining(); // kolik se má iterovat

        LocalDateTime originalStart = initialTraining.getStartOfCurrentLesson();
        LocalDateTime originalEnd = initialTraining.getEndOfCurrentLesson();
        LocalDateTime originalDate = initialTraining.getDateOfCurrentLesson();

        // Vycházíme z nových hodnot, které uživatel zadal
        LocalDateTime newStart = trainingDTO.getStartOfCurrentLesson();
        LocalDateTime newEnd = trainingDTO.getEndOfCurrentLesson();
        LocalDateTime newDate = trainingDTO.getDateOfCurrentLesson();

        for (int i = 1; i <= numberOfRepeats; i++) {
            LocalDateTime startToFind = originalStart.plusDays(i * repeatInterval);

            List<TrainingEntity> trainings = trainingRepository
                    .findByNameOfLessonAndStartOfCurrentLesson(initialTraining.getNameOfLesson(), startToFind);

            if (!trainings.isEmpty()) {
                // Očekáváme pouze jeden trénink v seznamu
                TrainingEntity training = trainings.get(0);

                training.setNameOfLesson(trainingDTO.getNameOfLesson());
                training.setCoachName(trainingDTO.getCoachName());
                training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());

                // Posun data podle i-tého týdne vůči novému základu
                training.setStartOfCurrentLesson(newStart.plusDays(i * repeatInterval));
                training.setEndOfCurrentLesson(newEnd.plusDays(i * repeatInterval));
                training.setDateOfCurrentLesson(newDate.plusDays(i * repeatInterval));

                trainingRepository.save(training);
            } else {
                System.out.println("Upozornění: Trénink pro týden " + i + " nebyl nalezen.");
            }
        }

        // Uložíme i první trénink (aktuálně upravený)
        initialTraining.setNameOfLesson(trainingDTO.getNameOfLesson());
        initialTraining.setCoachName(trainingDTO.getCoachName());
        initialTraining.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        initialTraining.setStartOfCurrentLesson(newStart);
        initialTraining.setEndOfCurrentLesson(newEnd);
        initialTraining.setDateOfCurrentLesson(newDate);

        trainingRepository.save(initialTraining);
    }




}


