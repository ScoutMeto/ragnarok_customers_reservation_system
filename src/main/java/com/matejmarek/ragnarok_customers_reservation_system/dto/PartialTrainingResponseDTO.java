package com.matejmarek.ragnarok_customers_reservation_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PartialTrainingResponseDTO {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private int numberOfTotalFreeSlots;
    private Long trainingId;

    private List<PartialReservationDTO> reservations;

    private Map<String, Object> extendedProps;

}