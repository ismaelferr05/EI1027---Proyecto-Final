package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.MessageDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.model.Message;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private SessionUserService sessionUserService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        UserDetails currentUser = sessionUserService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        String role = currentUser.getRole();
        if ("OVIUSER".equals(role)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null) {
                return "redirect:/login";
            }
            model.addAttribute("messages", idOviUser != null ? messageDao.getByOviUser(idOviUser) : java.util.List.of());
        } else if ("PAPPATI".equals(role)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null) {
                return "redirect:/login";
            }
            model.addAttribute("messages", idPapPati != null ? messageDao.getByPapPati(idPapPati) : java.util.List.of());
        } else {
            model.addAttribute("messages", messageDao.getAll());
        }

        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
        return "message/list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("message", new Message());
        model.addAttribute("negotiations", negotiationDao.getAll());
        return "message/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("message") Message message, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        MessageValidator messageValidator = new MessageValidator();
        messageValidator.validate(message, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiations", negotiationDao.getAll());
            return "message/add";
        }

        messageDao.add(message);
        redirectAttributes.addFlashAttribute("successMessage", "Mensaje creado correctamente.");
        return "redirect:/messages/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("message", messageDao.get(id));
        model.addAttribute("negotiations", negotiationDao.getAll());
        return "message/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("message") Message message, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        MessageValidator messageValidator = new MessageValidator();
        messageValidator.validate(message, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiations", negotiationDao.getAll());
            return "message/edit";
        }

        messageDao.update(message);
        redirectAttributes.addFlashAttribute("successMessage", "Mensaje editado correctamente.");
        return "redirect:/messages/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        messageDao.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Mensaje eliminado correctamente.");
        return "redirect:/messages/list";
    }

    @GetMapping("/negotiation/{idNegotiation}")
    public String listByNegotiation(@PathVariable int idNegotiation, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("messages", messageDao.getByNegotiation(idNegotiation));
        model.addAttribute("idNegotiation", idNegotiation);
        return "message/list";
    }
}

