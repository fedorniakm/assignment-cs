package com.fedorniakm.assignment.persistence.repository;

import com.fedorniakm.assignment.model.UserPatch;
import com.fedorniakm.assignment.persistence.entity.UserEntity;

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
