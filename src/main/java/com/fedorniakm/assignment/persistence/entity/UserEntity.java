package com.fedorniakm.assignment.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "birth_date", nullable = false)
        private LocalDate birthDate;

        @Column(name = "address", nullable = true)
        private String address;

        @Column(name = "phone_number", nullable = true)
        private String phoneNumber;

}
