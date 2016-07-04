package no.rogfk.tokenizer.claims;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "name")
public class Claim {
    private final String name;
    private final String value;

    public Claim(String name, long value) {
        this.name = name;
        this.value = String.valueOf(value);
    }
}
