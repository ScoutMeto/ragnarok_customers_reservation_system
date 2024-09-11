package com.matejmarek.ragnarok_customers_reservation_system.entity.repository;

import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity> {
    List<ReservationEntity> findByTraining_TrainingId(Long trainingId);

}
