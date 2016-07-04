package no.rogfk.tokenizer.testutils;

import no.rogfk.tokenizer.annotations.EnableJwt;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJwt(issuer = "test")
@SpringBootApplication
public class TestApplication {
}
