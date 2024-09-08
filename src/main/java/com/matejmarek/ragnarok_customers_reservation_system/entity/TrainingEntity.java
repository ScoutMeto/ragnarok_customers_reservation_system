package com.matejmarek.ragnarok_customers_reservation_system.entity;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "training")
@Table(name = "training")
@Getter
@Setter
public class TrainingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingId;

    private String nameOfLesson;

    private int numberOfReservations;

    private int numberOfFreeSlots;

    private LocalDateTime dateOfCurrentLesson;

    private Time startOfCurrentLesson;

    private Time endOfCurrentLesson;

    private int repeatIntervalInDays;

    private int numberOfCopyConcreteTraining;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservationsList;
}
