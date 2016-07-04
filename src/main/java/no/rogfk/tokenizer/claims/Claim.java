package no.rogfk.tokenizer.claims;

import java.util.Objects;

public class Claim {
    private final String name;
    private final String value;

    public Claim(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Claim(String name, long value) {
        this.name = name;
        this.value = String.valueOf(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return Objects.equals(name, claim.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
