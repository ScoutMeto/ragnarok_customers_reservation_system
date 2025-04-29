package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingResponseDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import com.matejmarek.ragnarok_customers_reservation_system.service.TrainingService;
import io.swagger.annotations.Api;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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



    @PostMapping({"api/createNewTraining/", "api/createNewTraining"})
    public TrainingDTO addTraining (@RequestBody @Valid TrainingDTO trainingDTO) {
        System.out.println("Požadavek na vytvoření nového tréninku (TrainingController, addTraining): " + trainingDTO);
        return trainingService.createTraining(trainingDTO);
    }


    @GetMapping({"api/loadAllTrainings/", "api/loadAllTrainings"})
    public List<TrainingResponseDTO> getTrainingsForCalendar(

            @RequestParam("start") OffsetDateTime start,
            @RequestParam("end") OffsetDateTime end) {

        LocalDateTime startDate = start.toLocalDateTime();
        LocalDateTime endDate = end.toLocalDateTime();


        System.out.println("Backend DEBUG: start=" + startDate + ", end=" + endDate);
        return trainingService.getAllTrainingsAsCalendarEvents(startDate, endDate);
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

    // PUT metoda pro úpravu jednoho vybraného tréninku
    @PutMapping({"api/editTrainingChosenInOverview/{trainingId}/", "api/editTrainingChosenInOverview/{trainingId}"})
    public TrainingDTO editTraining (@PathVariable("trainingId") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        System.out.println("Požadavek na úpravu zvoleného tréninku podle načteného ID: " + trainingId + ". Nedojde ke smazání rezervací.");
        return trainingService.editOneTraining(trainingId, trainingDTO);
    }

    // PUT metoda pro úpravu všech následujících tréninků, včetně aktuálně vybraného
    @PutMapping({"api/editAllPlanned/{trainingId}/", "api/editAllPlanned/{trainingId}"})
    public ResponseEntity<Void> editAllPlannedTrainings(@PathVariable("trainingId") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        System.out.println("Požadavek na úpravu zvoleného tréninku a všech následujících (v odpovídajícím čase, s totožným názvem, o týden později) podle načteného ID: " + trainingId + ". Nedojde ke smazání rezervací.");
        trainingService.editAllPlannedTrainings(trainingId, trainingDTO);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content pokud úprava proběhne úspěšně
    }


}
