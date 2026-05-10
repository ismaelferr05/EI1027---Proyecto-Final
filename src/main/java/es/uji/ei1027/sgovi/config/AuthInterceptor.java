package es.uji.ei1027.sgovi.config;

import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        UserDetails currentUser = session != null ? (UserDetails) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (isAllowedForRole(path, currentUser.getRole())) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
        return false;
    }

    private boolean isPublicPath(String path) {
        return "/".equals(path)
                || "/login".equals(path)
                || "/logout".equals(path)
                || "/error".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || "/favicon.ico".equals(path)
                || "/index.html".equals(path);
    }

    private boolean isAllowedForRole(String path, String role) {
        if ("TECNICO".equals(role)) {
            return true;
        }

        if ("OVIUSER".equals(role)) {
            return "/dashboard".equals(path)
                    || "/requests/list".equals(path)
                    || path.startsWith("/requests/frontoffice")
                    || "/messages/list".equals(path);
        }

        if ("PAPPATI".equals(role)) {
            return "/dashboard".equals(path)
                    || "/requests/list".equals(path)
                    || "/messages/list".equals(path);
        }

        return false;
    }
}


