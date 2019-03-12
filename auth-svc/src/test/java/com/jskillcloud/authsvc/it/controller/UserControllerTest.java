package com.jskillcloud.authsvc.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskillcloud.authsvc.dto.*;
import com.jskillcloud.authsvc.mapper.UserMapper;
import com.jskillcloud.authsvc.model.RoleName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserMapper userMapper;

    SignUpRequest signUpRequest;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setUp() {
        userMapper.deleteAll();

        signUpRequest = SignUpRequest.builder()
                .username("testUsername")
                .password("testPassword")
                .build();
    }

    @Test
    public void testCheckUsernameAvailability() throws Exception {
        this.signUpSuccess();

        // not available case
        MvcResult mvcResult = mockMvc.perform(get("/user/checkUsernameAvailability")
                .param("username", "testUsername"))
                .andExpect(status().isOk())
                .andReturn();

        UserIdentityAvailability userIdentityAvailability =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserIdentityAvailability.class);
        assertThat(userIdentityAvailability.isAvailable()).isFalse();

        // available case
        mvcResult = mockMvc.perform(get("/user/checkUsernameAvailability")
                .param("username", "availableUsername"))
                .andExpect(status().isOk())
                .andReturn();

        userIdentityAvailability =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserIdentityAvailability.class);
        assertThat(userIdentityAvailability.isAvailable());
    }


    @Test
    public void testGetUserNoToken() throws Exception {
        this.signUpSuccess();

        MvcResult mvcResult = mockMvc.perform(get("/user/me"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetUserWithCorrectToken() throws Exception {
        // signUp
        this.signUpSuccess();

        // signIn
        String token = this.signInThenGetToken();

        // getUser
        MvcResult mvcResult = mockMvc.perform(get("/user/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        UserInfo userInfo =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserInfo.class);
        assertThat(userInfo.getId()).isNotNull();
        assertThat(userInfo.getUsername()).isEqualTo(signUpRequest.getUsername());
        assertThat(userInfo.getRoles().size()).isEqualTo(1);
        assertThat(userInfo.getRoles().get(0)).isEqualTo(RoleName.ROLE_USER.name());
    }

    @Test
    public void testGetUserWithWrongToken() throws Exception {
        // signUp
        this.signUpSuccess();

        // signIn
        String token = signInThenGetToken();

        // getUser
        mockMvc.perform(get("/user/me")
                .header("Authorization", "Bearer xxx" + token))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    private String signInThenGetToken() throws Exception {
        // signIn
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testUsername")
                .password("testPassword")
                .build();
        MvcResult mvcResult = mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtAuthenticationResponse jwtAuthenticationResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);
        assertThat(jwtAuthenticationResponse.getAccessToken()).isNotBlank();
        assertThat(jwtAuthenticationResponse.getTokenType()).isEqualTo("Bearer");

        return jwtAuthenticationResponse.getAccessToken();
    }

    private void signUpSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);
        assertThat(apiResponse.getSuccess()).isTrue();
        assertThat(apiResponse.getMessage()).isEqualTo("User registered successfully");
    }

    @After
    public void destroy() {
        userMapper.deleteAll();
    }

}
