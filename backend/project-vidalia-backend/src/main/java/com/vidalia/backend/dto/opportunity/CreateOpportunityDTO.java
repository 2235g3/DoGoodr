package com.vidalia.backend.dto.opportunity;

import com.vidalia.backend.model.OpportunityStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateOpportunityDTO {
    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;

    private Double longitude;
    private Double latitude;

    private Boolean remote;

    @Min(value = 0, message = "Minimum age cannot be negative")
    @Max(value = 21, message = "Minimum age cannot exceed 21")
    private Integer minAge;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private LocalDate endDate;
    private Boolean recurring;

    @Size(max = 255, message = "Availability cannot exceed 255 characters")
    private String availability;

    @Min(value = 0, message = "Required hours cannot be negative")
    private Integer requiredHours;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private OpportunityStatus status;
}
