package com.jskillcloud.authsvc.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskillcloud.authsvc.dto.ApiResponse;
import com.jskillcloud.authsvc.dto.JwtAuthenticationResponse;
import com.jskillcloud.authsvc.dto.LoginRequest;
import com.jskillcloud.authsvc.dto.SignUpRequest;
import com.jskillcloud.authsvc.mapper.UserMapper;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserMapper userMapper;

    SignUpRequest signUpRequest;

    @Before
    public void setUp() {

        userMapper.deleteAll();

        signUpRequest = SignUpRequest.builder()
                .username("testUsername")
                .password("testPassword")
                .build();
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testSignUpSuccess() throws Exception {
        this.signUpSuccess();
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

    @Test
    public void testSignUpUsernameExists() throws Exception {

        this.signUpSuccess();

        MvcResult mvcResult = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);
        assertThat(apiResponse.getSuccess()).isFalse();
        assertThat(apiResponse.getMessage()).isEqualTo("Username is already taken!");
    }

    @Test
    public void testLoginWithUsernameSuccess() throws Exception {

        this.signUpSuccess();

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
    }

    @Test
    public void testLoginWithWrongUsername() throws Exception {

        this.signUpSuccess();

        LoginRequest loginRequest = LoginRequest.builder()
                .username("wrongUsername")
                .password("testPassword")
                .build();
        MvcResult mvcResult = mockMvc.perform(post("/api/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testLoginWithWrongPassword() throws Exception {

        this.signUpSuccess();

        LoginRequest loginRequest = LoginRequest.builder()
                .username("testUsername")
                .password("wrongPassword")
                .build();
        MvcResult mvcResult = mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @After
    public void destroy() {
        userMapper.deleteAll();
    }
}
