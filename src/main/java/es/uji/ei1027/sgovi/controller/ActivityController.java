package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Activity;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private SessionUserService sessionUserService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        model.addAttribute("activities", activityDao.getAll());
        return "activity/list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        model.addAttribute("activity", new Activity());
        model.addAttribute("trainers", trainerDao.getAll());
        return "activity/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerDao.getAll());
            return "activity/add";
        }

        activityDao.add(activity);
        return "redirect:/activities/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        model.addAttribute("activity", activityDao.get(id));
        model.addAttribute("trainers", trainerDao.getAll());
        return "activity/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerDao.getAll());
            return "activity/edit";
        }

        activityDao.update(activity);
        return "redirect:/activities/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        activityDao.delete(id);
        return "redirect:/activities/list";
    }
}
