package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {

    @JsonProperty("reservation_id")
    private Long trainingId;

    private String nameOfLesson;

//    private int numberOfReservations;

    private int numberOfFreeSlots;

    private String coachName;

    private LocalDateTime dateOfCurrentLesson;

    private LocalDateTime startOfCurrentLesson;

    private LocalDateTime endOfCurrentLesson;

    @Value("0")
    private int repeatIntervalInDays;

    @Value("0")
    private int numberOfCopyConcreteTraining;



    private List<ReservationDTO> reservations;
}
