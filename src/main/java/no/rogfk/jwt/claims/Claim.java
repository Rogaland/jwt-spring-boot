package no.rogfk.jwt.claims;

import java.util.Objects;

public class Claim {
    private String name;
    private String value;

    public Claim() {
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
