package es.uji.ei1027.sgovi.controller;

import jakarta.servlet.http.HttpSession;
import es.uji.ei1027.sgovi.dao.UserDao;
import es.uji.ei1027.sgovi.model.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private es.uji.ei1027.sgovi.service.PasswordService passwordService;

    @GetMapping("/")
    public String home(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        return user != null ? redirectToWorkspace(user) : "redirect:/index.html";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            return redirectToWorkspace((UserDetails) session.getAttribute("user"));
        }
        model.addAttribute("user", new UserDetails());
        return "login";
    }

    @PostMapping("/login")
    public String checkLogin(@ModelAttribute("user") UserDetails user, BindingResult bindingResult, HttpSession session){
        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login"; // Si hay errores de validación, vuelve a mostrar el formulario de inicio de sesión
        }
        try {
            UserDetails userDetails = userDao.getUserByEmail(user.getEmail());
            if (userDetails == null || !passwordService.check(user.getPassword(), userDetails.getPassword())) {
                bindingResult.rejectValue("email", "error.user", "Contraseña o email inválidos");
                return "login";
            }
            session.setAttribute("user", userDetails);
            return redirectToWorkspace(userDetails);
        } catch (Exception ex) {
            log.error("Error inesperado durante el login para {}", user.getEmail(), ex);
            bindingResult.rejectValue("email", "error.user", "No se pudo iniciar sesión. Revisa los datos o inténtalo de nuevo.");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user.getRole() != null) {
            return redirectToWorkspace(user);
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("isTechnician", "TECNICO".equals(user.getRole()));
        model.addAttribute("isOviUser", "OVIUSER".equals(user.getRole()));
        model.addAttribute("isPapPati", "PAPPATI".equals(user.getRole()));
        return "dashboard";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectToWorkspace(UserDetails user) {
        String role = user.getRole();
        if ("TECNICO".equals(role)) {
            return "redirect:/requests/list";
        }
        if ("OVIUSER".equals(role)) {
            return "redirect:/requests/frontoffice/track";
        }
        return "redirect:/messages/list";
    }
}
