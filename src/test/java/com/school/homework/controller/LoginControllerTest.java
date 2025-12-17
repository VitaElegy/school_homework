package com.school.homework.controller;

import com.school.homework.entity.User;
import com.school.homework.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    public void testRegisterSuccess() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");
        user.setEmail("new@example.com");

        given(userService.registerUser(any(User.class))).willReturn(user);

        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "newuser")
                .param("password", "password")
                .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    @WithMockUser
    public void testRegisterFailure_UsernameExists() throws Exception {
        given(userService.registerUser(any(User.class))).willThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/register")
                .with(csrf())
                .param("username", "existing")
                .param("password", "password")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }
}
