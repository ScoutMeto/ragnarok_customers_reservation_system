package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.TrainingDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.TrainingEntity;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring", uses = {ReservationMapper.class})
public interface TrainingMapper {

//    @Mapping(target = "trainingId", ignore = true) // ID generuje DB
//    @Mapping(target = "reservations", ignore = true)
    TrainingEntity toEntity(TrainingDTO dto);

//    @Mapping(target = "reservations", source = "reservations")
    TrainingDTO toDTO(TrainingEntity entity);

}
