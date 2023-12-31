package nextstep.subway.auth.ui.authorization;

import nextstep.subway.auth.infrastructure.SecurityContext;
import nextstep.subway.auth.infrastructure.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SecurityContextPersistenceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return true;
        }
        return proceed(request);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.clearContext();
    }

    protected abstract boolean proceed(HttpServletRequest request);

    protected void setSecurityContext(SecurityContext securityContext) {
        if (securityContext != null) {
            SecurityContextHolder.setContext(securityContext);
        }
    }
}
