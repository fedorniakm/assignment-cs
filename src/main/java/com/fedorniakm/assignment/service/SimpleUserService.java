package com.fedorniakm.assignment.service;

import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service
public class SimpleUserService implements UserService {

    private final List<User> userRepository;
    private final AtomicLong idGenerator;

    public SimpleUserService() {
        this.userRepository = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
    }

    @Override
    public List<User> getAll() {
        return userRepository;
    }

    @Override
    public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        var users = userRepository.stream();
        if (from.isPresent()) {
            users = users.filter(user -> user.getBirthDate().isAfter(from.get()));
        }
        if (to.isPresent()) {
            users = users.filter(user -> user.getBirthDate().isBefore(to.get()));
        }

        return users.toList();
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.stream().filter(user -> user.getId().equals(id)).findAny();
    }

    @Override
    public User create(User user) {
        user.setId(idGenerator.getAndIncrement());
        userRepository.add(user);
        return user;
    }

    @Override
    public boolean patch(Long id, UserPatch userPatch) {
        var user = getById(id);
        if (user.isPresent()) {
            patch(user.get(), userPatch);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return userRepository.removeIf(u -> u.getId().equals(id));
    }

    @Override
    public boolean replace(User user) {
         var isRemoved = userRepository.removeIf(u -> u.getId().equals(user.getId()));
         if (isRemoved)
             userRepository.add(user);
         return isRemoved;
    }

    private void patch(User user, UserPatch patch) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(patch);
        patchStringIfNotEmpty(patch.getEmail(), user::setEmail);
        patchStringIfNotEmpty(patch.getFirstName(), user::setFirstName);
        patchStringIfNotEmpty(patch.getLastName(), user::setLastName);
        patchValue(patch.getBirthDate(), user::setBirthDate);
        patchValue(patch.getAddress(), v -> user.setAddress(Optional.of(v)));
        patchValue(patch.getPhoneNumber(), v -> user.setPhoneNumber((Optional.of(v))));
    }

    private void patchStringIfNotEmpty(String value, Consumer<String> patch) {
        if (Objects.nonNull(value) && !value.isBlank()) {
            patch.accept(value);
        }
    }

    private <T> void patchValue(T value, Consumer<T> patch) {
        if (Objects.nonNull(value)) {
            patch.accept(value);
        }
    }
}
