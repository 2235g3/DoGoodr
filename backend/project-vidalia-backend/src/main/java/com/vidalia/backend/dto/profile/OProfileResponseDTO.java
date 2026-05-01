package com.vidalia.backend.dto.profile;
import com.vidalia.backend.model.AccountType;
import lombok.Data;

import java.util.UUID;

@Data
public class OProfileResponseDTO {
    private UUID id;
    private String displayName;
    private AccountType accountType;
    private boolean verified;
    private String profilePictureUrl;
    private String description;
    private String contactEmail;
    private String location;
    private String websiteUrl;

}
