package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {

    @JsonProperty("reservation_id")
    private Long trainingId;

    private String nameOfLesson;

    private int numberOfReservations;

    private int numberOfFreeSlots;

    private LocalDateTime dateOfCurrentLesson;

    private Time startOfCurrentLesson;

    private Time endOfCurrentLesson;

    private int repeatIntervalInDays;

    private int numberOfCopyConcreteTraining;



    private List<ReservationDTO> reservations;
}
