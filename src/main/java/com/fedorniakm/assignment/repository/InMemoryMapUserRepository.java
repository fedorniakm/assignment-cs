package com.fedorniakm.assignment.repository;

import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Repository
public class InMemoryMapUserRepository implements UserRepository {

    private final AtomicLong atomicLong = new AtomicLong(1L);
    private final Map<Long, User> users;

    public InMemoryMapUserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        var userStream = users.values().stream();
        if (from.isPresent()) {
            userStream = userStream.filter(user -> user.getBirthDate().isAfter(from.get()));
        }
        if (to.isPresent()) {
            userStream = userStream.filter(user -> user.getBirthDate().isBefore(to.get()));
        }
        return userStream.toList();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        Objects.requireNonNull(user);
        var id = atomicLong.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public boolean replace(User user) {
        return users.replace(user.getId(), user) != null;
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
