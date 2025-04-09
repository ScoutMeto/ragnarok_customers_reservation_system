package com.matejmarek.ragnarok_customers_reservation_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

//    private int numberOfReservations;

    private int numberOfFreeSlots;

    private String coachName;

    private LocalDateTime dateOfCurrentLesson;

    private LocalDateTime startOfCurrentLesson;

    private LocalDateTime endOfCurrentLesson;

    private int repeatIntervalInDays;

    private int numberOfCopyConcreteTraining;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ReservationEntity> reservations;
}
