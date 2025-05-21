package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminDTO {

    @JsonProperty("user_id")
    private Long adminId;

    @Email
    private String adminEmail;

    private String nickname;

    @JsonProperty("isAdmin")
    private boolean admin = true;

    @Size(min = 6, message = "Použij minimálně 6 znaků.")
    private String password;
}
