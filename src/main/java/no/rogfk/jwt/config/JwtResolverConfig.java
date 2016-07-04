package no.rogfk.jwt.config;

import no.rogfk.jwt.SpringJwtTokenizer;
import no.rogfk.jwt.resolver.JwtArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class JwtResolverConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private SpringJwtTokenizer springJwtTokenizer;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new JwtArgumentResolver(springJwtTokenizer));
    }

}
