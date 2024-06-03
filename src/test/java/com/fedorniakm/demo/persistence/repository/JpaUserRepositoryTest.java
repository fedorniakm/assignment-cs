package com.fedorniakm.demo.persistence.repository;

import com.fedorniakm.demo.Application;
import com.fedorniakm.demo.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaUserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 150, 300})
    void testCreateUser_AssignsId(int userNumber) {
        IntStream.range(0, userNumber)
                .mapToObj(i -> validUserEntity())
                .forEach(user -> {
                    var createdUser = userRepo.create(user);
                    assertNotNull(createdUser.getId(),
                            "User Id is not populated while creating.");
                    assertEqualUsers(user, createdUser);
                });
    }

    @Test
    void testCreateUserAndFindById() {
        var user = new UserEntity(null,
                "Test@test.com",
                "First Test",
                "Last Test",
                LocalDate.of(1991, 2, 3),
                "Test Address",
                "+129873298374");
        user = userRepo.create(user);
        var foundUser = userRepo.getById(user.getId());

        assertThat(foundUser).isPresent();
        assertEqualUsers(user, foundUser.get());
    }

    @Test
    void testGetById_NotFound() {
        var foundUser = userRepo.getById(1L);

        assertThat(foundUser).isNotPresent();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 10, 237})
    void testGetAll(int userNumber) {
        IntStream.range(0, userNumber)
                .mapToObj(i -> validUserEntity())
                .forEach(userRepo::create);

        var users = userRepo.getAll();

        assertThat(users.size()).isEqualTo(userNumber);
    }

    @Test
    void testGetAll_NoUsers() {
        var users = userRepo.getAll();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    void testGetAll_WithFilters() {
        var u1 = validUserEntity();
        u1.setBirthDate(LocalDate.of(1990, 1, 1));
        var u2 = validUserEntity();
        u2.setBirthDate(LocalDate.of(1995, 1, 1));
        var u3 = validUserEntity();
        u3.setBirthDate(LocalDate.of(1999, 1, 1));

        userRepo.create(u1);
        userRepo.create(u2);
        userRepo.create(u3);

        var users = userRepo.getAll(
                Optional.of(LocalDate.of(1991, 5, 5)),
                Optional.of(LocalDate.of(1998, 5, 5)));

        assertThat(userRepo.getAll().size()).isEqualTo(3);
        assertThat(users.size()).isEqualTo(1);
        assertEqualUsers(u2, users.get(0));
        assertThat(userRepo.getAll(
                Optional.of(LocalDate.of(1991, 5, 5)),
                Optional.of(LocalDate.of(2001, 5, 5)))
                .size())
                .isEqualTo(2);
        assertThat(userRepo.getAll(
                        Optional.of(LocalDate.of(1981, 5, 5)),
                        Optional.of(LocalDate.of(2001, 5, 5)))
                .size())
                .isEqualTo(3);
        assertThat(userRepo.getAll(
                        Optional.of(LocalDate.of(1981, 5, 5)),
                        Optional.of(LocalDate.of(1989, 5, 5)))
                .size())
                .isEqualTo(0);
    }

    @Test
    void testDeleteById() {
        var user = validUserEntity();
        var userId = userRepo.create(user).getId();

        var result = userRepo.deleteById(userId);

        assertThat(result).isTrue();
        assertThat(userRepo.getById(userId)).isNotPresent();
    }

    @Test
    void testDeleteById_NoUser() {
        var userId = 1L;

        var result = userRepo.deleteById(userId);

        assertThat(result).isFalse();
        assertThat(userRepo.getById(userId)).isNotPresent();
    }

    @Test
    void testUpdate() {
        var firstUser = validUserEntity();
        var firstUserId = userRepo.create(firstUser).getId();
        var secondUser = validUserEntity(firstUserId);

        var result = userRepo.update(secondUser);

        var savedUser = userRepo.getById(firstUserId);
        assertThat(result).isTrue();
        assertThat(savedUser).isPresent();
        assertEqualUsers(secondUser, savedUser.get());
    }

    @Test
    void testUpdate_NoUser() {
        var userId = 99999L;
        var user = validUserEntity(userId);

        var result = userRepo.update(user);
        var savedUser = userRepo.getById(userId);

        assertThat(result).isFalse();
        assertThat(savedUser).isNotPresent();
    }

    private void assertEqualUsers(UserEntity expected, UserEntity actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    private UserEntity validUserEntity() {
        return validUserEntity(null);
    }

    private UserEntity validUserEntity(Long id) {
        var num = ThreadLocalRandom.current().nextInt(10_000) + 1;
        return UserEntity.builder()
                .id(id)
                .firstName("FirstName" + num)
                .lastName("LastName")
                .email("user" + num + "@user" + num + ".com")
                .birthDate(LocalDate.of(1950 + num, 1, 1))
                .address(num + "str., Userwill, Australia, " + num)
                .phoneNumber("+321" + num + "6" + num * 3 + "" + num * num + "")
                .build();
    }

}
