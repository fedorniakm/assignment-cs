package com.fedorniakm.assigment.service;

import com.fedorniakm.assignment.Application;
import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.model.UserPatch;
import com.fedorniakm.assignment.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 1000, 10_000})
    void testCreateUser_AssignsId(int userNumber) {
        IntStream.range(0, userNumber)
                .mapToObj(i -> validUser())
                .forEach(user -> {
                    var createdUser = userService.create(user);
                    assertNotNull(createdUser.getId(),
                            "User Id is not populated while creating.");
                    assertEqualUsers(user, createdUser);
                });
    }

    @Test
    void testCreateUserAndFindById() {
        var user = new User(null,
                "Test@test.com",
                "First Test",
                "Last Test",
                LocalDate.of(1991, 2, 3),
                Optional.of("Test Address"),
                Optional.of("+129873298374"));
        user = userService.create(user);
        var foundUser = userService.getById(user.getId());

        assertThat(foundUser).isPresent();
        assertEqualUsers(user, foundUser.get());
    }

    @Test
    void testGetById_NotFound() {
        var foundUser = userService.getById(1L);

        assertThat(foundUser).isNotPresent();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 10, 237, 2745})
    void testGetAll(int userNumber) {
        IntStream.range(0, userNumber)
                .mapToObj(i -> validUser())
                .forEach(userService::create);

        var users = userService.getAll();

        assertThat(users.size()).isEqualTo(userNumber);
    }

    @Test
    void testGetAll_NoUsers() {
        var users = userService.getAll();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    void testDeleteById() {
        var user = validUser();
        var userId = userService.create(user).getId();

        var result = userService.deleteById(userId);

        assertThat(result).isTrue();
        assertThat(userService.getById(userId)).isNotPresent();
    }

    @Test
    void testDeleteById_NoUser() {
        var userId = 1L;

        var result = userService.deleteById(userId);

        assertThat(result).isFalse();
        assertThat(userService.getById(userId)).isNotPresent();
    }

    @Test
    void testReplace() {
        var firstUser = validUser();
        var firstUserId = userService.create(firstUser).getId();
        var secondUser = validUser();
        secondUser.setId(firstUserId);

        var result = userService.replace(secondUser);

        var savedUser = userService.getById(firstUserId);
        assertThat(result).isTrue();
        assertThat(savedUser).isPresent();
        assertEqualUsers(secondUser, savedUser.get());
    }

    @Test
    void testReplace_NoUser() {
        var userId = 99999L;
        var user = validUser();
        user.setId(userId);

        var result = userService.replace(user);
        var savedUser = userService.getById(userId);

        assertThat(result).isFalse();
        assertThat(savedUser).isNotPresent();
    }

    @Test
    void testPatch_NoUser() {
        var userId = 99999L;
        var userPatch = UserPatch.builder().email("test@test.com").build();

        var result = userService.patch(userId, userPatch);
        var savedUser = userService.getById(userId);

        assertThat(result).isFalse();
        assertThat(savedUser).isNotPresent();
    }

    @Test
    void testPatch() {
        var user = validUser();
        var userId = userService.create(user).getId();
        var userPatch = UserPatch.builder()
                .email("testingTestTEEEEST@testyvannya.com")
                .firstName("SomeRandomFirst1Name")
                .lastName("RandomNameLastForTestP")
                .build();

        var result = userService.patch(userId, userPatch);
        var patchedUser = userService.getById(userId);

        assertThat(result).isTrue();
        assertThat(patchedUser).isPresent();
        assertEquals(userPatch.getFirstName(), patchedUser.get().getFirstName());
        assertEquals(userPatch.getLastName(), patchedUser.get().getLastName());
        assertEquals(userPatch.getEmail(), patchedUser.get().getEmail());
    }

    private void assertEqualUsers(User expected, User actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    private User validUser() {
        var num = ThreadLocalRandom.current().nextInt(10_000) + 1;
        return User.builder()
                .id(null)
                .firstName("FirstName" + num)
                .lastName("LastName")
                .email("user" + num + "@user" + num + ".com")
                .birthDate(LocalDate.of(1950 + num, 1, 1))
                .address(Optional.of(num + "str., Userwill, Australia, " + num))
                .phoneNumber(Optional.of("+321" + num + "6" + num * 3 + "" + num * num + ""))
                .build();
    }

}
