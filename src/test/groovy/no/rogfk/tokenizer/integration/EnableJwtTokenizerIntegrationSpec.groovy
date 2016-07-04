package no.rogfk.tokenizer.integration

import no.rogfk.tokenizer.SpringJwtTokenizer
import no.rogfk.tokenizer.config.SpringJwtTokenizerConfig
import no.rogfk.tokenizer.testutils.TestApplication
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
