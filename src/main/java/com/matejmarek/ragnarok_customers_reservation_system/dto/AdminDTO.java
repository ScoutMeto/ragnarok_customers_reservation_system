package com.matejmarek.ragnarok_customers_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminDTO {

    @JsonProperty("user_id")
    private Long adminId;

    private String nickname;

    @Email
    private String adminEmail;

    @Size(min = 6, message = "Použij minimálně 6 znaků.")
    private String password;

    @JsonProperty("isAdmin")
    private boolean admin;
}
