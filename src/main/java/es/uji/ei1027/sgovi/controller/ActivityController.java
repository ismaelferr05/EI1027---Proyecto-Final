package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Activity;
import es.uji.ei1027.sgovi.service.NameMaps;
import es.uji.ei1027.sgovi.service.SessionUserService;
import es.uji.ei1027.sgovi.service.TableViewService;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private SessionUserService sessionUserService;

    @Autowired
    private TableViewService tableViewService;

    @Autowired
    private NameMaps nameMaps;

    @GetMapping("/list")
    public String list(HttpSession session, Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        Map<String, Function<Activity, ?>> sorters = new LinkedHashMap<>();
        sorters.put("id", Activity::getIdActivity);
        sorters.put("name", Activity::getName);
        sorters.put("date", Activity::getDate);
        sorters.put("duration", Activity::getDuration);
        sorters.put("location", Activity::getLocation);
        sorters.put("category", Activity::getCategory);
        sorters.put("trainer", activity -> nameMaps.trainerNameById(activity.getIdTrainer()));

        model.addAttribute("activities", tableViewService.apply(activityDao.getAll(), q, sort, dir, sorters,
                tableViewService.fields(
                        Activity::getIdActivity,
                        Activity::getName,
                        Activity::getDate,
                        Activity::getDuration,
                        Activity::getLocation,
                        Activity::getCategory,
                        Activity::getDescription,
                        activity -> nameMaps.trainerNameById(activity.getIdTrainer())
                )));
        tableViewService.addState(model, "/activities/list", q, sort, dir,
                tableViewService.options("id", "ID", "name", "Nombre", "date", "Fecha", "duration", "Duración", "location", "Localización", "category", "Categoría", "trainer", "Formador"));
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
    public String add(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addFlashAttribute("successMessage", "Actividad creada correctamente.");
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
    public String edit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addFlashAttribute("successMessage", "Actividad editada correctamente.");
        return "redirect:/activities/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        activityDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Actividad eliminada correctamente.");
        return "redirect:/activities/list";
    }
}
