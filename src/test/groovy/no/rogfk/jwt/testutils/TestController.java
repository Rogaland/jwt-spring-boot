package no.rogfk.jwt.testutils;

import no.rogfk.jwt.annotations.JwtParam;
import no.rogfk.jwt.claims.Claim;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {


    @RequestMapping("/standard-claim")
    public Claim test(@JwtParam(name = "jwt") Claim claim) {
        return claim;
    }

    @RequestMapping("/custom-claim")
    public TestDto test(@JwtParam TestDto testDto) {
        return testDto;
    }

    @RequestMapping("/no-claim/{id}")
    public String test(@PathVariable String id) {
        return id;
    }

}
