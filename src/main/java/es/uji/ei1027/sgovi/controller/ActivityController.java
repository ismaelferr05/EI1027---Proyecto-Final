package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private TrainerDao trainerDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("activities", activityDao.getAll());
        return "activity/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("trainers", trainerDao.getAll());
        return "activity/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Activity activity) {
        activityDao.add(activity);
        return "redirect:/activities/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("activity", activityDao.get(id));
        model.addAttribute("trainers", trainerDao.getAll());
        return "activity/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Activity activity) {
        activityDao.update(activity);
        return "redirect:/activities/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        activityDao.delete(id);
        return "redirect:/activities/list";
    }
}

