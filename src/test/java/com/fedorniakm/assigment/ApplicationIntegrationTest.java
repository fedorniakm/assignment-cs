package com.fedorniakm.assigment;

import com.fedorniakm.assignment.AssignmentApplication;
import com.fedorniakm.assignment.model.Data;
import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = AssignmentApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApplicationIntegrationTest {

    private static final String API_USERS = "/v1/users";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void getAllUsers() {
        userService.create(validUser());
        userService.create(validUser());

        var response = restTemplate.exchange(API_USERS,
                HttpMethod.GET,
                new HttpEntity<>(Map.of("ContentType", "application/json")),
                new ParameterizedTypeReference<Data<List<User>>>() {});
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        List<User> users = response.getBody().data();
        assertEquals(2, users.size());
    }

    @Test
    void getAllUsers_WhenNoUsers_ThenEmptyData() {
        var response = restTemplate.exchange(API_USERS,
                HttpMethod.GET,
                new HttpEntity<>(Map.of("ContentType", "application/json")),
                new ParameterizedTypeReference<Data<List<User>>>() {});

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        log(response.getBody());
        Data<List<User>> data = response.getBody();
        assertThat(data.isEmpty() || data.data().isEmpty()).isTrue();
    }

    @Test
    void getAllUsers_WhenFilters_ThenOnlyFilteredUsers() {
        var user1 = validUser();
        var user2 = validUser();
        var user3 = validUser();
        user1.setBirthDate(LocalDate.of(1991, 1, 1));
        user2.setBirthDate(LocalDate.of(1994, 1, 1));
        user3.setBirthDate(LocalDate.of(1999, 1, 1));
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);

        var response = restTemplate.exchange(API_USERS + "?from=01-01-1992&to=01-01-1998",
                HttpMethod.GET,
                new HttpEntity<>(Map.of("ContentType", "application/json")),
                new ParameterizedTypeReference<Data<List<User>>>() {});
        log("Response: " + response);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());

        var data = response.getBody();
        assertThat(data.isEmpty()).isFalse();

        var users = data.data();
        assertEquals(1, users.size(), "Should return one of three users that matches the filter");

        var returnedUser = users.get(0);
        assertTrue(returnedUser.getBirthDate().isEqual(LocalDate.of(1994, 1, 1)));
    }

    @Test
    void getUserById() {
        var user = validUser();
        userService.create(user);

        var response = restTemplate.exchange(API_USERS + "/1",
                HttpMethod.GET,
                new HttpEntity<>(Map.of("ContentType", "application/json")),
                new ParameterizedTypeReference<Data<User>>() {});

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertNotNull(response.getBody());
        log(response.getBody());
        var receivedUser = response.getBody().data();
        assertNotNull(receivedUser);
        assertEqualUsers(user, receivedUser);
    }

    @Test
    void createUser() {
        var user = new User(null,
                "email",
                "frst",
                "lawdawd",
                LocalDate.of(2001, 1, 1),
                Optional.of("Address str. Address DC, ADR"),
                Optional.of("+380934764527"));

        var response
                = restTemplate.postForEntity(API_USERS, Data.of(user), String.class);
        log("Response:" + response);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        var savedUser = userService.getById(1L);

        assertTrue(savedUser.isPresent());
        assertEqualUsers(user, savedUser.get());
    }

    private void assertEqualUsers(User expected, User actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
    }

    private User validUser() {
        var num = ThreadLocalRandom.current().nextInt(20) + 1;
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

    private void log(String log) {
        System.out.println(log);
    }

    private void log(Object log) {
        log(log.toString());
    }

    private String userDataJson(Long id, String email, String firstName, String lastName,
                                String birthDate, String address, String phoneNumber) {

        var userDataJson = new StringJoiner(",");
        if (Objects.nonNull(id)) {
            userDataJson.add("\"id\":\"" + id + "\"");
        }
        if (Objects.nonNull(email)) {
            userDataJson.add("\"email\":\"" + email + "\"");
        }
        if (Objects.nonNull(firstName)) {
            userDataJson.add("\"firstName\":\"" + firstName + "\"");
        }
        if (Objects.nonNull(lastName)) {
            userDataJson.add("\"lastName\":\"" + lastName + "\"");
        }
        if (Objects.nonNull(birthDate)) {
            userDataJson.add("\"birthDate\":\"" + birthDate + "\"");
        }
        if (Objects.nonNull(address)) {
            userDataJson.add("\"address\":\"" + address + "\"");
        }
        if (Objects.nonNull(phoneNumber)) {
            userDataJson.add("\"phoneNumber\":\"" + phoneNumber + "\"");
        }
        return "{\"data\":{" + userDataJson + "}}";
    }

}
