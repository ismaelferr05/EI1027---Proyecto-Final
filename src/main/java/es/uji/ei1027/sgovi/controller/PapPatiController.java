package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pap-patis")
public class PapPatiController {

    @Autowired
    private PapPatiDao papPatiDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "pappati/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("papPati", new PapPati());
        return "pappati/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute PapPati papPati) {
        papPatiDao.add(papPati);
        return "redirect:/pap-patis/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("papPati", papPatiDao.get(id));
        return "pappati/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute PapPati papPati) {
        papPatiDao.update(papPati);
        return "redirect:/pap-patis/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        papPatiDao.delete(id);
        return "redirect:/pap-patis/list";
    }
}

