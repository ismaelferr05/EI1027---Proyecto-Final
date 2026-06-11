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

        if (isGuestOnlyPath(path)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                session.invalidate();
            }
            return true;
        }

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
                || isGuestOnlyPath(path)
                || "/logout".equals(path)
                || "/error".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || "/favicon.ico".equals(path)
                || "/index.html".equals(path);
    }

    private boolean isGuestOnlyPath(String path) {
        return path.startsWith("/ovi-users/register")
                || path.startsWith("/ovi-users/track")
                || path.startsWith("/pap-patis/register")
                || path.startsWith("/pap-patis/track");
    }

    private boolean isAllowedForRole(String path, String role) {
        if ("TECNICO".equals(role)) {
            return true;
        }

        if ("OVIUSER".equals(role)) {
            return "/dashboard".equals(path)
                    || "/requests/list".equals(path)
                    || path.startsWith("/ovi-users/profile")
                    || path.startsWith("/contracts/oviuser")
                    || path.startsWith("/contracts/add")
                    || path.startsWith("/contracts/edit")
                    || path.startsWith("/contracts/view/")
                    || path.startsWith("/communications")
                    || path.startsWith("/requests/frontoffice")
                    || path.startsWith("/activities/browse")
                    || path.matches("/activities/\\d+/signup")
                    || "/messages/list".equals(path)
                    || path.startsWith("/messages/chat/");
        }

        if ("PAPPATI".equals(role)) {
            return "/dashboard".equals(path)
                    || "/requests/list".equals(path)
                    || path.startsWith("/pap-patis/profile")
                    || path.startsWith("/pap-patis/track")
                    || path.startsWith("/communications")
                    || path.startsWith("/contracts/pappati")
                    || path.startsWith("/contracts/view/")
                    || path.startsWith("/activities/browse")
                    || path.matches("/activities/\\d+/signup")
                    || "/messages/list".equals(path)
                    || path.startsWith("/messages/chat/");
        }

        return false;
    }
}
