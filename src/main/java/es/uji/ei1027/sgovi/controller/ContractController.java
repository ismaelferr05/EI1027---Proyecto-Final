package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private SessionUserService sessionUserService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        if (sessionUserService.isTechnician(session)) {
            model.addAttribute("contracts", contractDao.getAll());
            return "contracts/list";
        }

        if (sessionUserService.isPapPati(session)) {
            return "redirect:/contracts/pappati/list";
        }

        if (sessionUserService.getCurrentUser(session) == null) {
            return "redirect:/login";
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/pappati/list")
    public String papPatiList(HttpSession session, Model model) {
        if (!sessionUserService.isPapPati(session)) {
            return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
        }

        Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
        if (idPapPati == null) {
            return "redirect:/login";
        }

        model.addAttribute("contracts", contractDao.getByPapPatiId(idPapPati));
        model.addAttribute("currentPapPati", sessionUserService.getCurrentPapPati(session));
        return "contracts/pappati-list";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable int id, HttpSession session, Model model) {
        if (sessionUserService.getCurrentUser(session) == null) {
            return "redirect:/login";
        }

        if (sessionUserService.isTechnician(session)) {
            model.addAttribute("contract", contractDao.get(id));
            return "contracts/view";
        }

        if (sessionUserService.isPapPati(session)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null || !contractDao.belongsToPapPati(id, idPapPati)) {
                return "redirect:/contracts/pappati/list";
            }
            model.addAttribute("contract", contractDao.get(id));
            return "contracts/view";
        }

        return "redirect:/dashboard";
    }
}

