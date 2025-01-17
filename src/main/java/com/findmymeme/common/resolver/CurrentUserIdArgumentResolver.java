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

import java.util.Optional;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) &&
                (parameter.getParameterType().equals(Long.class) ||
                (parameter.getParameterType().equals(Optional.class) &&
                                parameter.getParameterType().equals(Long.class)));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = (Authentication) webRequest.getUserPrincipal();

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

        throw new FindMyMemeException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
