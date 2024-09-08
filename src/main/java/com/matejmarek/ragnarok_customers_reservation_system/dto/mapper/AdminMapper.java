package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminEntity toEntity(AdminDTO source);
    AdminDTO toDTO (AdminEntity source);

}
