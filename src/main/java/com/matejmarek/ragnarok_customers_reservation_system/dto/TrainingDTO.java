package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingDTO {

    @JsonProperty("training_id")
    private Long trainingId;

    private String nameOfLesson;

    private int numberOfFreeSlots;

    private String coachName;

    private LocalDateTime dateOfCurrentLesson;

    private LocalDateTime startOfCurrentLesson;

    private LocalDateTime endOfCurrentLesson;

    private int repeatIntervalInDays = 7;

    private int numberOfCopyConcreteTraining = 0;

    private Long parentTrainingId;

    private List<ReservationDTO> reservations;
}
