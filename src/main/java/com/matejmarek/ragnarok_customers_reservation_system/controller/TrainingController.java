package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainingController {

    @Autowired
    TrainingService trainingService;

    @PostMapping("/createNewTraining/api")
    public TrainingDTO addTraining (@RequestBody @Valid TrainingDTO trainingDTO) {
        System.out.println("Požadavek na vytvoření nového tréninku (TrainingController, addTraining): " + trainingDTO);
        return trainingService.createTraining(trainingDTO);
    }

}
