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
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingEntity, Long>, JpaSpecificationExecutor<TrainingEntity> {

    List<TrainingEntity> findByDateOfCurrentLessonBetween(@Param ("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    List<TrainingEntity> findAllByNameOfLessonAndStartOfCurrentLesson(String nameOfLesson, LocalDateTime startOfCurrentLesson);

    Optional<TrainingEntity> findByNameOfLessonAndStartOfCurrentLesson(
            String nameOfLesson, LocalDateTime startOfCurrentLesson);

//    List<TrainingEntity> findByParentTrainingId(Long parentTrainingId);

    List<TrainingEntity> findByParentTrainingIdAndStartOfCurrentLessonGreaterThanEqual(Long parentTrainingId, LocalDateTime startOfCurrentLesson);



}
