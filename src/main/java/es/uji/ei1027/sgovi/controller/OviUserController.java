package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.EmailContent;
import es.uji.ei1027.sgovi.service.EmailService;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ovi-users")
public class OviUserController {

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private es.uji.ei1027.sgovi.service.PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SessionUserService sessionUserService;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("oviUsers", oviUserDao.getAll());
        return "oviuser/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        OviUser oviUser = new OviUser();
        oviUser.setStatus("PENDING");
        oviUser.setLopdConsent(false);
        model.addAttribute("oviUser", oviUser);
        return "oviuser/add";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        OviUser oviUser = new OviUser();
        oviUser.setStatus("PENDING");
        model.addAttribute("oviUser", oviUser);
        return "oviuser/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, Model model) {
        oviUser.setStatus("PENDING");
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/register";
        }

        oviUser.setPassword(passwordService.encrypt(oviUser.getPassword()));
        oviUserDao.add(oviUser);
        model.addAttribute("trackedUser", oviUserDao.getByEmail(oviUser.getEmail()));
        return "oviuser/track";
    }

    @GetMapping("/track")
    public String trackForm() {
        return "oviuser/track";
    }

    @PostMapping("/track")
    public String track(@RequestParam("email") String email, Model model) {
        model.addAttribute("trackedUser", oviUserDao.getByEmail(email));
        model.addAttribute("searched", true);
        return "oviuser/track";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/add";
        }

        // Encriptar contraseña antes de persistir
        if (oviUser.getPassword() != null && !oviUser.getPassword().isBlank()) {
            oviUser.setPassword(passwordService.encrypt(oviUser.getPassword()));
        }
        oviUserDao.add(oviUser);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario OVI creado correctamente.");
        return "redirect:/ovi-users/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("oviUser", oviUserDao.get(id));
        return "oviuser/edit";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        OviUser current = sessionUserService.getCurrentOviUser(session);
        if (current == null) {
            return "redirect:/login";
        }
        current.setPassword("");
        model.addAttribute("oviUser", current);
        model.addAttribute("selfProfile", true);
        return "oviuser/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        OviUser existing = oviUserDao.get(oviUser.getIdOviUser());
        if (oviUser.getPassword() == null || oviUser.getPassword().isBlank()) {
            oviUser.setPassword(existing.getPassword());
        } else {
            oviUser.setPassword(passwordService.encrypt(oviUser.getPassword()));
        }
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/edit";
        }

        oviUserDao.update(oviUser);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario OVI editado correctamente.");
        return "redirect:/ovi-users/list";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        OviUser current = sessionUserService.getCurrentOviUser(session);
        if (current == null || current.getIdOviUser() != oviUser.getIdOviUser()) {
            return "redirect:/dashboard";
        }
        oviUser.setStatus(current.getStatus());
        oviUser.setRejectionReason(current.getRejectionReason());
        if (oviUser.getPassword() == null || oviUser.getPassword().isBlank()) {
            oviUser.setPassword(current.getPassword());
        } else {
            oviUser.setPassword(passwordService.encrypt(oviUser.getPassword()));
        }

        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);
        if (bindingResult.hasErrors()) {
            return "oviuser/edit";
        }
        oviUserDao.update(oviUser);
        redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
        return "redirect:/dashboard";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model) {
        OviUser user = oviUserDao.get(id);
        model.addAttribute("oviUser", user);
        model.addAttribute("contracts", contractDao.getByOviUserId(id));
        return "oviuser/view";
    }

    @PostMapping("/accept")
    public String accept(@RequestParam("idOviUser") int idOviUser, Model model) {
        OviUser user = oviUserDao.get(idOviUser);
        oviUserDao.updateStatus(idOviUser, "ACCEPTED", null);
        EmailContent email = emailService.sendUserStatusEmail(user.getEmail(), user.getName() + " " + user.getLastName(), "ACCEPTED", null);
        model.addAttribute("email", email);
        model.addAttribute("entityName", user.getName() + " " + user.getLastName());
        model.addAttribute("action", "acceptance");
        return "user-status-confirmation";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam("idOviUser") int idOviUser, @RequestParam("reason") String reason, Model model) {
        OviUser user = oviUserDao.get(idOviUser);
        oviUserDao.updateStatus(idOviUser, "REJECTED", reason);
        EmailContent email = emailService.sendUserStatusEmail(user.getEmail(), user.getName() + " " + user.getLastName(), "REJECTED", reason);
        model.addAttribute("email", email);
        model.addAttribute("entityName", user.getName() + " " + user.getLastName());
        model.addAttribute("action", "rejection");
        return "user-status-confirmation";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        oviUserDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario OVI eliminado correctamente.");
        return "redirect:/ovi-users/list";
    }
}
