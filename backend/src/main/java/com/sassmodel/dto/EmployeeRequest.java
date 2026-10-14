package com.sassmodel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeRequest(

        Long organizationId,

        /** Optional login account link; not every employee needs platform access. */
        Long userId,

        @NotBlank(message = "Full name is required")
        @Size(max = 150, message = "Full name must be at most 150 characters")
        String fullName,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @Size(max = 30, message = "Phone must be at most 30 characters")
        String phone,

        @Size(max = 100, message = "Position must be at most 100 characters")
        String position,

        LocalDate joinedAt,

        Boolean active
) {
}
