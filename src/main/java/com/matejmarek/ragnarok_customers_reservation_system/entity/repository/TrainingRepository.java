package com.matejmarek.ragnarok_customers_reservation_system.entity.repository;

import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
@Repository
public interface TrainingRepository extends JpaRepository<TrainingEntity, Long>, JpaSpecificationExecutor<TrainingEntity> {

//    @Query("SELECT t FROM TrainingEntity t LEFT JOIN FETCH t.reservationsList WHERE t.dateOfCurrentLesson BETWEEN :startDate AND :endDate")
//    Page<TrainingEntity> findByDateBetween(@Param ("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    List<TrainingEntity> findByDateOfCurrentLessonBetween(@Param ("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    List<TrainingEntity> findAllByNameOfLessonAndStartOfCurrentLesson(String nameOfLesson, LocalDateTime startOfCurrentLesson);

    List<TrainingEntity> findByNameOfLessonAndStartOfCurrentLesson(
            String nameOfLesson, LocalDateTime startOfCurrentLesson);


}
