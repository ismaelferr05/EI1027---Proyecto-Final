package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trainers")
public class TrainerController {

    @Autowired
    private TrainerDao trainerDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("trainers", trainerDao.getAll());
        return "trainer/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("trainer", new Trainer());
        return "trainer/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("trainer") Trainer trainer, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        TrainerValidator trainerValidator = new TrainerValidator();
        trainerValidator.validate(trainer, bindingResult);

        if (bindingResult.hasErrors()) {
            return "trainer/add";
        }

        trainerDao.add(trainer);
        redirectAttributes.addFlashAttribute("successMessage", "Formador creado correctamente.");
        return "redirect:/trainers/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("trainer", trainerDao.get(id));
        return "trainer/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("trainer") Trainer trainer, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        TrainerValidator trainerValidator = new TrainerValidator();
        trainerValidator.validate(trainer, bindingResult);

        if (bindingResult.hasErrors()) {
            return "trainer/edit";
        }

        trainerDao.update(trainer);
        redirectAttributes.addFlashAttribute("successMessage", "Formador editado correctamente.");
        return "redirect:/trainers/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        trainerDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Formador eliminado correctamente.");
        return "redirect:/trainers/list";
    }
}

