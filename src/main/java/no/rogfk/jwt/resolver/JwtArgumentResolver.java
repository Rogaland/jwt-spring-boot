package no.rogfk.jwt.resolver;

import no.rogfk.jwt.SpringJwtTokenizer;
import no.rogfk.jwt.annotations.JwtVariable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class JwtArgumentResolver implements HandlerMethodArgumentResolver {

    private final SpringJwtTokenizer springJwtTokenizer;

    public JwtArgumentResolver(SpringJwtTokenizer springJwtTokenizer) {
        this.springJwtTokenizer = springJwtTokenizer;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        JwtVariable annotation = methodParameter.getParameterAnnotation(JwtVariable.class);
        return (annotation != null);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String token = nativeWebRequest.getParameter("jwt");
        return springJwtTokenizer.unwrap(methodParameter.getParameterType(), token);
    }
}
