package com.fedorniakm.demo.model;

import com.fedorniakm.demo.validation.ValidDateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidDateRange
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {

        @DateTimeFormat(pattern = "dd-MM-yyyy")
        private LocalDate from;

        @DateTimeFormat(pattern = "dd-MM-yyyy")
        private LocalDate to;
}
