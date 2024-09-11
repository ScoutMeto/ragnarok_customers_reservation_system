package com.matejmarek.ragnarok_customers_reservation_system.entity.repository;

import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<TrainingEntity, Long>, JpaSpecificationExecutor<TrainingEntity> {

    Page<TrainingEntity> findByDateBetweenMondaySunday(LocalDateTime startDate, LocalDateTime endDate);

}
