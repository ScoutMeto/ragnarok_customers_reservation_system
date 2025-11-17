package com.matejmarek.ragnarok_customers_reservation_system.dto.mapper;

import com.matejmarek.ragnarok_customers_reservation_system.dto.PartialReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.dto.ReservationDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.ReservationEntity;
import com.matejmarek.ragnarok_customers_reservation_system.configuration.InputSanitizationService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.constraints.Email;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//původní mapper. Nově předělaný pro ruční převody. Map Struct nespolupracoval. Třeba to časem opravím.
//@Mapper(componentModel = "spring")
//public interface ReservationMapper {
//
//    ReservationEntity toEntity(ReservationDTO dto);
//
////    @Mapping(target = "firstName", source = "firstName")
////    @Mapping(target = "secondName", source = "secondName")
////    @Mapping(target = "userEmail", source = "userEmail")
////    @Mapping(target = "telephoneNumber", source = "telephoneNumber")
////    @Mapping(target = "numberOfBookedEntries", source = "numberOfBookedEntries")
//    ReservationDTO toDTO(ReservationEntity entity);
//
//    List<ReservationEntity> toReservationEntities(List<ReservationDTO> reservationDTOs);
//    List<ReservationDTO> toReservationDTOs(List<ReservationEntity> reservationEntities);
//}
@Component
public class ReservationMapper {


    @Autowired
    InputSanitizationService sanitizer;

    public ReservationEntity toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        ReservationEntity entity = new ReservationEntity();
        entity.setReservationId(dto.getReservationId());
        entity.setFirstName(sanitizer.sanitizeTextOnlyPlain(dto.getFirstName()));
        entity.setSecondName(sanitizer.sanitizeTextOnlyPlain(dto.getSecondName()));
        entity.setUserEmail(dto.getUserEmail());
        entity.setTelephoneNumber(sanitizer.sanitizeTextOnlyPlain(dto.getTelephoneNumber()));
        entity.setNumberOfBookedEntries(dto.getNumberOfBookedEntries());
        entity.setTraining(dto.getTraining());
        // POZOR: TrainingEntity ani pole `trainingPassedOrDeleted` tu nenastavuj, pokud to neřešíš ručně v Service

        return entity;
    }

    @Mapping(target = "training", ignore = true)
    public ReservationDTO toDTO(ReservationEntity entity) {
        if (entity == null) {
            return null;
        }

        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(entity.getReservationId());
        dto.setFirstName(entity.getFirstName());
        dto.setSecondName(entity.getSecondName());
        dto.setUserEmail(entity.getUserEmail());
        dto.setTelephoneNumber(entity.getTelephoneNumber());
        dto.setNumberOfBookedEntries(entity.getNumberOfBookedEntries());

        return dto;
    }

    @Mapping(target = "training", ignore = true)
//    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "maskLastName")
    public PartialReservationDTO toPartialDTO(ReservationEntity entity) {
        if (entity == null) {
            return null;
        }

        PartialReservationDTO dto = new PartialReservationDTO();
        dto.setReservationId(entity.getReservationId());
        dto.setFirstName(entity.getFirstName());

        String secondName = entity.getSecondName();
        String masked = null;
        if (secondName != null && !secondName.isBlank()) {
            int cp = secondName.trim().codePointAt(0);
            masked = new String(Character.toChars(cp)) + ".";
        }
        dto.setSecondName(masked);

        dto.setNumberOfBookedEntries(entity.getNumberOfBookedEntries());

        return dto;
    }

    public List<ReservationEntity> toReservationEntities(List<ReservationDTO> reservationDTOs) {
        if (reservationDTOs == null) {
            return null;
        }

        return reservationDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> toReservationDTOs(List<ReservationEntity> reservationEntities) {
        if (reservationEntities == null) {
            return null;
        }

        return reservationEntities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}