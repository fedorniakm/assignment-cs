package com.fedorniakm.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fedorniakm.demo.validation.ValidUserAge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Past
    @ValidUserAge
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy",
            shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

    private Optional<String> address;
    private Optional<String> phoneNumber;

}
