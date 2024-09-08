package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private TrainingEntity training;

    @JsonProperty("reservation_id")
    private Long reservationId;

    private String firstName;

    private String secondName;

    @Email
    private String userEmail;

    private int telephoneNumber;

    @Value("1")
    private Integer numberOfBookedEntries;

    @Value("true")
    private boolean trainingPassedOrDeleted; //true = pass; false = delete
}
