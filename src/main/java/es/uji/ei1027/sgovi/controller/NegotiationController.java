package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private ContractDao contractDao;

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
        model.addAttribute("contracts", contractDao.getAll());
        return "negotiation/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Negotiation negotiation) {
        negotiationDao.add(negotiation);
        return "redirect:/negotiations/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("negotiation", negotiationDao.get(id));
        model.addAttribute("requests", requestDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        model.addAttribute("contracts", contractDao.getAll());
        return "negotiation/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Negotiation negotiation) {
        negotiationDao.update(negotiation);
        return "redirect:/negotiations/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        negotiationDao.delete(id);
        return "redirect:/negotiations/list";
    }
}

