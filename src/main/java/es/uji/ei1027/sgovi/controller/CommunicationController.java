package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.TechnicianCommunicationDao;
import es.uji.ei1027.sgovi.model.TechnicianCommunication;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/communications")
public class CommunicationController {
    @Autowired
    private TechnicianCommunicationDao communicationDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private SessionUserService sessionUserService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        if (sessionUserService.isTechnician(session)) {
            model.addAttribute("communications", communicationDao.getAll());
            model.addAttribute("oviUsers", oviUserDao.getAll());
            model.addAttribute("papPatis", papPatiDao.getAll());
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("isTechnician", true);
            return "communications/list";
        }

        if (sessionUserService.isOviUser(session)) {
            Integer id = sessionUserService.getCurrentOviUserId(session);
            model.addAttribute("communications", communicationDao.getByRecipient("OVIUSER", id));
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "OVIUSER");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        if (sessionUserService.isPapPati(session)) {
            Integer id = sessionUserService.getCurrentPapPatiId(session);
            model.addAttribute("communications", communicationDao.getByRecipient("PAPPATI", id));
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "PAPPATI");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        return "redirect:/login";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute("communication") TechnicianCommunication communication,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (sessionUserService.getCurrentUser(session) == null) {
            return "redirect:/login";
        }

        if (!sessionUserService.isTechnician(session)) {
            if (sessionUserService.isOviUser(session)) {
                communication.setRecipientType("OVIUSER");
                communication.setRecipientId(sessionUserService.getCurrentOviUserId(session));
                communication.setSenderRole("OVIUSER");
            } else if (sessionUserService.isPapPati(session)) {
                communication.setRecipientType("PAPPATI");
                communication.setRecipientId(sessionUserService.getCurrentPapPatiId(session));
                communication.setSenderRole("PAPPATI");
            }
        } else {
            communication.setSenderRole("TECNICO");
        }

        if (communication.getRecipientId() == null || communication.getSubject() == null || communication.getSubject().isBlank()
                || communication.getText() == null || communication.getText().isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rellena destinatario, asunto y texto.");
            return "redirect:/communications/list";
        }

        communication.setCommunicationDateTime(LocalDateTime.now());
        communicationDao.add(communication);
        redirectAttributes.addFlashAttribute("successMessage", "Comunicación registrada correctamente.");
        return "redirect:/communications/list";
    }
}
