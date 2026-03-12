package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.MessageDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("messages", messageDao.getAll());
        return "message/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("message", new Message());
        model.addAttribute("negotiations", negotiationDao.getAll());
        return "message/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Message message) {
        messageDao.add(message);
        return "redirect:/messages/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("message", messageDao.get(id));
        model.addAttribute("negotiations", negotiationDao.getAll());
        return "message/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Message message) {
        messageDao.update(message);
        return "redirect:/messages/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        messageDao.delete(id);
        return "redirect:/messages/list";
    }

    @GetMapping("/negotiation/{idNegotiation}")
    public String listByNegotiation(@PathVariable int idNegotiation, Model model) {
        model.addAttribute("messages", messageDao.getByNegotiation(idNegotiation));
        model.addAttribute("idNegotiation", idNegotiation);
        return "message/list";
    }
}

