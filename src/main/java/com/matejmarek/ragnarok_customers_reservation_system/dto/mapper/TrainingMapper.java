package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    TrainingEntity toEntity(TrainingDTO dto);
    TrainingDTO toDTO(TrainingEntity entity);
}
