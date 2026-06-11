package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.TechnicianCommunicationDao;
import es.uji.ei1027.sgovi.model.TechnicianCommunication;
import es.uji.ei1027.sgovi.service.NameMaps;
import es.uji.ei1027.sgovi.service.SessionUserService;
import es.uji.ei1027.sgovi.service.TableViewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/communications")
public class CommunicationController {

    private static final int TECHNICIAN_RECIPIENT_ID = 1;

    @Autowired
    private TechnicianCommunicationDao communicationDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private SessionUserService sessionUserService;

    @Autowired
    private TableViewService tableViewService;

    @Autowired
    private NameMaps nameMaps;

    @GetMapping("/list")
    public String list(HttpSession session, Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        if (sessionUserService.isTechnician(session)) {
            addCommunicationsTable(model, communicationDao.getAll(), q, sort, dir);
            model.addAttribute("oviUsers", oviUserDao.getByStatus("ACCEPTED"));
            model.addAttribute("papPatis", papPatiDao.getByStatus("ACCEPTED"));
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("isTechnician", true);
            return "communications/list";
        }

        if (sessionUserService.isOviUser(session)) {
            Integer id = sessionUserService.getCurrentOviUserId(session);
            addCommunicationsTable(model, communicationDao.getConversationForOviUser(id), q, sort, dir);
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "OVIUSER");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        if (sessionUserService.isPapPati(session)) {
            Integer id = sessionUserService.getCurrentPapPatiId(session);
            addCommunicationsTable(model, communicationDao.getConversationForPapPati(id), q, sort, dir);
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "PAPPATI");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        return "redirect:/login";
    }

    private void addCommunicationsTable(Model model, List<TechnicianCommunication> communications, String q, String sort, String dir) {
        model.addAttribute("communications", communications);
    }

    @PostMapping("/send")
    public String send(@ModelAttribute("communication") TechnicianCommunication communication,
                       @RequestParam(value = "replyToSenderRole", required = false) String replyToSenderRole,
                       @RequestParam(value = "replyToSenderId", required = false) Integer replyToSenderId,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (sessionUserService.getCurrentUser(session) == null) {
            return "redirect:/login";
        }

        if (sessionUserService.isTechnician(session)) {
            communication.setSenderRole("TECNICO");
            communication.setSenderId(TECHNICIAN_RECIPIENT_ID);
            if (replyToSenderRole != null && replyToSenderId != null) {
                communication.setRecipientType(replyToSenderRole);
                communication.setRecipientId(replyToSenderId);
                if (communication.getSubject() != null && !communication.getSubject().startsWith("Re:")) {
                    communication.setSubject("Re: " + communication.getSubject());
                }
            }
        } else if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            communication.setSenderRole("OVIUSER");
            communication.setSenderId(idOviUser);
            communication.setRecipientType("TECNICO");
            communication.setRecipientId(TECHNICIAN_RECIPIENT_ID);
        } else if (sessionUserService.isPapPati(session)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            communication.setSenderRole("PAPPATI");
            communication.setSenderId(idPapPati);
            communication.setRecipientType("TECNICO");
            communication.setRecipientId(TECHNICIAN_RECIPIENT_ID);
        }

        if (communication.getRecipientId() == null || communication.getRecipientType() == null
                || communication.getSubject() == null || communication.getSubject().isBlank()
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
