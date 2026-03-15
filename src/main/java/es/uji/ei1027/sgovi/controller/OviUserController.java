package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.model.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ovil-users")
public class OviUserController {

    @Autowired
    private OviUserDao ovilUserDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("ovilUsers", ovilUserDao.getAll());
        return "oviluser/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("ovilUser", new OviUser());
        return "oviluser/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute OviUser ovilUser) {
        ovilUserDao.add(ovilUser);
        return "redirect:/ovil-users/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("ovilUser", ovilUserDao.get(id));
        return "oviluser/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute OviUser oviUser) {
        ovilUserDao.update(oviUser);
        return "redirect:/ovil-users/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        ovilUserDao.delete(id);
        return "redirect:/ovil-users/list";
    }
}

