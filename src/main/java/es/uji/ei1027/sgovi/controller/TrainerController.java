package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Trainer;
import es.uji.ei1027.sgovi.service.TableViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping("/trainers")
public class TrainerController {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TableViewService tableViewService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        Map<String, Function<Trainer, ?>> sorters = new LinkedHashMap<>();
        sorters.put("name", Trainer::getName);
        sorters.put("lastName", Trainer::getLastName);
        sorters.put("occupation", Trainer::getOccupation);
        sorters.put("email", Trainer::getEmail);
        sorters.put("phone", Trainer::getPhone);
        sorters.put("address", Trainer::getAddress);
        sorters.put("id", Trainer::getIdTrainer);

        model.addAttribute("trainers", tableViewService.apply(trainerDao.getAll(), q, sort, dir, sorters,
                tableViewService.fields(Trainer::getIdTrainer, Trainer::getName, Trainer::getLastName,
                        Trainer::getOccupation, Trainer::getEmail, Trainer::getPhone, Trainer::getAddress)));
        tableViewService.addState(model, "/trainers/list", q, sort, dir,
                tableViewService.options("name", "Nombre", "lastName", "Apellido", "occupation", "Ocupación", "email", "Email", "phone", "Teléfono", "address", "Dirección", "id", "ID"));
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
