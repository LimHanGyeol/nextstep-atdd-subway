package nextstep.subway.auth.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.auth.application.UserDetailsService;
import nextstep.subway.auth.ui.authentication.*;
import nextstep.subway.auth.ui.authorization.AuthenticationPrincipalArgumentResolver;
import nextstep.subway.auth.ui.authorization.LoginValidationInterceptor;
import nextstep.subway.auth.ui.authorization.SessionSecurityContextPersistenceInterceptor;
import nextstep.subway.auth.ui.authorization.TokenSecurityContextPersistenceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public AuthConfig(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthenticationInterceptor(userDetailsService, sessionAuthenticationConverter())).addPathPatterns("/login/session");
        registry.addInterceptor(new TokenAuthenticationInterceptor(userDetailsService, tokenAuthenticationConverter(), jwtTokenProvider, objectMapper)).addPathPatterns("/login/token");
        registry.addInterceptor(new SessionSecurityContextPersistenceInterceptor());
        registry.addInterceptor(new TokenSecurityContextPersistenceInterceptor(jwtTokenProvider, objectMapper));
        registry.addInterceptor(new LoginValidationInterceptor()).addPathPatterns("/favorites/**");
    }

    private AuthenticationConverter tokenAuthenticationConverter() {
        return new TokenAuthenticationConverter(objectMapper);
    }

    private AuthenticationConverter sessionAuthenticationConverter() {
        return new SessionAuthenticationConverter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver(userDetailsService));
    }
}
