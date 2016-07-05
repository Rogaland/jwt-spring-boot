package no.rogfk.jwt.resolver.integration

import no.rogfk.jwt.SpringJwtTokenizer
import no.rogfk.jwt.claims.Claim
import no.rogfk.jwt.testutils.TestApplication
import no.rogfk.jwt.testutils.TestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpStatus
import spock.lang.Specification

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = TestApplication)
class JwtArgumentResolverIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    private int port
    private String baseUrl

    private TestRestTemplate testRestTemplate

    @Autowired
    private SpringJwtTokenizer springJwtTokenizer

    void setup() {
        testRestTemplate = new TestRestTemplate()
        baseUrl = "http://localhost:${port}/test"
    }

    def "@JwtVariable, standard claim"() {
        given:
        def token = springJwtTokenizer.create(new Claim(name: "test1", value: "value1"))

        when:
        def response = testRestTemplate.getForEntity("${baseUrl}/standard-claim?jwt=${token}", Claim)

        then:
        response.statusCode == HttpStatus.OK
        response.body.name == "test1"
        response.body.value == "value1"
    }

    def "@JwtVariable, custom claim"() {
        given:
        def token = springJwtTokenizer.create(new TestDto(text1: "value1", text2: "value2"))


        when:
        def response = testRestTemplate.getForEntity("${baseUrl}/custom-claim?jwt=${token}", TestDto)

        then:
        response.statusCode == HttpStatus.OK
        response.body.text1 == "value1"
        response.body.text2 == "value2"
    }

    def "Standard @PathVariable"() {
        when:
        def response = testRestTemplate.getForEntity("${baseUrl}/no-claim/123", String)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "123"
    }
}
