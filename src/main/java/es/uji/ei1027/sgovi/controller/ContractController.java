package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.model.Contract;
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
        return "contract/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("contract", new Contract());
        return "contract/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Contract contract) {
        contractDao.add(contract);
        return "redirect:/contracts/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("contract", contractDao.get(id));
        return "contract/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Contract contract) {
        contractDao.update(contract);
        return "redirect:/contracts/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        contractDao.delete(id);
        return "redirect:/contracts/list";
    }
}

