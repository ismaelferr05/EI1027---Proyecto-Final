package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.service.NameMaps;
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
@RequestMapping("/negotiations")
public class NegotiationController {

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private TableViewService tableViewService;

    @Autowired
    private NameMaps nameMaps;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        Map<String, Function<Negotiation, ?>> sorters = new LinkedHashMap<>();
        sorters.put("oviUser", negotiation -> {
            var request = nameMaps.requestById(negotiation.getIdRequest());
            return request != null ? nameMaps.oviUserNameById(request.getIdOviUser()) : "";
        });
        sorters.put("papPati", negotiation -> nameMaps.papPatiNameById(negotiation.getIdPapPati()));
        sorters.put("request", Negotiation::getIdRequest);
        sorters.put("state", Negotiation::getStateOfApproval);
        sorters.put("id", Negotiation::getIdNegotiation);

        model.addAttribute("negotiations", tableViewService.apply(negotiationDao.getAll(), q, sort, dir, sorters,
                tableViewService.fields(
                        Negotiation::getIdNegotiation,
                        Negotiation::getStateOfApproval,
                        Negotiation::getIdRequest,
                        negotiation -> {
                            var request = nameMaps.requestById(negotiation.getIdRequest());
                            return request != null ? request.getDescription() : "";
                        },
                        negotiation -> {
                            var request = nameMaps.requestById(negotiation.getIdRequest());
                            return request != null ? nameMaps.oviUserNameById(request.getIdOviUser()) : "";
                        },
                        negotiation -> nameMaps.papPatiNameById(negotiation.getIdPapPati())
                )));
        tableViewService.addState(model, "/negotiations/list", q, sort, dir,
                tableViewService.options("oviUser", "Usuario OVI", "papPati", "PAP/PATI", "request", "Solicitud", "state", "Estado", "id", "ID"));
        return "negotiation/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("negotiation", new Negotiation());
        model.addAttribute("requests", requestDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "negotiation/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("negotiation") Negotiation negotiation, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        NegotiationValidator negotiationValidator = new NegotiationValidator();
        negotiationValidator.validate(negotiation, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("requests", requestDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            return "negotiation/add";
        }

        negotiationDao.add(negotiation);
        redirectAttributes.addFlashAttribute("successMessage", "Negociación creada correctamente.");
        return "redirect:/negotiations/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("negotiation", negotiationDao.get(id));
        model.addAttribute("requests", requestDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "negotiation/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("negotiation") Negotiation negotiation, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        NegotiationValidator negotiationValidator = new NegotiationValidator();
        negotiationValidator.validate(negotiation, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("requests", requestDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            return "negotiation/edit";
        }

        negotiationDao.update(negotiation);
        redirectAttributes.addFlashAttribute("successMessage", "Negociación editada correctamente.");
        return "redirect:/negotiations/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        negotiationDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Negociación eliminada correctamente.");
        return "redirect:/negotiations/list";
    }
}
