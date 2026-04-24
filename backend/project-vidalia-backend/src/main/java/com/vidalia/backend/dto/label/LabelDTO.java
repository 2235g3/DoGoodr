package com.vidalia.backend.dto.label;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabelDTO {
    @NotNull(message = "Label ID cannot be null")
    private Long labelId;
    @NotNull(message = "Label weight cannot be null")
    private double weight;
}
