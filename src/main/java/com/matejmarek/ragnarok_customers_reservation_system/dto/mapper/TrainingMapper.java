package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReservationMapper.class})
public interface TrainingMapper {

    TrainingEntity toEntity(TrainingDTO dto);

    @Mapping(target = "reservations.training", ignore = true)
    TrainingDTO toDTO(TrainingEntity entity);

}