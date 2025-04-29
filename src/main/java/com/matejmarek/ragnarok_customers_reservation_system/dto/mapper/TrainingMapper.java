package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {ReservationMapper.class})
//@Mapper(componentModel = "spring")
public interface TrainingMapper {

//    @Mapping(target = "trainingId", ignore = true) // ID generuje DB
//    @Mapping(target = "parentTrainingId", source = "parentTrainingId") // výslovně zachová mapování
//    @Mapping(target = "reservations", ignore = true)
    TrainingEntity toEntity(TrainingDTO dto);

//    @Mapping(target = "parentTrainingId", source = "parentTrainingId")
//    @Mapping(target = "reservations", ignore = true)
    TrainingDTO toDTO(TrainingEntity entity);

}