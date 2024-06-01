package com.study.todo.user.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.study.todo.user.security.CustomUserDetailsService;
import com.study.todo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    CustomUserDetailsService userDetailsService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(post("/**").with(csrf()))
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void testSignup() throws Exception {
        String userJson = "{\"username\":\"testuser\", \"password\":\"password\", \"nickname\":\"테스트유저\"}";

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() throws Exception {
        mockMvc.perform(formLogin("/api/v1/users/login").user("testuser").password("password"))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser(username = "testuser")
    public void testLogout() throws Exception {
        mockMvc.perform(logout("/api/v1/users/logout"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockUser(username = "testuser")
    public void testWithdraw() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
    }
}