package no.rogfk.tokenizer.claims;

public class DefaultClaim {
    private String iss;
    private String iat;

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getIssuer() {
        return iss;
    }

    public String getIssuedAt() {
        return iat;
    }
}
