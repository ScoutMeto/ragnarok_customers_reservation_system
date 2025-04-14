package com.matejmarek.ragnarok_customers_reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

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
//    @Value ("false")
    private boolean admin = false;

    @Column(nullable = false)
    @Size(max = 8)
    private Integer numberOfBookedEntries;

    @Column(nullable = false)
    private boolean trainingPassedOrDeleted;

}
