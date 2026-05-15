package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/negotiations")
public class NegotiationController {

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("negotiations", negotiationDao.getAll());
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
    public String edit(@ModelAttribute("negotiation") Negotiation negotiation, BindingResult bindingResult, Model model) {
        NegotiationValidator negotiationValidator = new NegotiationValidator();
        negotiationValidator.validate(negotiation, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("requests", requestDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            return "negotiation/edit";
        }

        negotiationDao.update(negotiation);
        return "redirect:/negotiations/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        negotiationDao.delete(id);
        return "redirect:/negotiations/list";
    }
}

