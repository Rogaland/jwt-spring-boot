package no.rogfk.tokenizer.annotation;

import no.rogfk.tokenizer.config.SpringJwtTokenizerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringJwtTokenizerConfig.class)
public @interface EnableJwtTokenizer {
    boolean encryption() default true;

    boolean standardValidators() default true;

    String issuer() default "";

    long maxAgeMinutes() default -1;
}
