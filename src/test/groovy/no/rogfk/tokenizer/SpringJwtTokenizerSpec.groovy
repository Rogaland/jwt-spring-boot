package no.rogfk.tokenizer

import no.rogfk.tokenizer.claims.Claim
import no.rogfk.tokenizer.claims.validators.ClaimValidator
import spock.lang.Specification

class SpringJwtTokenizerSpec extends Specification {
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
