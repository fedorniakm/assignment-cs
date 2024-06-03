package com.fedorniakm.demo.service;

import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.model.UserPatch;
import com.fedorniakm.demo.persistence.entity.UserEntity;
import com.fedorniakm.demo.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return toUsers(repository.getAll());
    }

    @Override
    public List<User> getAll(Optional<LocalDate> from, Optional<LocalDate> to) {
        return toUsers(repository.getAll(from, to));
    }

    @Override
    public Optional<User> getById(Long id) {
        return repository.getById(id).map(this::toUser);
    }

    @Override
    public User create(User user) {
        var result = repository.create(toUserEntity(user));
        return toUser(result);
    }

    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public boolean replace(User user) {
        return repository.update(toUserEntity(user));
    }

    @Override
    public boolean patch(Long id, UserPatch userPatch) {
        var userEntity = repository.getById(id);
        if (userEntity.isPresent()) {
            var user = userEntity.get();
            patch(user, userPatch);
            return repository.update(user);
        }
        return false;
    }

    private void patch(UserEntity user, UserPatch patch) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(patch);
        patchStringIfNotEmpty(patch.getEmail(), user::setEmail);
        patchStringIfNotEmpty(patch.getFirstName(), user::setFirstName);
        patchStringIfNotEmpty(patch.getLastName(), user::setLastName);
        patchValue(patch.getBirthDate(), user::setBirthDate);
        patchValue(patch.getAddress(), user::setAddress);
        patchValue(patch.getPhoneNumber(), user::setPhoneNumber);
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

    private User toUser(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .address(Optional.ofNullable(entity.getAddress()))
                .phoneNumber(Optional.ofNullable(entity.getPhoneNumber()))
                .build();
    }

    private UserEntity toUserEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber().orElse(null))
                .address(user.getAddress().orElse(null))
                .build();
    }

    private List<User> toUsers(List<UserEntity> entities) {
        return entities.parallelStream().map(this::toUser).toList();
    }
}
