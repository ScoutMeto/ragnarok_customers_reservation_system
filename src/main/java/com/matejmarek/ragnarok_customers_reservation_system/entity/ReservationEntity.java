package com.matejmarek.ragnarok_customers_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "reservations")
@Table(name = "reservations")
@Getter
@Setter
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "training_id", nullable = false)
    private TrainingEntity training;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private int telephoneNumber;

    @JsonProperty("isAdmin")
    private boolean admin = false;

    private int numberOfBookedEntries;

    @Column(nullable = false)
    private boolean trainingPassedOrDeleted;

}
