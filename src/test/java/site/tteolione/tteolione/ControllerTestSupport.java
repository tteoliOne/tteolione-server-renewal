package site.tteolione.tteolione;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import site.tteolione.tteolione.api.controller.email.EmailController;
import site.tteolione.tteolione.api.controller.user.AuthController;
import site.tteolione.tteolione.api.controller.user.UserController;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.api.service.user.AuthService;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.config.redis.RedisUtil;

@ActiveProfiles({"test", "private"})
@WebMvcTest(controllers = {
        UserController.class,
        EmailController.class,
        AuthController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected RedisUtil redisUtil;

    @MockBean
    protected UserService userService;

    @MockBean
    protected EmailService emailService;

    @MockBean
    protected AuthService authService;

}
