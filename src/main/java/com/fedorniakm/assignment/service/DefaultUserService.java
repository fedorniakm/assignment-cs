package com.fedorniakm.assignment.service;

import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;
import com.fedorniakm.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        return userRepository.getAll(from, to);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public boolean patch(Long id, UserPatch userPatch) {
        return userRepository.patch(id, userPatch);
    }

    @Override
    public boolean deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public boolean replace(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        return userRepository.replace(user);
    }

}
