package com.matejmarek.ragnarok_customers_reservation_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TrainingResponseDTO {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
//    private String coachName;
    private int numberOfTotalFreeSlots;
    private Long trainingId;

    private List<ReservationDTO> reservations;

    private Map<String, Object> extendedProps;

}