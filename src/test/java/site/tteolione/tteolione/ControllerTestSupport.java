package site.tteolione.tteolione;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import site.tteolione.tteolione.api.controller.email.EmailController;
import site.tteolione.tteolione.api.controller.product.ProductController;
import site.tteolione.tteolione.api.controller.user.AuthController;
import site.tteolione.tteolione.api.controller.user.OAuth2Controller;
import site.tteolione.tteolione.api.controller.user.UserController;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.api.service.product.ProductService;
import site.tteolione.tteolione.api.service.user.AuthService;
import site.tteolione.tteolione.api.service.user.OAuth2Service;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.common.config.redis.RedisUtil;

@ActiveProfiles({"test", "private"})
@WebMvcTest(controllers = {
        UserController.class,
        EmailController.class,
        AuthController.class,
        ProductController.class,
        OAuth2Controller.class
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

    @MockBean
    protected ProductService productService;

    @MockBean
    protected OAuth2Service oAuth2Service;

    /**
     * 403 forbidden 문제(csrf)
     * https://velog.io/@shwncho/Spring-security-6-Controller-test-403-Forbidden-%EC%97%90%EB%9F%ACfeat.-csrf
     */
    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(MockMvcRequestBuilders.multipart("/api/v2/products").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.get("/api/v2/users/check/nickname").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.get("/api/v2/users/nickname").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.post("/api/v2/auth/kakao").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.post("/api/v2/auth/kakao/profile").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.post("/api/v2/products/**").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .defaultRequest(MockMvcRequestBuilders.get("/api/v2/products/**").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .build();
    }

}
