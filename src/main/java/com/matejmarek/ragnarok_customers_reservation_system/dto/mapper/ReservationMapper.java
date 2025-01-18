package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationEntity toEntity(ReservationDTO dto);
    ReservationDTO toDTO(ReservationEntity entity);
    List<ReservationEntity> toReservationEntities(List<ReservationDTO> reservationDTOs);

}
