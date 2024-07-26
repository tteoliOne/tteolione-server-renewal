package site.tteolione.tteolione;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "private"})
@SpringBootTest
public abstract class IntegrationTestSupport {
}
