package com.ead.authservice.dto;

import com.ead.authservice.enums.RoleType;
import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private RoleType role;
}
