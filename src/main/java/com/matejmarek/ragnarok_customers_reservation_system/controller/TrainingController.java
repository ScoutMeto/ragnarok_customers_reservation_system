package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingResponseDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import com.matejmarek.ragnarok_customers_reservation_system.service.TrainingService;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Api
@RestController
public class TrainingController {

    @Autowired
    TrainingService trainingService;
    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    AdminService adminService;
    @Autowired
    TrainingMapper trainingMapper;


    @PostMapping({"api/createNewTraining/", "api/createNewTraining"})
    public TrainingDTO addTraining (@RequestBody @Valid TrainingDTO trainingDTO) {
        System.out.println("Požadavek na vytvoření nového tréninku (TrainingController, addTraining): " + trainingDTO);
        return trainingService.createTraining(trainingDTO);
    }

//    @GetMapping({"api/loadAllTrainings/", "api/loadAllTrainings"})
//    public Page<TrainingEntity> getAllTrainingsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm") LocalDateTime startDate, Pageable pageable) {
//        System.out.println("Požadavek na načtení všech tréninků (TrainingController, getAllTrainingsForWeek");
//
//        // Primary date setup at a first page load.
//        if (startDate == null) {
//            startDate = LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
//        }
//
//        LocalDateTime endDate = startDate.plusDays(6);
//
//        /*
//        Přidat metodu, která nalézá všechny rezervace podle ID a vkládá je do Listu ke každé TrainingEntity
//        na Page (použito pro přehled: kdo je přihlášen na trénink, kolik míst je obsazených)
//         -vyřešeno načítáním EAGER
//         */
//        return trainingService.getTrainingsByDateRange(startDate, endDate, pageable);
//    }

//    @GetMapping({"api/loadAllTrainings/", "api/loadAllTrainings"})
//    public TrainingResponseDTO getAllTrainingsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm") LocalDateTime startDate, Pageable pageable) {
//        System.out.println("Požadavek na načtení všech tréninků (TrainingController, getAllTrainingsForWeek)");
//
//        // Primary date setup at a first page load.
//        if (startDate == null) {
//            startDate = LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
//        }
//
//        LocalDateTime endDate = startDate.plusDays(6);
//
//        Page<TrainingEntity> page = trainingService.getTrainingsByDateRange(startDate, endDate, pageable);
//
//        List<TrainingDTO> dtoList = page.getContent().stream()
//                .map(trainingMapper::toDTO)
//                .toList();
//
//        TrainingResponseDTO response = new TrainingResponseDTO();
//        response.setContent(dtoList);
//        response.setPageNumber(page.getNumber());
//        response.setTotalPages(page.getTotalPages());
//        response.setTotalElements(page.getTotalElements());
//
//        return response;    }

    //nahrazení metody výše
    @GetMapping({"api/loadAllTrainings/", "api/loadAllTrainings"})
    public List<TrainingResponseDTO> getTrainingsForCalendar(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        System.out.println("Backend DEBUG: start=" + start + ", end=" + end);
        return trainingService.getAllTrainingsAsCalendarEvents(start, end);
    }

    @GetMapping({"api/loadOneTraining/{id}/", "api/loadOneTraining/{id}"})
    public TrainingDTO getOneTraining(@PathVariable("id") Long trainingId) {
        System.out.println("Požadavek na načtení jednoho vybraného tréninků (TrainingController, getOneTraining");

        /*
        Přidat metodu, která nalézá všechny rezervace podle ID a vkládá je do Listu k TrainingEntity
        (použito pro přehled: kdo je přihlášen na trénink, kolik míst je obsazených)
         -vyřešeno načítáním EAGER
         */
        return trainingService.getOneTrainingById(trainingId);

    }

    @DeleteMapping({"api/deleteTrainingChosenInOverview/{id}/", "api/deleteTrainingChosenInOverview/{id}"})
    public ResponseEntity<Void> removeTraining(@PathVariable("id") Long trainingId) {
        System.out.println("Požadavek na odstranění zvoleného tréninku podle načteného ID: " + trainingId + ". Spolu s tréninkem dojde ke smazání všech rezervací.");
        trainingService.removeOneTraining(trainingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping({"api/deleteTrainingsChosenInOverview/{id}/", "api/deleteTrainingsChosenInOverview/{id}"})
    public ResponseEntity<Void> removeTrainings(@PathVariable("id") Long trainingId) {
        System.out.println("Požadavek na odstranění zvoleného tréninku podle načteného ID: " + trainingId + " a všech následujících, které splňují podmínky (shodný název, stejný čas, opakování po týdnu). Spolu s tréninkem dojde ke smazání všech rezervací.");
        trainingService.removeAllPlannedTrainings(trainingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping({"api/editTrainingChosenInOverview/{id}/", "api/editTrainingChosenInOverview/{id}"})
    public TrainingDTO editTraining (@PathVariable("id") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        System.out.println("Požadavek na úpravu zvoleného tréninku podle načteného ID: " + trainingId + ". Spolu s tréninkem dojde ke smazání všech rezervací.");
        return trainingService.editOneTraining(trainingId, trainingDTO);
    }

    // PUT metoda pro úpravu všech následujících tréninků, včetně aktuálně vybraného
    @PutMapping({"api/editAllPlanned/{id}/", "api/editAllPlanned/{id}"})
    public ResponseEntity<Void> editAllPlannedTrainings(@PathVariable("id") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        trainingService.editAllPlannedTrainings(trainingId, trainingDTO);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content pokud úprava proběhne úspěšně
    }


}
