package es.uji.ei1027.sgovi.config;

import es.uji.ei1027.sgovi.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewAttributesAdvice {

    @ModelAttribute
    public void addSessionAttributes(HttpSession session, org.springframework.ui.Model model) {
        UserDetails currentUser = null;
        if (session != null) {
            Object value = session.getAttribute("user");
            if (value instanceof UserDetails userDetails) {
                currentUser = userDetails;
            }
        }

        String currentRole = currentUser != null ? currentUser.getRole() : null;
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("isLoggedIn", currentUser != null);
        model.addAttribute("isTechnician", "TECNICO".equals(currentRole));
        model.addAttribute("isOviUser", "OVIUSER".equals(currentRole));
        model.addAttribute("isPapPati", "PAPPATI".equals(currentRole));
    }
}

