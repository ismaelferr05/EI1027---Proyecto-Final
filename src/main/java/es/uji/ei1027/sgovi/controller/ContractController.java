package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractDao contractDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("contracts", contractDao.getAll());
        return "contracts/list";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, Model model) {
        model.addAttribute("contract", contractDao.get(id));
        return "contracts/view";
    }
}

