package com.matejmarek.ragnarok_customers_reservation_system.entity.repository;

import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity> {
    List<ReservationEntity> findByTraining_TrainingId(Long trainingId);

//    @Modifying
//    void deleteReservationEntityByReservationId(Long reservationId);

    @Modifying
    @Transactional          // nebo na service-vrstvě
    @Query("DELETE FROM reservations r WHERE r.reservationId = :id")
    int deleteByReservationIdJPQL(@Param("id") Long reservationId);
}

