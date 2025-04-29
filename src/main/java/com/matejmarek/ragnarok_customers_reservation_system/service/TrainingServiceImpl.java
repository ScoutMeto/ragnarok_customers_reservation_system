package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingResponseDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.ReservationMapper;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    TrainingRepository trainingRepository;

    @Autowired
    ReservationMapper reservationMapper;

    @Autowired
    TrainingMapper trainingMapper;


    // Create new training and repeat in an interval (interval is 7 days - so repeating the chosen training lesson next week).
//    @Override
//    @Transactional
//    public TrainingDTO createTraining(TrainingDTO trainingDTO) {
//
//        TrainingEntity dtoToEntity = trainingMapper.toEntity(trainingDTO);
//        TrainingEntity savedEntity = trainingRepository.save(dtoToEntity);
//
//        // Admin set the number of copies higher than 1.
//        if (trainingDTO.getNumberOfCopyConcreteTraining() > 1) {
//            createRepeatedLessons(trainingDTO, trainingDTO.getRepeatIntervalInDays(), savedEntity.getTrainingId());
//        }
//
//        System.out.println("Trénink uložen: " + trainingDTO + ". Zároveň bylo vytvořeno " + trainingDTO.getNumberOfCopyConcreteTraining() + " kopií vzájemně vzdálených " + trainingDTO.getRepeatIntervalInDays() + " dnů.");
//
//        return trainingDTO;
//    }

    @Override
    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {
        // Ruční vytvoření nové entitní instance
        TrainingEntity trainingEntity = new TrainingEntity();

        trainingEntity.setNameOfLesson(trainingDTO.getNameOfLesson());
        trainingEntity.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        trainingEntity.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
        trainingEntity.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        trainingEntity.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
        trainingEntity.setRepeatIntervalInDays(trainingDTO.getRepeatIntervalInDays());
        trainingEntity.setCoachName(trainingDTO.getCoachName());
        trainingEntity.setNumberOfCopyConcreteTraining(trainingDTO.getNumberOfCopyConcreteTraining());

        // ParentId se nenastavuje při první instanci
        trainingEntity.setParentTrainingId(null);

        TrainingEntity savedEntity = trainingRepository.save(trainingEntity);
        Long parentId = savedEntity.getTrainingId();

        // Vytvoření kopií pokud je počet větší než 1
        if (trainingDTO.getNumberOfCopyConcreteTraining() > 1) {
            createRepeatedLessons(trainingDTO, trainingDTO.getRepeatIntervalInDays(), parentId);
        }

        System.out.println("Trénink uložen: " + trainingDTO + ". Zároveň bylo vytvořeno " + trainingDTO.getNumberOfCopyConcreteTraining() + " kopií vzájemně vzdálených " + trainingDTO.getRepeatIntervalInDays() + " dnů.");

        return trainingMapper.toDTO(savedEntity); // Volitelně: můžeš i ručně vytvořit TrainingDTO pokud mapper blbne
    }

    // Repeat lessons method.
    private void createRepeatedLessons(TrainingDTO modelLesson, int repeatIntervalInDays, Long parentId) {
        LocalDateTime nextDate = modelLesson.getDateOfCurrentLesson().plusDays(repeatIntervalInDays);
        int remainNumberOfCopy = modelLesson.getNumberOfCopyConcreteTraining() - 1;

        for (int i = 1; i < modelLesson.getNumberOfCopyConcreteTraining(); i++) {
            TrainingEntity repeatedEntity = new TrainingEntity();

            repeatedEntity.setNameOfLesson(modelLesson.getNameOfLesson());
            repeatedEntity.setNumberOfFreeSlots(modelLesson.getNumberOfFreeSlots());

            repeatedEntity.setDateOfCurrentLesson(nextDate);
            repeatedEntity.setStartOfCurrentLesson(nextDate);
            repeatedEntity.setEndOfCurrentLesson(
                    nextDate.plusHours(
                            modelLesson.getEndOfCurrentLesson().getHour() - modelLesson.getStartOfCurrentLesson().getHour() + 1
                    )
            );

            repeatedEntity.setRepeatIntervalInDays(repeatIntervalInDays);
            repeatedEntity.setCoachName(modelLesson.getCoachName());

            repeatedEntity.setNumberOfCopyConcreteTraining(remainNumberOfCopy);
            repeatedEntity.setParentTrainingId(parentId);

            trainingRepository.save(repeatedEntity);

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
                    extraReservations += reservationDTO.getNumberOfBookedEntries() - 1;
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
        TrainingEntity clickedTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink nenalezen."));

        Long parentTrainingId = clickedTraining.getParentTrainingId() != null
                ? clickedTraining.getParentTrainingId()
                : clickedTraining.getTrainingId(); // Pokud klikneme na originál, použijeme jeho ID

        LocalDateTime startOfCurrentLesson = clickedTraining.getStartOfCurrentLesson();

        List<TrainingEntity> toDelete = trainingRepository
                .findByParentTrainingIdAndStartOfCurrentLessonGreaterThanEqual(parentTrainingId, startOfCurrentLesson);

        trainingRepository.deleteAll(toDelete);

        // Smaž i právě kliknutý trénink
        trainingRepository.delete(clickedTraining);
    }

    //edit - one training
    @Transactional
    @Override
    public TrainingDTO editOneTraining(Long trainingId, TrainingDTO trainingDTO) {
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));

        boolean hasChanged = !Objects.equals(trainingEntity.getNameOfLesson(), trainingDTO.getNameOfLesson()) ||
                !Objects.equals(trainingEntity.getStartOfCurrentLesson(), trainingDTO.getStartOfCurrentLesson()) ||
                !Objects.equals(trainingEntity.getEndOfCurrentLesson(), trainingDTO.getEndOfCurrentLesson());

        if (hasChanged) {
            trainingEntity.setParentTrainingId(null);
        }

        trainingEntity.setNameOfLesson(trainingDTO.getNameOfLesson());
        trainingEntity.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        trainingEntity.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
        trainingEntity.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        trainingEntity.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
        trainingEntity.setRepeatIntervalInDays(trainingDTO.getRepeatIntervalInDays());
        trainingEntity.setCoachName(trainingDTO.getCoachName());
        trainingEntity.setNumberOfCopyConcreteTraining(trainingDTO.getNumberOfCopyConcreteTraining());

        trainingRepository.save(trainingEntity);
        return trainingMapper.toDTO(trainingEntity);
    }


    //edit - all next's trainings
    @Transactional
    @Override
    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
        TrainingEntity clickedTraining = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Trénink nenalezen."));

        Long parentTrainingId = clickedTraining.getParentTrainingId() != null
                ? clickedTraining.getParentTrainingId()
                : clickedTraining.getTrainingId();

        LocalDateTime startFrom = clickedTraining.getStartOfCurrentLesson();
        List<TrainingEntity> toEdit = trainingRepository
                .findByParentTrainingIdAndStartOfCurrentLessonGreaterThanEqual(parentTrainingId, startFrom);

        int interval = trainingDTO.getRepeatIntervalInDays();

        boolean hasChanged = !Objects.equals(clickedTraining.getNameOfLesson(), trainingDTO.getNameOfLesson()) ||
                !Objects.equals(clickedTraining.getStartOfCurrentLesson(), trainingDTO.getStartOfCurrentLesson()) ||
                !Objects.equals(clickedTraining.getEndOfCurrentLesson(), trainingDTO.getEndOfCurrentLesson());

        if (hasChanged) {
            clickedTraining.setParentTrainingId(null);
            trainingRepository.save(clickedTraining);

            for (int i = 0; i < toEdit.size(); i++) {
                TrainingEntity training = toEdit.get(i);
                training.setParentTrainingId(clickedTraining.getTrainingId());

                training.setNameOfLesson(trainingDTO.getNameOfLesson());
                training.setCoachName(trainingDTO.getCoachName());
                training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());

                training.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson().plusDays((i + 1) * interval));
                training.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson().plusDays((i + 1) * interval));
                training.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson().plusDays((i + 1) * interval));

                trainingRepository.save(training);
            }
        } else {
            for (int i = 0; i < toEdit.size(); i++) {
                TrainingEntity training = toEdit.get(i);

                training.setNameOfLesson(trainingDTO.getNameOfLesson());
                training.setCoachName(trainingDTO.getCoachName());
                training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());

                training.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson().plusDays((i + 1) * interval));
                training.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson().plusDays((i + 1) * interval));
                training.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson().plusDays((i + 1) * interval));

                trainingRepository.save(training);
            }
        }

        clickedTraining.setNameOfLesson(trainingDTO.getNameOfLesson());
        clickedTraining.setCoachName(trainingDTO.getCoachName());
        clickedTraining.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
        clickedTraining.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
        clickedTraining.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
        clickedTraining.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());

        trainingRepository.save(clickedTraining);
    }
}
//    @Transactional
//    @Override
//    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
//        TrainingEntity clickedTraining = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new EntityNotFoundException("Trénink nenalezen."));
//
//        Long parentTrainingId = clickedTraining.getParentTrainingId() != null
//                ? clickedTraining.getParentTrainingId()
//                : clickedTraining.getTrainingId();
//
//        LocalDateTime startFrom = clickedTraining.getStartOfCurrentLesson();
//
//        List<TrainingEntity> toEdit = trainingRepository
//                .findByParentTrainingIdAndStartOfCurrentLessonGreaterThanEqual(parentTrainingId, startFrom);
//
//        int interval = trainingDTO.getRepeatIntervalInDays();
//
//        for (int i = 0; i < toEdit.size(); i++) {
//            TrainingEntity training = toEdit.get(i);
//
//            training.setNameOfLesson(trainingDTO.getNameOfLesson());
//            training.setCoachName(trainingDTO.getCoachName());
//            training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//
//            training.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson().plusDays(i * interval));
//            training.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson().plusDays(i * interval));
//            training.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson().plusDays(i * interval));
//
//            trainingRepository.save(training);
//        }
//
//        // Upravíme i kliknutý trénink (první v sérii)
//        clickedTraining.setNameOfLesson(trainingDTO.getNameOfLesson());
//        clickedTraining.setCoachName(trainingDTO.getCoachName());
//        clickedTraining.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//        clickedTraining.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
//        clickedTraining.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
//        clickedTraining.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
//
//        trainingRepository.save(clickedTraining);
//    }
//}
//    @Transactional
//    @Override
//    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
//        TrainingEntity initialTraining = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new EntityNotFoundException("Trénink podle zadaného ID " + trainingId + " nenalezen."));
//
//        String nameOfLesson = initialTraining.getNameOfLesson();
//        LocalDateTime startOfCurrentLesson = initialTraining.getStartOfCurrentLesson();
//
//        List<TrainingEntity> plannedTrainings = trainingRepository.findAllByNameOfLessonAndStartOfCurrentLesson(
//                nameOfLesson, startOfCurrentLesson.plusDays(7));
//
//        for (int i = 0; i < plannedTrainings.size(); i++) {
//            TrainingEntity trainingEntity = plannedTrainings.get(i);
//
//            trainingEntity.setNameOfLesson(trainingDTO.getNameOfLesson());
//            trainingEntity.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//            trainingEntity.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson().plusDays(trainingDTO.getRepeatIntervalInDays() * i));
//            trainingEntity.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson().plusDays(trainingDTO.getRepeatIntervalInDays() * i));
//            trainingEntity.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson().plusDays(trainingDTO.getRepeatIntervalInDays() * i));
//            trainingEntity.setCoachName(trainingDTO.getCoachName());
//
//            trainingRepository.save(trainingEntity);
//        }
//
//        // Uložení prvního (upraveného) tréninku
//        initialTraining.setNameOfLesson(trainingDTO.getNameOfLesson());
//        initialTraining.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//        initialTraining.setDateOfCurrentLesson(trainingDTO.getDateOfCurrentLesson());
//        initialTraining.setStartOfCurrentLesson(trainingDTO.getStartOfCurrentLesson());
//        initialTraining.setEndOfCurrentLesson(trainingDTO.getEndOfCurrentLesson());
//        initialTraining.setCoachName(trainingDTO.getCoachName());
//
//        trainingRepository.save(initialTraining);
//    }
//}



//    @Transactional
//    @Override
//    public void editAllPlannedTrainings(Long trainingId, TrainingDTO trainingDTO) {
//        TrainingEntity initialTraining = trainingRepository.findById(trainingId)
//                .orElseThrow(() -> new EntityNotFoundException("Trénink nenalezen."));
//
//        int repeatInterval = trainingDTO.getRepeatIntervalInDays(); // 7
//        int numberOfRepeats = trainingDTO.getNumberOfCopyConcreteTraining(); // kolik se má iterovat
//
//        LocalDateTime originalStart = initialTraining.getStartOfCurrentLesson();
//
//        // Vycházíme z nových hodnot, které uživatel zadal
//        LocalDateTime newStart = trainingDTO.getStartOfCurrentLesson();
//        LocalDateTime newEnd = trainingDTO.getEndOfCurrentLesson();
//        LocalDateTime newDate = trainingDTO.getDateOfCurrentLesson();
//
//        for (int i = 1; i <= numberOfRepeats; i++) {
//            LocalDateTime startToFind = originalStart.plusDays(i * repeatInterval);
//
//            Optional<TrainingEntity> optionalTraining = trainingRepository
//                    .findByNameOfLessonAndStartOfCurrentLesson(initialTraining.getNameOfLesson(), startToFind);
//
//            if (optionalTraining.isPresent()) {
//                TrainingEntity training = optionalTraining.get();
//
//                training.setNameOfLesson(trainingDTO.getNameOfLesson());
//                training.setCoachName(trainingDTO.getCoachName());
//                training.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//
//                // Posun data podle i-tého týdne vůči novému základu
//                training.setStartOfCurrentLesson(newStart.plusDays(i * repeatInterval));
//                training.setEndOfCurrentLesson(newEnd.plusDays(i * repeatInterval));
//                training.setDateOfCurrentLesson(newDate.plusDays(i * repeatInterval));
//
//                trainingRepository.save(training);
//            } else {
//                System.out.println("Upozornění: Trénink pro týden " + i + " nebyl nalezen.");
//            }
//        }
//
//        // Uložíme i první trénink (aktuálně upravený)
//        initialTraining.setNameOfLesson(trainingDTO.getNameOfLesson());
//        initialTraining.setCoachName(trainingDTO.getCoachName());
//        initialTraining.setNumberOfFreeSlots(trainingDTO.getNumberOfFreeSlots());
//        initialTraining.setStartOfCurrentLesson(newStart);
//        initialTraining.setEndOfCurrentLesson(newEnd);
//        initialTraining.setDateOfCurrentLesson(newStart);
//
//        trainingRepository.save(initialTraining);
//    }
//}







