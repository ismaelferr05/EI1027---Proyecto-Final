package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/formador")
public class FormadorController {

    @Autowired
    private FormadorDao formadorDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("formadores", formadorDao.getAll());
        return "formador/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("formador", new Formador());
        return "formador/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Formador formador) {
        formadorDao.add(formador);
        return "redirect:/formador/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("formador", formadorDao.get(id));
        return "formador/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Formador formador) {
        formadorDao.update(formador);
        return "redirect:/formador/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        formadorDao.delete(id);
        return "redirect:/formador/list";
    }
}
