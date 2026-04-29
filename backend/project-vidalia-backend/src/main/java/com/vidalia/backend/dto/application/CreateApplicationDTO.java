package com.vidalia.backend.dto.application;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateApplicationDTO {
    private UUID opportunityId;
    private String message;
}
