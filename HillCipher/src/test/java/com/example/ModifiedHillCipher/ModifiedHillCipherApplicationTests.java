package com.example.ModifiedHillCipher;

import com.example.ModifiedHillCipher.controller.HillCipherController;
import com.example.ModifiedHillCipher.model.User;
import com.example.ModifiedHillCipher.repository.UserRepository;
import com.example.ModifiedHillCipher.service.HillCipherService;
import com.example.ModifiedHillCipher.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HillCipherController.class)
@AutoConfigureMockMvc
class ModifiedHillCipherApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private HillCipherService hillCipherService;

	@Nested
	class UserRegistrationTest {

		@MockBean
		private UserRepository userRepository;

		@Test
		void testUserRegistration() throws Exception {
			User user = new User("testUser", "test@example.com", "password");
			when(userRepository.save(any(User.class))).thenReturn(user);

			mockMvc.perform(get("/register")
							.param("username", "testUser")
							.param("email", "test@example.com")
							.param("password", "password"))
					.andExpect(status().isOk())
					.andExpect(view().name("registration-successful"));

			verify(userRepository, times(1)).save(any(User.class));
		}
	}

	@Nested
	class HillCipherControllerTest {

		@WithMockUser(username = "user")
		@Test
		void testLoginPage() throws Exception {
			mockMvc.perform(get("/login"))
					.andExpect(status().isOk())
					.andExpect(view().name("login"));
		}

		@WithMockUser(username = "user")
		@Test
		void testHomePageWithLogin() throws Exception {
			mockMvc.perform(get("/home"))
					.andExpect(status().isOk())
					.andExpect(view().name("home"));
		}

		@Test
		void testHomePageAccessDeniedWithoutLogin() throws Exception {
			mockMvc.perform(get("/home"))
					.andExpect(status().is3xxRedirection());
		}
	}
}
