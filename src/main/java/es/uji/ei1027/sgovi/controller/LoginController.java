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

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "login"; // Devuelve el nombre de la vista para el formulario de inicio de sesión
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
            bindingResult.rejectValue("email", "error.user", "Invalid email or password");
            return "login"; // Si el usuario no existe o la contraseña es incorrecta, vuelve a mostrar el formulario de inicio de sesión con un mensaje de error
        }
        session.setAttribute("user", userDetails); // Guarda los detalles del usuario en la sesión
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalida la sesión para cerrar la sesión del usuario
        return "redirect:/login";
    }
}
