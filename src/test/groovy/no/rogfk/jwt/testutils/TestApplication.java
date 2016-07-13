package no.rogfk.jwt.testutils;

import no.rogfk.jwt.annotations.EnableJwt;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJwt(encryption = true, issuer = "test")
@SpringBootApplication
public class TestApplication {
}
