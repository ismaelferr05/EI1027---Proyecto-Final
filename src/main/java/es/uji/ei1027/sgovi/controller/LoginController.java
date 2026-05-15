package es.uji.ei1027.sgovi.controller;

import jakarta.servlet.http.HttpSession;
import es.uji.ei1027.sgovi.dao.UserDao;
import es.uji.ei1027.sgovi.model.UserDetails;


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
    @Autowired
    private UserDao userDao;

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("user") != null ? "redirect:/dashboard" : "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
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
        UserDetails userDetails = userDao.getUserByEmail(user.getEmail());
        if (userDetails == null || !userDetails.getPassword().equals(user.getPassword())) {
            bindingResult.rejectValue("email", "error.user", "Constraseña o email inválidos");
            return "login";
        }
        session.setAttribute("user", userDetails);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        UserDetails user = (UserDetails) session.getAttribute("user");
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
}
