package no.rogfk.jwt.resolver;

import no.rogfk.jwt.SpringJwtTokenizer;
import no.rogfk.jwt.annotations.JwtParam;
import no.rogfk.jwt.exceptions.MissingJwtParamException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.springframework.util.StringUtils.isEmpty;

public class JwtArgumentResolver implements HandlerMethodArgumentResolver {

    private final SpringJwtTokenizer springJwtTokenizer;

    public JwtArgumentResolver(SpringJwtTokenizer springJwtTokenizer) {
        this.springJwtTokenizer = springJwtTokenizer;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        JwtParam annotation = methodParameter.getParameterAnnotation(JwtParam.class);
        return (annotation != null);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        JwtParam annotation = methodParameter.getParameterAnnotation(JwtParam.class);
        String parameterName = (isEmpty(annotation.name())) ? methodParameter.getParameterName() : annotation.name();
        String token = nativeWebRequest.getParameter(parameterName);

        if (isEmpty(token)) {
            throw new MissingJwtParamException("The JwtParam is missing, expected param: " + parameterName);
        } else {
            return springJwtTokenizer.parse(token, methodParameter.getParameterType());
        }
    }
}
