package com.fedorniakm.demo;

import com.fedorniakm.demo.model.Data;
import com.fedorniakm.demo.model.User;
import com.fedorniakm.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ApplicationIntegrationTest {

    private static final String API_USERS = "/v1/users";
    private static final String API_USERS_ID = "/v1/users/{id}";

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private UserService userService;

    @BeforeEach
    void configureFactory() {
        restTemplate.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
    }

    @Test
    void contextLoads() { }

    @Test
    void getAllUsers() {
        userService.create(validUser());
        userService.create(validUser());

        var response = restTemplate.exchange(API_USERS,
                HttpMethod.GET,
                new HttpEntity<>(headersWithContentTypeJson()),
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
                new HttpEntity<>(headersWithContentTypeJson()),
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
                new HttpEntity<>(headersWithContentTypeJson()),
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

        var response = restTemplate.exchange(API_USERS_ID,
                HttpMethod.GET,
                new HttpEntity<>(headersWithContentTypeJson()),
                new ParameterizedTypeReference<Data<User>>() {},
                1);

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

    @Test
    void deleteUser() {
        var user = validUser();
        var userId = userService.create(user).getId();

        var response = restTemplate.exchange(
                API_USERS_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(headersWithContentTypeJson()),
                String.class,
                userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(userService.getById(userId)).isNotPresent();
    }

    @Test
    void updateUser() {
        var user = validUser();
        var userId = userService.create(user).getId();
        var userUpdate = new User(null,
                "test@test.com",
                "testF",
                "testL",
                LocalDate.of(1990, 1, 1),
                Optional.of("add"),
                Optional.of("ph++"));

        var response = restTemplate.exchange(
                API_USERS_ID,
                HttpMethod.PUT,
                new HttpEntity<>(Data.of(userUpdate), headersWithContentTypeJson()),
                String.class,
                userId);

        log(response);
        var updatedUser = userService.getById(userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updatedUser).isPresent();
        assertEqualUsers(userUpdate, updatedUser.get());
    }

    @Test
    void patchUser() {
        var newUser = validUser();
        var userId = userService.create(newUser).getId();
        var userPatch = userDataJson(null,
                "test@test.com",
                "testF",
                "testL",
                null,
                null,
                null);

        var response = restTemplate.exchange(
                API_USERS_ID,
                HttpMethod.PATCH,
                new HttpEntity<>(userPatch, headersWithContentTypeJson()),
                String.class,
                userId);

        log(response);
        var updatedUser = userService.getById(userId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(updatedUser).isPresent();
        var actuallUser = updatedUser.get();
        assertEquals("test@test.com", actuallUser.getEmail());
        assertEquals("testF", actuallUser.getFirstName());
        assertEquals("testL", actuallUser.getLastName());
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

    private HttpHeaders headersWithContentTypeJson() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
