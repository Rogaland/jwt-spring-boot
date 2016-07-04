package no.rogfk.jwt.integration

import io.jsonwebtoken.Claims
import no.rogfk.jwt.SpringJwtTokenizer
import no.rogfk.jwt.claims.Claim
import no.rogfk.jwt.exceptions.ClaimValidatorException
import no.rogfk.jwt.exceptions.InvalidTokenException
import no.rogfk.jwt.testutils.TestApplication
import no.rogfk.jwt.testutils.TestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

@IntegrationTest
@SpringApplicationConfiguration(classes = TestApplication)
class SpringJwtTokenizerIntegrationSpec extends Specification {

    @Autowired
    private SpringJwtTokenizer springJwtTokenizer


    def "Unwrap invalid token"() {
        when:
        springJwtTokenizer.unwrap("testing")

        then:
        thrown InvalidTokenException
    }

    def "Wrap and unwrap standard and custom claims"() {
        given:
        def claim1 = new Claim("test-claim", "test-value1")
        def claim2 = new Claim("test-claim2", "test-value2")

        when:
        def token = springJwtTokenizer.wrap(claim1, claim2)
        def claims = springJwtTokenizer.unwrap(token)

        then:
        token != null
        claims.size() == 4 // 2 standard claims, issuer and issuedAt
        contains(claims, Claims.ISSUER)
        contains(claims, Claims.ISSUED_AT)
        contains(claims, "test-claim")
        contains(claims, "test-claim2")
    }

    def "Wrap and unwrap, failing claim validation with custom exception message"() {
        given:
        def claim1 = new Claim("test-claim2", "test-value1")
        def claim2 = new Claim("test-claim", "fail")

        when:
        def token = springJwtTokenizer.wrap(claim1, claim2)
        springJwtTokenizer.unwrap(token)

        then:
        ClaimValidatorException exception = thrown()
        exception.message == "Test exception"
    }

    def "Wrap and unwrap, single value"() {
        when:
        def token = springJwtTokenizer.wrap(new TestDto(text1: "value1"))
        def unwrapped = springJwtTokenizer.unwrap("text1", token)

        then:
        unwrapped.get().value == "value1"
    }

    def "Wrap and unwrap, custom dto"() {
        given:
        TestDto testDto = new TestDto(text1: "value1", text2: "value2")

        when:
        def token = springJwtTokenizer.wrap(testDto)
        def unwrapped = springJwtTokenizer.unwrap(TestDto, token)

        then:
        unwrapped.text1 == "value1"
        unwrapped.text2 == "value2"
        unwrapped.issuer == "test-org"
        unwrapped.issuedAt != null
    }

    private static boolean contains(Set<Claim> claims, String name) {
        return claims.find { it.name == name }
    }
}
