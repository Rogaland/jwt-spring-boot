package no.rogfk.jwt.config

import spock.lang.Specification

class SpringJwtTokenizerConfigSpec extends Specification {

    def "Require encryption, missing encryptor password"() {
        given:
        SpringJwtTokenizerConfig config = new SpringJwtTokenizerConfig(claimsConfig: new ClaimsConfig())

        when:
        config.init()

        then:
        thrown IllegalArgumentException
    }

    def "Encryption disabled, missing jwt key"() {
        given:
        SpringJwtTokenizerConfig config = new SpringJwtTokenizerConfig(claimsConfig: new ClaimsConfig(encryption: false))

        when:
        config.init()

        then:
        thrown IllegalArgumentException
    }
}
