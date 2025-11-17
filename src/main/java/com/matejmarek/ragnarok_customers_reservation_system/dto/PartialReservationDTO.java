package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartialReservationDTO{

    @JsonProperty("reservation_id")
    private Long reservationId;

    private Long trainingId;

    private TrainingEntity training;

    private String firstName;

    private String secondName;

    private boolean admin;

    private int numberOfBookedEntries = 1;

    @Value("true")
    private boolean trainingPassedOrDeleted; //true = pass; false = delete
}