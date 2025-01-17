package com.findmymeme.common.resolver;

import com.findmymeme.exception.ErrorCode;
import com.findmymeme.exception.FindMyMemeException;
import com.findmymeme.user.domain.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(CurrentUserId.class)) {
            return false;
        }
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType.equals(Long.class)) {
            return true;
        }

        if (parameterType.equals(Optional.class)) {
            return parameter.getGenericParameterType() instanceof ParameterizedType parameterizedType
                    && parameterizedType.getActualTypeArguments()[0].equals(Long.class);
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = (Authentication) webRequest.getUserPrincipal();
        boolean isRequired = parameter.getParameterAnnotation(CurrentUserId.class).required();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            if (parameter.getParameterType().equals(Long.class)) {
                return userId;
            } else if (parameter.getParameterType().equals(Optional.class)) {
                return Optional.of(userId);
            }
        }

        if (parameter.getParameterType().equals(Optional.class)) {
            return Optional.empty();
        }

        if (isRequired) {
            throw new FindMyMemeException(ErrorCode.UNAUTHORIZED_USER);
        }
        return null;
    }
}
