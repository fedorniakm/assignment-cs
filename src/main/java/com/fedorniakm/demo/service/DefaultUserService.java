package com.fedorniakm.demo.service;

import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.model.UserPatch;
import com.fedorniakm.demo.persistence.entity.UserEntity;
import com.fedorniakm.demo.persistence.repository.UserRepository;
import com.fedorniakm.demo.service.patcher.UserEntityPatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class DefaultUserService implements UserService {

    private final UserRepository repository;
    private final UserEntityPatcher userEntityPatcher;

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
    public boolean patch(Long id, UserPatch patch) {
        var target = repository.getById(id);
        if (target.isPresent()) {
            var userEntity = target.get();
            userEntityPatcher.patch(userEntity, patch);
            return repository.update(userEntity);
        }
        return false;
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
