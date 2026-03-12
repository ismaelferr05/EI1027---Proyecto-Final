package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ParticipantListDao;
import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.OvilUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/participant-lists")
public class ParticipantListController {

    @Autowired
    private ParticipantListDao participantListDao;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private OvilUserDao ovilUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("participantLists", participantListDao.getAll());
        return "participantlist/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("participantList", new ParticipantList());
        model.addAttribute("activities", activityDao.getAll());
        model.addAttribute("ovilUsers", ovilUserDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "participantlist/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute ParticipantList participantList) {
        participantListDao.add(participantList);
        return "redirect:/participant-lists/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("participantList", participantListDao.get(id));
        model.addAttribute("activities", activityDao.getAll());
        model.addAttribute("ovilUsers", ovilUserDao.getAll());
        model.addAttribute("papPatis", papPatiDao.getAll());
        return "participantlist/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute ParticipantList participantList) {
        participantListDao.update(participantList);
        return "redirect:/participant-lists/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        participantListDao.delete(id);
        return "redirect:/participant-lists/list";
    }
}

