package no.rogfk.tokenizer.testutils;

import lombok.Data;
import no.rogfk.tokenizer.claims.DefaultClaim;

@Data
public class TestDto extends DefaultClaim {
    private String text1;
    private String text2;
}
