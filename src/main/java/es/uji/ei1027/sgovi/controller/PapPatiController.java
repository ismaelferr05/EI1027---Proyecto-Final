package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        PapPati papPati = new PapPati();
        papPati.setStatus("PENDING");
        model.addAttribute("papPati", papPati);
        return "pappati/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pappati/add";
        }

        papPatiDao.add(papPati);
        redirectAttributes.addFlashAttribute("successMessage", "PAP PATI creado correctamente.");
        return "redirect:/pap-patis/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("papPati", papPatiDao.get(id));
        return "pappati/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pappati/edit";
        }

        papPatiDao.update(papPati);
        return "redirect:/pap-patis/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        papPatiDao.delete(id);
        return "redirect:/pap-patis/list";
    }
}

