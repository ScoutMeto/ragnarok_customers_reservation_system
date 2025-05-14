package com.matejmarek.ragnarok_customers_reservation_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AimedTrainingsRequestDTO {

    private List<Long> trainingIds;

}
