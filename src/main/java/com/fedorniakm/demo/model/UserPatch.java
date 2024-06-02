package com.fedorniakm.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fedorniakm.demo.validation.NullOrNotEmptyString;
import com.fedorniakm.demo.validation.ValidUserAge;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPatch {

    @NullOrNotEmptyString
    private String email;

    @NullOrNotEmptyString
    private String firstName;

    @NullOrNotEmptyString
    private String lastName;

    @Past
    @ValidUserAge
    @JsonInclude(value= JsonInclude.Include.NON_EMPTY)
    @JsonFormat(pattern = "dd-MM-yyyy",
            shape = JsonFormat.Shape.STRING)
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;

}
