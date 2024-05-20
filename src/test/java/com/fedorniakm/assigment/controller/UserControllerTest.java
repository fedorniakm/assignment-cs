package com.fedorniakm.assigment.controller;

import com.fedorniakm.assignment.AssignmentApplication;
import com.fedorniakm.assignment.controller.UserController;
import com.fedorniakm.assignment.model.User;
import com.fedorniakm.assignment.service.SimpleUserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = AssignmentApplication.class)
class UserControllerTest {

    private static final String API_USERS = "/v1/users";
    private static final String API_USERS_ID = "/v1/users/{id}";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SimpleUserService simpleUserService;

    @Test
    void getAllUsers_whenNoUsers_thenReturnEmptyJson() throws Exception {
        given(simpleUserService.getAll()).willReturn(Collections.emptyList());

        mvc.perform(get(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.not(empty())))
                .andExpect(jsonPath("$.data", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void getAllUsers_whenFromAndToParamsValid_thenReturnMatchingUsers() throws Exception {
        var user = User.builder()
                .id(1L)
                .firstName("1990user")
                .lastName("1990user")
                .email("1990user@123")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(Optional.of("1990user Address"))
                .phoneNumber(Optional.empty())
                .build();

        given(simpleUserService.getAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .willReturn(List.of(user));

        mvc.perform(get(API_USERS + "?from=01-01-1992&to=01-01-1998")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.data[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.data[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.data[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.data[0].birthDate",
                        is(dateToPattern(user.getBirthDate()))))
                .andExpect(jsonPath("$.data[0].address", is(user.getAddress().orElse(null))))
                .andExpect(jsonPath("$.data[0].phoneNumber", is(user.getPhoneNumber().orElse(null))));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {"ad-da01-dwa2099", "0112-01-2023", "2023-1-16", "2023-16-1", "16-1-2023", "1o-1-1990"})
    void getAllUsers_whenFromParamIsNotValid_thenReturn400(String date) throws Exception {
        mvc.perform(get(API_USERS + "?from=" + date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message", is("Field [from] is not valid.")))
                .andDo(print());
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {"ad-da01-dwa2099", "0112-01-2023", "2023-1-16", "2023-16-1", "16-1-2023", "1o-1-1990"})
    void getAllUsers_whenToParamIsNotValid_thenReturn400(String date) throws Exception {
        mvc.perform(get(API_USERS + "?to=" + date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message", is("Field [to] is not valid.")))
                .andDo(print());
    }

    @Test
    void getAllUsers_whenFromIsAfterToParam_thenReturn400() throws Exception {
        mvc.perform(get(API_USERS + "?from=01-01-1998&to=01-01-1992")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message", is("Invalid date range: 'from' must be before 'to'")));
    }

    @Test
    void getUserById_whenUserExists_thenReturnNonEmptyData() throws Exception {
        var user = User.builder()
                .id(1L)
                .firstName("1212")
                .lastName("123")
                .email("123@123")
                .birthDate(LocalDate.now())
                .address(Optional.of("Temp Address"))
                .phoneNumber(Optional.of("38099"))
                .build();

        given(simpleUserService.getById(1L)).willReturn(Optional.ofNullable(user));

        mvc.perform(get(API_USERS_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.data.email", is(user.getEmail())))
                .andExpect(jsonPath("$.data.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.data.birthDate",
                        is(dateToPattern(user.getBirthDate()))))
                .andExpect(jsonPath("$.data.address", is(user.getAddress().orElse(null))))
                .andExpect(jsonPath("$.data.phoneNumber", is(user.getPhoneNumber().orElse(null))));
    }

    @Test
    void getUserById_whenUsersNotExist_thenReturnEmptyData() throws Exception {
        given(simpleUserService.getById(anyLong())).willReturn(Optional.empty());

        mvc.perform(get(API_USERS_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void postUser_whenValidUserAllData_thenReturnOkAndLocation() throws Exception {
        var postUserJson = """
                {"data":{"email":"tempUser@temp.com","firstName":"John","lastName":"Doe","birthDate":"08-05-1994","address":"Temp Address, NY, Ukraine","phoneNumber":"+380936482351"}}
                """;

        var idCounter = 0L;
        given(simpleUserService.create(ArgumentMatchers.any(User.class)))
                .willReturn(User.builder().id(++idCounter).build());

        mvc.perform(post(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", API_USERS + "/1"))
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    void postUser_whenValidUserNoOptionalData_thenReturnOkAndLocation() throws Exception {
        var postUserJson = """
                {"data":{"email":"tempUser@temp.com","firstName":"John","lastName":"Doe","birthDate":"08-05-1994"}}
                """;

        var idCounter = 0L;
        given(simpleUserService.create(ArgumentMatchers.any(User.class)))
                .willReturn(User.builder().id(++idCounter).build());

        mvc.perform(post(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postUserJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", API_USERS + "/1"))
                .andExpect(content().string(""));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {"01-01-2099", "01-01-2023"})
    @NullSource
    @EmptySource
    void postUser_whenBirthDateIsNotWithinAllowedRange_thenReturn400(String birthDate) throws Exception {
        var userJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                birthDate,
                "Address str. Address DC, ADR",
                "+380927364527");

        mvc.perform(post(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message",
                        is("Field [data.birthDate] is not valid.")));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {
            "01-01-209f9",
            "o1-01-2023",
            "awdawdawd",
            "ad-da01-dwa2099",
            "0112-01-2023",
            "2023-1-16",
            "2023-16-1",
            "16-1-2023",
            "1o-1-1990",
            "1"
    })
    void postUser_whenBirthDateIsNotValidFormat_thenReturn400(String birthDate) throws Exception {
        var userJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                birthDate,
                "Address str. Address DC, ADR",
                "+380927364527");

        mvc.perform(post(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message",
                        is("Input date [" + birthDate + "] is not valid or has a wrong format.")));
    }

    @Test
    void postUser_whenRequiredFieldsAreMissing_thenReturn400() throws Exception {
        var userJson = userDataJson(null,
                null,
                null,
                null,
                "01-01-1991",
                "Address str. Address DC, ADR",
                "+380927364527");

        mvc.perform(post(API_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors..message", hasItems(
                        "Field [data.email] is not valid.",
                        "Field [data.firstName] is not valid.",
                        "Field [data.lastName] is not valid.")))
                .andDo(print());
    }

    @Test
    void deleteUser_whenSuccessfullyDeleted_thenReturn200() throws Exception {
        given(simpleUserService.deleteById(1L)).willReturn(true);

        mvc.perform(delete(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void deleteUser_whenNoUser_thenReturn204() throws Exception {
        given(simpleUserService.deleteById(1L)).willReturn(false);

        mvc.perform(delete(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void putUser_whenUserExists_thenReturn200() throws Exception {
        var updateUserJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                "01-01-1991",
                "Address str. Address DC, ADR",
                "+380927364527");

        given(simpleUserService.replace(ArgumentMatchers.any(User.class))).willReturn(true);

        mvc.perform(put(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    void putUser_whenUserDoesNotExist_thenReturn200() throws Exception {
        var updateUserJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                "01-01-1991",
                "Address str. Address DC, ADR",
                "+380927364527");

        given(simpleUserService.replace(ArgumentMatchers.any(User.class))).willReturn(false);

        mvc.perform(put(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void putUser_whenRequiredFieldsAreMissing_thenReturn400() throws Exception {
        var userJson = userDataJson(null,
                null,
                null,
                null,
                null,
                "Address str. Address DC, ADR",
                "+380927364527");

        given(simpleUserService.replace(ArgumentMatchers.any(User.class))).willReturn(true);

        mvc.perform(put(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors..message", containsInAnyOrder(
                        "Field [data.email] is not valid.",
                        "Field [data.firstName] is not valid.",
                        "Field [data.lastName] is not valid.",
                        "Field [data.birthDate] is not valid.")));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {"01-01-2099", "01-01-2023"})
    @NullSource
    @EmptySource
    void putUser_whenBirthDateIsNotWithingAllowedRange_thenReturn400(String birthDate) throws Exception {
        var userJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                birthDate,
                "Address str. Address DC, ADR",
                "+380927364527");

        mvc.perform(put(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message",
                        is("Field [data.birthDate] is not valid.")));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {
            "01-01-209f9",
            "o1-01-2023",
            "awdawdawd",
            "ad-da01-dwa2099",
            "0112-01-2023",
            "2023-1-16",
            "2023-16-1",
            "16-1-2023",
            "1o-1-1990",
            "1"
    })
    void putUser_whenBirthDateIsNotValidFormat_thenReturn400(String birthDate) throws Exception {
        var userJson = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                birthDate,
                "Address str. Address DC, ADR",
                "+380927364527");

        mvc.perform(put(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message",
                        is("Input date [" + birthDate + "] is not valid or has a wrong format.")));
    }

    @Test
    void patchUser_whenUserExists_thenReturn200() throws Exception {
        var userPatch = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                "01-01-1991",
                "Address str. Address DC, ADR",
                "+380927364527");

        given(simpleUserService.patch(anyLong(), ArgumentMatchers.any()))
                .willReturn(true);

        mvc.perform(patch(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPatch))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void patchUser_whenUserDoesNotExist_thenReturn404() throws Exception {
        var userPatch = userDataJson(null,
                "email@email.com",
                "John",
                "Doe",
                "01-01-1991",
                "Address str. Address DC, ADR",
                "+380927364527");

        given(simpleUserService.patch(anyLong(), ArgumentMatchers.any()))
                .willReturn(false);

        mvc.perform(patch(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPatch))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @ParameterizedTest(name = "Test [{0}]")
    @ValueSource(strings = {
            "01-01-209f9",
            "o1-01-2023",
            "awdawdawd",
            "ad-da01-dwa2099",
            "0112-01-2023",
            "2023-1-16",
            "2023-16-1",
            "16-1-2023",
            "1o-1-1990",
            "1"
    })
    void patchUser_whenInvalidDate_thenReturn400(String date) throws Exception {
        var userPatch = userDataJson(null,
                null,
                null,
                null,
                date,
                null,
                null);

        given(simpleUserService.patch(anyLong(), ArgumentMatchers.any()))
                .willReturn(true);

        mvc.perform(patch(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPatch))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors[0].message",
                        is("Input date [" + date + "] is not valid or has a wrong format.")));
    }

    @Test
    void patchUser_whenEmpty_thenReturn404() throws Exception {
        var userPatch = userDataJson(null,
                "",
                "",
                "",
                "",
                null,
                null);

        given(simpleUserService.patch(anyLong(), ArgumentMatchers.any()))
                .willReturn(true);

        mvc.perform(patch(API_USERS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPatch))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].status", is(equalTo(400))))
                .andExpect(jsonPath("$.errors..message", hasItems(
                        "Field [data.email] is not valid.",
                        "Field [data.firstName] is not valid.",
                        "Field [data.lastName] is not valid.")));
    }

    private String dateToPattern(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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
