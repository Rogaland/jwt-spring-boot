package no.rogfk.tokenizer.config.integration

import no.rogfk.tokenizer.config.ClaimsConfig
import no.rogfk.tokenizer.testutils.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

@IntegrationTest
@SpringApplicationConfiguration(classes = TestApplication)
class ClaimsConfigIntegrationSpec extends Specification {

    @Autowired
    private ClaimsConfig claimsConfig

    def "Initialize config"() {
        when:
        def key = claimsConfig.key
        def issuer = claimsConfig.issuer

        then:
        key != null
        issuer == "test-org"
    }
}
