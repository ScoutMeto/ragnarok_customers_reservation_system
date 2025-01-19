package com.matejmarek.ragnarok_customers_reservation_system.entity.repository;

import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface TrainingRepository extends JpaRepository<TrainingEntity, Long>, JpaSpecificationExecutor<TrainingEntity> {

    @Query("SELECT t FROM TrainingEntity t LEFT JOIN FETCH t.reservationsList WHERE t.dateOfCurrentLesson BETWEEN :startDate AND :endDate")
    Page<TrainingEntity> findByDateBetweenMondaySunday(@Param ("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    List<TrainingEntity> findAllByNameOfLessonAndStartOfCurrentLessonAndEndOfCurrentLessonAndDateOfCurrentLessonAfter(String lessonName, Time startTime, Time endTime, LocalDateTime localDateTime);

    List<TrainingEntity> findByNameOfLessonAndStartOfCurrentLessonAndDateOfCurrentLessonGreaterThanEqual(
            String nameOfLesson, Time startOfCurrentLesson, LocalDateTime dateOfCurrentLesson);


}
