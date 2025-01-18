package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.mapper.TrainingMapper;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.TrainingRepository;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import com.matejmarek.ragnarok_customers_reservation_system.service.TrainingService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

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
    public Page<TrainingEntity> getAllTrainingsForWeek(@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDateTime startDate) {
        System.out.println("Požadavek na načtení všech tréninků (TrainingController, getAllTrainingsForWeek");

        // Primary date setup at a first page load.
        if (startDate == null) {
            startDate = LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        }

        LocalDateTime endDate = startDate.plusDays(6);

        /*
        Přidat metodu, která nalézá všechny rezervace podle ID a vkládá je do Listu ke každé TrainingEntity
        na Page (použito pro přehled: kdo je přihlášen na trénink, kolik míst je obsazených)
         -vyřešeno načítáním EAGER
         */
        return trainingService.getTrainingsByDateRange(startDate, endDate);
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
        System.out.println("Požadavek na odstranění zvoleného tréninku podle načteného ID: " + trainingId + ". Spolu s tréninkem dojde ke smazání všech rezervací.");
        trainingService.removeAllPlannedTrainings(trainingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping({"api/editTrainingChosenInOverview/{id}/", "api/editTrainingChosenInOverview/{id}"})
    public TrainingDTO editTraining (@PathVariable("id") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        System.out.println("Požadavek na odstranění zvoleného tréninku podle načteného ID: " + trainingId + ". Spolu s tréninkem dojde ke smazání všech rezervací.");
        return trainingService.editOneTraining(trainingId, trainingDTO);
    }

    // PUT metoda pro úpravu všech následujících tréninků
    @PutMapping({"api/editAllPlanned/{id}/", "api//editAllPlanned/{id}"})
    public ResponseEntity<Void> editAllPlannedTrainings(@PathVariable("id") Long trainingId, @RequestBody TrainingDTO trainingDTO) {
        trainingService.editAllPlannedTrainings(trainingId, trainingDTO);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content pokud úprava proběhne úspěšně
    }

    // Metoda pro odhlášení administrátora
    @PostMapping("/api/logoutAdmin")
    public ResponseEntity<String> logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Požadavek na odhlášení administrátora (AdminController, logoutAdmin)");

        adminService.logout(request, response);
        return ResponseEntity.ok("Úspěšně odhlášen.");
    }

}
