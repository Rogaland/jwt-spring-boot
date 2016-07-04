package no.rogfk.tokenizer.testutils;

import no.rogfk.tokenizer.annotation.EnableJwtTokenizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJwtTokenizer(issuer = "test")
@SpringBootApplication
public class TestApplication {
}
