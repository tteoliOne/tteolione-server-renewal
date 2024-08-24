package site.tteolione.tteolione.common.config.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "site.tteolione.tteolione")
public class OpenfeignConfig {}