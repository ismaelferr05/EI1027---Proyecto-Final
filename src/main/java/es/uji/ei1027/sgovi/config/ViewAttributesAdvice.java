package es.uji.ei1027.sgovi.config;

import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class ViewAttributesAdvice {

    @ModelAttribute
    public void addSessionAttributes(HttpSession session, HttpServletRequest request, org.springframework.ui.Model model) {
        UserDetails currentUser = null;
        if (session != null) {
            Object value = session.getAttribute("user");
            if (value instanceof UserDetails userDetails) {
                currentUser = userDetails;
            }
        }

        String currentRole = currentUser != null ? currentUser.getRole() : null;
        boolean loggedIn = currentUser != null;

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isTechnician", "TECNICO".equals(currentRole));
        model.addAttribute("isOviUser", "OVIUSER".equals(currentRole));
        model.addAttribute("isPapPati", "PAPPATI".equals(currentRole));
        model.addAttribute("topbarMode", resolveTopbarMode(request, loggedIn));
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("today", LocalDate.now());
    }

    private String resolveTopbarMode(HttpServletRequest request, boolean loggedIn) {
        String path = request.getRequestURI();
        if ("/login".equals(path)) {
            return "none";
        }
        if (path.startsWith("/ovi-users/register")) {
            return "guest-ovi-register";
        }
        if (path.startsWith("/pap-patis/register")) {
            return "guest-pappati-register";
        }
        if (path.startsWith("/ovi-users/track") || path.startsWith("/pap-patis/track")) {
            return "guest-minimal";
        }
        if (loggedIn) {
            return "app";
        }
        return "guest-full";
    }
}
