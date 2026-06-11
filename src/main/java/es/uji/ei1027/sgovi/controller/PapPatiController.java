package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.model.Contract;
import es.uji.ei1027.sgovi.model.EmailContent;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.service.EmailService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping("/pap-patis")
public class PapPatiController {

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private es.uji.ei1027.sgovi.service.PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SessionUserService sessionUserService;

    @Autowired
    private TableViewService tableViewService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        Map<String, Function<PapPati, ?>> sorters = new LinkedHashMap<>();
        sorters.put("name", PapPati::getName);
        sorters.put("lastName", PapPati::getLastName);
        sorters.put("email", PapPati::getEmail);
        sorters.put("phone", PapPati::getPhone);
        sorters.put("status", PapPati::getStatus);
        sorters.put("province", PapPati::getProvince);
        sorters.put("town", PapPati::getTown);
        sorters.put("pc", PapPati::getPc);
        sorters.put("age", PapPati::getAge);
        sorters.put("gender", PapPati::getGender);
        sorters.put("training", PapPati::getTraining);
        sorters.put("experience", PapPati::getExperience);
        sorters.put("id", PapPati::getIdPapPati);

        model.addAttribute("papPatis", tableViewService.apply(papPatiDao.getAll(), q, sort, dir, sorters,
                tableViewService.fields(PapPati::getIdPapPati, PapPati::getName, PapPati::getLastName,
                        PapPati::getEmail, PapPati::getPhone, PapPati::getProvince, PapPati::getTown,
                        PapPati::getPc, PapPati::getAge, PapPati::getGender, PapPati::getTraining,
                        PapPati::getExperience, PapPati::getExperienceType, PapPati::getStatus),
                PapPati::getStatus));
        tableViewService.addState(model, "/pap-patis/list", q, sort, dir,
                tableViewService.options("name", "Nombre", "lastName", "Apellidos", "email", "Email", "phone", "Teléfono", "status", "Estado", "province", "Provincia", "town", "Ciudad", "pc", "CP", "age", "Edad", "gender", "Género", "training", "Formación", "experience", "Experiencia", "id", "ID"),
                tableViewService.userStatusOptions());
        return "pappati/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        PapPati papPati = new PapPati();
        papPati.setStatus("PENDING");
        papPati.setAvailabilityStartDate(LocalDate.of(2026, 1, 1));
        papPati.setAvailabilityEndDate(LocalDate.of(2026, 12, 31));
        model.addAttribute("papPati", papPati);
        return "pappati/add";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        PapPati papPati = new PapPati();
        papPati.setStatus("PENDING");
        papPati.setAvailabilityStartDate(LocalDate.of(2026, 1, 1));
        papPati.setAvailabilityEndDate(LocalDate.of(2026, 12, 31));
        model.addAttribute("papPati", papPati);
        return "pappati/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult, Model model) {
        papPati.setStatus("PENDING");
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);
        if (bindingResult.hasErrors()) {
            return "pappati/register";
        }

        papPati.setPassword(passwordService.encrypt(papPati.getPassword()));
        papPatiDao.add(papPati);
        model.addAttribute("trackedUser", papPatiDao.getByEmail(papPati.getEmail()));
        return "pappati/track";
    }

    @GetMapping("/track")
    public String trackForm() {
        return "pappati/track";
    }

    @PostMapping("/track")
    public String track(@RequestParam("email") String email, Model model) {
        model.addAttribute("trackedUser", papPatiDao.getByEmail(email));
        model.addAttribute("searched", true);
        return "pappati/track";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pappati/add";
        }

        if (papPati.getPassword() != null && !papPati.getPassword().isBlank()) {
            papPati.setPassword(passwordService.encrypt(papPati.getPassword()));
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

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        PapPati current = sessionUserService.getCurrentPapPati(session);
        if (current == null) {
            return "redirect:/login";
        }
        current.setPassword("");
        model.addAttribute("papPati", current);
        model.addAttribute("selfProfile", true);
        return "pappati/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        PapPati existing = papPatiDao.get(papPati.getIdPapPati());
        if (papPati.getPassword() == null || papPati.getPassword().isBlank()) {
            papPati.setPassword(existing.getPassword());
        } else {
            papPati.setPassword(passwordService.encrypt(papPati.getPassword()));
        }
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);

        if (bindingResult.hasErrors()) {
            return "pappati/edit";
        }

        papPatiDao.update(papPati);
        redirectAttributes.addFlashAttribute("successMessage", "PAP PATI editado correctamente.");
        return "redirect:/pap-patis/list";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("papPati") PapPati papPati, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        PapPati current = sessionUserService.getCurrentPapPati(session);
        if (current == null || current.getIdPapPati() != papPati.getIdPapPati()) {
            return "redirect:/dashboard";
        }
        papPati.setStatus(current.getStatus());
        papPati.setRejectionReason(current.getRejectionReason());
        if (papPati.getPassword() == null || papPati.getPassword().isBlank()) {
            papPati.setPassword(current.getPassword());
        } else {
            papPati.setPassword(passwordService.encrypt(papPati.getPassword()));
        }

        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(papPati, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("selfProfile", true);
            return "pappati/edit";
        }
        papPatiDao.update(papPati);
        redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
        return "redirect:/dashboard";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        PapPati papPati = papPatiDao.get(id);
        model.addAttribute("papPati", papPati);
        addContractsTable(model, "/pap-patis/view/" + id, contractDao.getByPapPatiId(id), q, sort, dir);
        return "pappati/view";
    }

    private void addContractsTable(Model model, String action, List<Contract> contracts, String q, String sort, String dir) {
        Map<String, Function<Contract, ?>> sorters = new LinkedHashMap<>();
        sorters.put("status", contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO" : "ACTIVO");
        sorters.put("startDate", Contract::getStartDate);
        sorters.put("endDate", Contract::getEndDate);
        sorters.put("document", Contract::getUrl);
        sorters.put("id", Contract::getIdContract);

        model.addAttribute("contracts", tableViewService.apply(contracts, q, sort, dir, sorters,
                tableViewService.fields(
                        Contract::getIdContract,
                        Contract::getStartDate,
                        Contract::getEndDate,
                        Contract::getUrl,
                        contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO finalizado" : "ACTIVO activo"
                ),
                contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO" : "ACTIVO"));
        tableViewService.addState(model, action, q, sort, dir,
                tableViewService.options("status", "Estado", "startDate", "Inicio", "endDate", "Fin", "document", "Documento", "id", "ID"),
                tableViewService.contractStatusOptions());
    }

    @PostMapping("/accept")
    public String accept(@RequestParam("idPapPati") int idPapPati, Model model) {
        PapPati papPati = papPatiDao.get(idPapPati);
        papPatiDao.updateStatus(idPapPati, "ACCEPTED", null);
        EmailContent email = emailService.sendUserStatusEmail(papPati.getEmail(), papPati.getName() + " " + papPati.getLastName(), "ACCEPTED", null);
        model.addAttribute("email", email);
        model.addAttribute("entityName", papPati.getName() + " " + papPati.getLastName());
        model.addAttribute("action", "acceptance");
        return "user-status-confirmation";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam("idPapPati") int idPapPati, @RequestParam("reason") String reason, Model model) {
        PapPati papPati = papPatiDao.get(idPapPati);
        papPatiDao.updateStatus(idPapPati, "REJECTED", reason);
        EmailContent email = emailService.sendUserStatusEmail(papPati.getEmail(), papPati.getName() + " " + papPati.getLastName(), "REJECTED", reason);
        model.addAttribute("email", email);
        model.addAttribute("entityName", papPati.getName() + " " + papPati.getLastName());
        model.addAttribute("action", "rejection");
        return "user-status-confirmation";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        papPatiDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "PAP PATI eliminado correctamente.");
        return "redirect:/pap-patis/list";
    }
}
