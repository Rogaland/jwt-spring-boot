package no.rogfk.tokenizer.claims;

import lombok.Data;

@Data
public class DefaultClaim {
    private String iss;
    private String iat;

    public String getIssuer() {
        return iss;
    }

    public String getIssuedAt() {
        return iat;
    }
}
