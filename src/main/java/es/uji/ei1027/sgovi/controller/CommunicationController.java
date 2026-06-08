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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
            // Sólo usuarios aceptados deben aparecer en el selector
            model.addAttribute("oviUsers", oviUserDao.getByStatus("ACCEPTED"));
            model.addAttribute("papPatis", papPatiDao.getByStatus("ACCEPTED"));
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("isTechnician", true);
            return "communications/list";
        }

        if (sessionUserService.isOviUser(session)) {
            Integer id = sessionUserService.getCurrentOviUserId(session);
            addCommunicationsTable(model, communicationDao.getByRecipient("OVIUSER", id), q, sort, dir);
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "OVIUSER");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        if (sessionUserService.isPapPati(session)) {
            Integer id = sessionUserService.getCurrentPapPatiId(session);
            addCommunicationsTable(model, communicationDao.getByRecipient("PAPPATI", id), q, sort, dir);
            model.addAttribute("communication", new TechnicianCommunication());
            model.addAttribute("recipientType", "PAPPATI");
            model.addAttribute("recipientId", id);
            return "communications/list";
        }

        return "redirect:/login";
    }

    private void addCommunicationsTable(Model model, List<TechnicianCommunication> communications, String q, String sort, String dir) {
        Map<String, Function<TechnicianCommunication, ?>> sorters = new LinkedHashMap<>();
        sorters.put("date", TechnicianCommunication::getCommunicationDateTime);
        sorters.put("sender", item -> nameMaps.roleLabel(item.getSenderRole()));
        sorters.put("recipient", item -> nameMaps.recipientLabel(item.getRecipientType(), item.getRecipientId()));
        sorters.put("subject", TechnicianCommunication::getSubject);

        model.addAttribute("communications", tableViewService.apply(communications, q, sort, dir, sorters,
                tableViewService.fields(
                        TechnicianCommunication::getCommunicationDateTime,
                        item -> nameMaps.roleLabel(item.getSenderRole()),
                        item -> nameMaps.recipientLabel(item.getRecipientType(), item.getRecipientId()),
                        TechnicianCommunication::getSubject,
                        TechnicianCommunication::getText
                )));
        tableViewService.addState(model, "/communications/list", q, sort, dir,
                tableViewService.options("date", "Fecha", "sender", "Emisor", "recipient", "Destinatario", "subject", "Asunto"));
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
