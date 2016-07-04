package no.rogfk.jwt.testutils;

import no.rogfk.jwt.annotations.EnableJwt;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJwt(issuer = "test")
@SpringBootApplication
public class TestApplication {
}
