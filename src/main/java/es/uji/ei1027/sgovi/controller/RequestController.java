package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.OvilUserDao;
import es.uji.ei1027.sgovi.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private OvilUserDao ovilUserDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("requests", requestDao.getAll());
        return "request/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("request", new Request());
        model.addAttribute("ovilUsers", ovilUserDao.getAll());
        return "request/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Request request) {
        requestDao.add(request);
        return "redirect:/requests/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("request", requestDao.get(id));
        model.addAttribute("ovilUsers", ovilUserDao.getAll());
        return "request/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Request request) {
        requestDao.update(request);
        return "redirect:/requests/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        requestDao.delete(id);
        return "redirect:/requests/list";
    }
}

