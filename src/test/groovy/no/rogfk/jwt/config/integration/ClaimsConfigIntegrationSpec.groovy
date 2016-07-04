package no.rogfk.jwt.config.integration

import no.rogfk.jwt.config.ClaimsConfig
import no.rogfk.jwt.testutils.TestApplication
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
