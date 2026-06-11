package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.ParticipantListDao;
import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Activity;
import es.uji.ei1027.sgovi.model.ParticipantList;
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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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
    private ParticipantListDao participantListDao;

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
        sorters.put("name", Activity::getName);
        sorters.put("date", Activity::getDate);
        sorters.put("category", Activity::getCategory);
        sorters.put("trainer", activity -> nameMaps.trainerNameById(activity.getIdTrainer()));
        sorters.put("location", Activity::getLocation);
        sorters.put("duration", Activity::getDuration);
        sorters.put("description", Activity::getDescription);
        sorters.put("id", Activity::getIdActivity);

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
                tableViewService.options("name", "Nombre", "date", "Fecha", "category", "Categoría", "trainer", "Formador", "location", "Localización", "duration", "Duración", "description", "Descripción", "id", "ID"));
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

    @GetMapping("/browse")
    public String browse(HttpSession session, Model model) {
        if (!sessionUserService.isOviUser(session) && !sessionUserService.isPapPati(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        List<Activity> upcoming = activityDao.getAll().stream()
                .filter(activity -> activity.getDate() != null && !activity.getDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Activity::getIdActivity).reversed())
                .toList();

        Map<Integer, Boolean> enrolledByActivityId = new LinkedHashMap<>();
        for (Activity activity : upcoming) {
            boolean enrolled = false;
            if (sessionUserService.isOviUser(session)) {
                Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
                enrolled = idOviUser != null && participantListDao.existsByActivityAndOviUser(activity.getIdActivity(), idOviUser);
            } else if (sessionUserService.isPapPati(session)) {
                Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
                enrolled = idPapPati != null && participantListDao.existsByActivityAndPapPati(activity.getIdActivity(), idPapPati);
            }
            enrolledByActivityId.put(activity.getIdActivity(), enrolled);
        }

        model.addAttribute("activities", upcoming);
        model.addAttribute("enrolledByActivityId", enrolledByActivityId);
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
        return "activity/browse";
    }

    @PostMapping("/{id}/signup")
    public String signup(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isOviUser(session) && !sessionUserService.isPapPati(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        Activity activity = activityDao.get(id);
        if (activity == null || activity.getDate() == null || activity.getDate().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Esta actividad no está disponible para inscripción.");
            return "redirect:/activities/browse";
        }

        ParticipantList participant = new ParticipantList();
        participant.setAttendance(false);
        participant.setAttendanceCertificateUrl(null);
        participant.setIdActivity(id);

        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null || participantListDao.existsByActivityAndOviUser(id, idOviUser)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya estás inscrito en esta actividad.");
                return "redirect:/activities/browse";
            }
            participant.setIdOviUser(idOviUser);
        } else {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null || participantListDao.existsByActivityAndPapPati(id, idPapPati)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya estás inscrito en esta actividad.");
                return "redirect:/activities/browse";
            }
            participant.setIdPapPati(idPapPati);
        }

        participantListDao.add(participant);
        redirectAttributes.addFlashAttribute("successMessage", "Inscripción realizada correctamente.");
        return "redirect:/activities/browse";
    }

    @PostMapping("/{id}/unsignup")
    public String unsignup(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isOviUser(session) && !sessionUserService.isPapPati(session)) {
            return sessionUserService.isLoggedIn(session) ? "redirect:/dashboard" : "redirect:/login";
        }

        Activity activity = activityDao.get(id);
        if (activity == null || activity.getDate() == null || activity.getDate().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes desinscribirte de esta actividad.");
            return "redirect:/activities/browse";
        }

        int deleted;
        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null || !participantListDao.existsByActivityAndOviUser(id, idOviUser)) {
                redirectAttributes.addFlashAttribute("errorMessage", "No estás inscrito en esta actividad.");
                return "redirect:/activities/browse";
            }
            deleted = participantListDao.deleteByActivityAndOviUser(id, idOviUser);
        } else {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null || !participantListDao.existsByActivityAndPapPati(id, idPapPati)) {
                redirectAttributes.addFlashAttribute("errorMessage", "No estás inscrito en esta actividad.");
                return "redirect:/activities/browse";
            }
            deleted = participantListDao.deleteByActivityAndPapPati(id, idPapPati);
        }

        if (deleted == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cancelar la inscripción.");
            return "redirect:/activities/browse";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Inscripción cancelada correctamente.");
        return "redirect:/activities/browse";
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
