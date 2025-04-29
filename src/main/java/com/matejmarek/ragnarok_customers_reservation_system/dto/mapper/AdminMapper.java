package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "authorities", ignore = true)
    AdminEntity toEntity(AdminDTO source);

    AdminDTO toDTO (AdminEntity source);

}
