package com.study.todo.user.controller;

import com.study.todo.security.CustomUserDetailsService;
import com.study.todo.user.domain.User;
import com.study.todo.user.dto.UserRequestDto;
import com.study.todo.user.dto.UserResponseDto;
import com.study.todo.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(UserController.class)
public class UserRestdocsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    CustomUserDetailsService userDetailsService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(MockMvcRequestBuilders.post("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .defaultRequest(MockMvcRequestBuilders.delete("/**").with(csrf()))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    @WithMockUser(username = "testuser")
    public void testSignup() throws Exception {
        String userJson = "{\"username\":\"testuser\", \"password\":\"password\", \"nickname\":\"테스트유저\"}";

        when(userService.signup(any(UserRequestDto.class))).thenReturn(new UserResponseDto(1L, "testuser", "테스트유저"));

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType("application/json")
                .content(userJson))
                .andExpect(status().isOk())
                .andDo(document("user-signup",
                        requestFields(
                                fieldWithPath("username").description("The user's username"),
                                fieldWithPath("password").description("The user's password"),
                                fieldWithPath("nickname").description("The user's nickname")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("username").description("The user's username"),
                                fieldWithPath("nickname").description("The user's nickname")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 테스트")
    @WithMockUser(username = "testuser")
    public void testLogin() throws Exception {
        User user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder().encode("password"))
                .nickname("테스트유저")
                .build();

        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>()));

        when(userService.login(any(UserRequestDto.class))).thenReturn(new UserResponseDto(1L, "testuser", "테스트유저"));

        String loginJson = "{\"username\":\"testuser\", \"password\":\"password\"}";

        mockMvc.perform(post("/api/v1/users/login")
                .contentType("application/json")
                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        requestFields(
                                fieldWithPath("username").description("The user's username"),
                                fieldWithPath("password").description("The user's password")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The user's ID"),
                                fieldWithPath("username").description("The user's username"),
                                fieldWithPath("nickname").description("The user's nickname")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser(username = "testuser")
    public void testLogout() throws Exception {
        mockMvc.perform(post("/api/v1/users/logout"))
                .andExpect(status().isOk())
                .andDo(document("user-logout"));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockUser(username = "testuser")
    public void testWithdraw() throws Exception {
        //doNothing().when(userService).withdraw(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                )
                .andExpect(status().isOk())
                .andDo(document("user-withdraw",
                        pathParameters(

                        )
                ));
    }
}
