package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.model.OviUser;
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

    @PostMapping("/add")
    public String add(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/add";
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

    @PostMapping("/edit")
    public String edit(@ModelAttribute("oviUser") OviUser oviUser, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        OviUserValidator oviUserValidator = new OviUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "oviuser/edit";
        }

        oviUserDao.update(oviUser);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario OVI editado correctamente.");
        return "redirect:/ovi-users/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        oviUserDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario OVI eliminado correctamente.");
        return "redirect:/ovi-users/list";
    }
}

