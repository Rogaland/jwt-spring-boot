package no.rogfk.jwt.integration

import no.rogfk.jwt.SpringJwtTokenizer
import no.rogfk.jwt.config.SpringJwtTokenizerConfig
import no.rogfk.jwt.testutils.TestApplication
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification

@IntegrationTest
@SpringApplicationConfiguration(classes = TestApplication)
class EnableJwtTokenizerIntegrationSpec extends Specification {

    @Autowired
    private ConfigurableApplicationContext applicationContext

    @Autowired
    private SpringJwtTokenizerConfig springJwtTokenizerConfig


    @Autowired
    private SpringJwtTokenizer springJwtTokenizer

    @Autowired
    private StringEncryptor stringEncryptor

    def "Valid initialization"() {
        when:
        def validators = springJwtTokenizer.claimValidators

        then:
        noExceptionThrown()
        validators.size() == 2
        stringEncryptor != null
    }

}
