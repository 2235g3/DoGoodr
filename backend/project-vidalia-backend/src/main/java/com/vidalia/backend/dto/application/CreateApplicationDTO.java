package com.vidalia.backend.dto.application;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateApplicationDTO {

    @NotNull(message = "Message must not be blank")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    @JsonDeserialize(using = HtmlEscapeDeserializer.class)
    private String message;
}
