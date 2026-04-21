package com.vidalia.backend.dto.profile;
import lombok.Data;

import java.util.UUID;

@Data
public class OProfileResponseDTO {
    private UUID id;
    private String displayName;
    private String description;
    private String contactEmail;
    private String location;
    private String websiteUrl;

}
