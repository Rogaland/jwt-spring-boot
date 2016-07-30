package no.rogfk.jwt

import no.rogfk.jwt.claims.Claim
import no.rogfk.jwt.claims.validators.ClaimValidator
import no.rogfk.jwt.config.ClaimsConfig
import no.rogfk.jwt.config.SpringJwtTokenizerConfig
import no.rogfk.jwt.exceptions.InvalidTokenException
import spock.lang.Specification

class SpringJwtTokenizerSpec extends Specification {


    def "Parse invalid token"() {
        given:
        def config = new SpringJwtTokenizerConfig(claimsConfig: new ClaimsConfig(encryption: true),
                encryptorPassword: "test123".bytes, encryptorAlgorithm: "PBEWithMD5AndDES")
        config.init()
        SpringJwtTokenizer springJwtTokenizer = new SpringJwtTokenizer(encryptor: config.stringEncryptor())

        when:
        springJwtTokenizer.parse("testing")

        then:
        thrown InvalidTokenException
    }

    def "Create claims map"() {
        given:
        def validator1 = Mock(ClaimValidator) {
            name() >> "validator"
        }
        def validator2 = Mock(ClaimValidator) {
            name() >> "validator"
        }

        when:
        SpringJwtTokenizer springJwtTokenizer = new SpringJwtTokenizer([validator1, validator2], [new Claim("", "")])
        def validators = springJwtTokenizer.claimValidators

        then:
        validators.size() == 1
        validators.keySet()[0] == "validator"
        validators.values()[0].size() == 2
    }

}
