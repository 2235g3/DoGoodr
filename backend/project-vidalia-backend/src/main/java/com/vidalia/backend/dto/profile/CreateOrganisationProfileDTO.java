package com.vidalia.backend.dto.profile;

import com.vidalia.backend.model.AccountType;
import lombok.Data;

@Data
public class CreateOrganisationProfileDTO {
    private String displayName;
    private AccountType accountType;
    private String description;
    private String contactEmail;
    private String location;
    private String websiteUrl;
}
