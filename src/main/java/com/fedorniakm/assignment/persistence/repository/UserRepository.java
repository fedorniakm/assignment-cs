package com.fedorniakm.assignment.persistance.repository;

import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to);

    Optional<User> getById(Long id);

    User create(User user);

    boolean deleteById(Long id);

    boolean replace(User user);

    boolean patch(Long id, UserPatch userPatch);
}
