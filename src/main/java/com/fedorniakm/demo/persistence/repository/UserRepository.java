package com.fedorniakm.demo.persistence.repository;

import com.fedorniakm.demo.persistence.entity.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserEntity> getAll();

    List<UserEntity> getAll(Optional<LocalDate> from, Optional<LocalDate> to);

    Optional<UserEntity> getById(Long id);

    UserEntity create(UserEntity user);

    boolean deleteById(Long id);

    boolean update(UserEntity user);

}
