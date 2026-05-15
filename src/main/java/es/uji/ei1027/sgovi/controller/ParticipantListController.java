package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ParticipantListDao;
import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/participant-lists")
public class ParticipantListController {

    @Autowired
    private ParticipantListDao participantListDao;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("participantLists", participantListDao.getAll());
        return "participantlist/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        ParticipantList participantList = new ParticipantList();
        participantList.setAttendance(false);
        model.addAttribute("participantList", participantList);
        model.addAttribute("activities", activityDao.getAll());
        model.addAttribute("oviUsers", oviUserDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "participantlist/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("participantList") ParticipantList participantList, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        ParticipantListValidator participantListValidator = new ParticipantListValidator();
        participantListValidator.validate(participantList, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activities", activityDao.getAll());
            model.addAttribute("oviUsers", oviUserDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            return "participantlist/add";
        }

        participantListDao.add(participantList);
        redirectAttributes.addFlashAttribute("successMessage", "Participante añadido correctamente.");
        return "redirect:/participant-lists/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("participantList", participantListDao.get(id));
        model.addAttribute("activities", activityDao.getAll());
        model.addAttribute("oviUsers", oviUserDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "participantlist/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("participantList") ParticipantList participantList, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        ParticipantListValidator participantListValidator = new ParticipantListValidator();
        participantListValidator.validate(participantList, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activities", activityDao.getAll());
            model.addAttribute("oviUsers", oviUserDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            return "participantlist/edit";
        }

        participantListDao.update(participantList);
        redirectAttributes.addFlashAttribute("successMessage", "Participante editado correctamente.");
        return "redirect:/participant-lists/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        participantListDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Participante eliminado correctamente.");
        return "redirect:/participant-lists/list";
    }
}

