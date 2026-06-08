package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.dao.MessageDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.model.ChatThreadSummary;
import es.uji.ei1027.sgovi.model.Contract;
import es.uji.ei1027.sgovi.model.Message;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.NameMaps;
import es.uji.ei1027.sgovi.service.SessionUserService;
import es.uji.ei1027.sgovi.service.TableViewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private static final String NEGOTIATION_REJECTED = "REJECTED";
    private static final String NEGOTIATION_CANCELLED = "CANCELLED";

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private ContractDao contractDao;

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
        UserDetails currentUser = sessionUserService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (sessionUserService.isTechnician(session)) {
            addMessagesTable(model, messageDao.getAll(), q, sort, dir);
            model.addAttribute("isTechnician", true);
            model.addAttribute("isOviUser", false);
            model.addAttribute("isPapPati", false);
            return "message/list";
        }

        List<ChatThreadSummary> chats = buildChats(session);
        addChatsTable(model, chats, q, sort, dir);
        model.addAttribute("currentUserRole", currentUser.getRole());
        model.addAttribute("currentOviUser", sessionUserService.getCurrentOviUser(session));
        model.addAttribute("currentPapPati", sessionUserService.getCurrentPapPati(session));
        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
        return "message/chats";
    }

    private void addMessagesTable(Model model, List<Message> messages, String q, String sort, String dir) {
        Map<String, Function<Message, ?>> sorters = new LinkedHashMap<>();
        sorters.put("id", Message::getIdMessage);
        sorters.put("date", Message::getMessageDateTime);
        sorters.put("sender", Message::getSender);
        sorters.put("receiver", Message::getReceiver);
        sorters.put("text", Message::getText);
        sorters.put("negotiation", Message::getIdNegotiation);

        model.addAttribute("messages", tableViewService.apply(messages, q, sort, dir, sorters,
                tableViewService.fields(Message::getIdMessage, Message::getMessageDateTime, Message::getSender,
                        Message::getReceiver, Message::getText, Message::getIdNegotiation)));
        tableViewService.addState(model, "/messages/list", q, sort, dir,
                tableViewService.options("id", "ID", "date", "Fecha/Hora", "sender", "Remitente", "receiver", "Destinatario", "text", "Texto", "negotiation", "Negociación"));
    }

    private void addChatsTable(Model model, List<ChatThreadSummary> chats, String q, String sort, String dir) {
        Map<String, Function<ChatThreadSummary, ?>> sorters = new LinkedHashMap<>();
        sorters.put("last", this::chatSortKey);
        sorters.put("request", chat -> chat.getRequest() != null ? chat.getRequest().getDescription() : "");
        sorters.put("oviUser", chat -> chat.getOviUser() != null ? nameMaps.fullName(chat.getOviUser().getName(), chat.getOviUser().getLastName()) : "");
        sorters.put("papPati", chat -> chat.getPapPati() != null ? nameMaps.fullName(chat.getPapPati().getName(), chat.getPapPati().getLastName()) : "");
        sorters.put("messages", ChatThreadSummary::getMessageCount);

        model.addAttribute("chats", tableViewService.apply(chats, q, sort, dir, sorters,
                tableViewService.fields(
                        chat -> chat.getNegotiation() != null ? chat.getNegotiation().getIdNegotiation() : "",
                        chat -> chat.getRequest() != null ? chat.getRequest().getDescription() : "",
                        chat -> chat.getOviUser() != null ? nameMaps.fullName(chat.getOviUser().getName(), chat.getOviUser().getLastName()) : "",
                        chat -> chat.getPapPati() != null ? nameMaps.fullName(chat.getPapPati().getName(), chat.getPapPati().getLastName()) : "",
                        chat -> chat.getLastMessage() != null ? chat.getLastMessage().getText() : "",
                        ChatThreadSummary::getMessageCount,
                        chat -> chat.isActive() ? "activo active" : "cerrado inactive"
                )));
        tableViewService.addState(model, "/messages/list", q, sort, dir,
                tableViewService.options("last", "Último mensaje", "request", "Solicitud", "oviUser", "Usuario OVI", "papPati", "PAP/PATI", "messages", "Mensajes"));
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

    @GetMapping("/chat/{idNegotiation}")
    public String chat(@PathVariable int idNegotiation, HttpSession session, Model model) {
        UserDetails currentUser = sessionUserService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (sessionUserService.isTechnician(session)) {
            return "redirect:/messages/list";
        }

        Negotiation negotiation = negotiationDao.get(idNegotiation);
        if (negotiation == null || !isNegotiationForCurrentUser(session, negotiation)) {
            model.addAttribute("errorMessage", "No se pudo abrir este chat.");
            model.addAttribute("chats", buildChats(session));
            model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
            model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
            return "message/chats";
        }

        ChatThreadSummary chat = buildChatSummary(negotiation);
        if (chat == null) {
            model.addAttribute("errorMessage", "Todavía no hay información suficiente para abrir este chat.");
            model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
            model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
            model.addAttribute("chats", buildChats(session));
            return "message/chats";
        }

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messageDao.getByNegotiation(idNegotiation));
        model.addAttribute("message", new Message());
        model.addAttribute("currentUserLabel", getCurrentParticipantLabel(session));
        model.addAttribute("papPatiLabel", compactLabel(chat.getPapPati().getName(), chat.getPapPati().getLastName()));
        model.addAttribute("oviUserLabel", compactLabel(chat.getOviUser().getName(), chat.getOviUser().getLastName()));
        model.addAttribute("counterpartLabel", getCounterpartLabel(session, chat));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
        return "message/chat";
    }

    @GetMapping("/negotiation/{idNegotiation}")
    public String messagesByNegotiation(@PathVariable int idNegotiation, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("messages", messageDao.getByNegotiation(idNegotiation));
        model.addAttribute("isTechnician", true);
        model.addAttribute("isOviUser", false);
        model.addAttribute("isPapPati", false);
        return "message/list";
    }

    @PostMapping("/chat/{idNegotiation}/send")
    public String sendChatMessage(@PathVariable int idNegotiation,
                                  @RequestParam("text") String text,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        UserDetails currentUser = sessionUserService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (sessionUserService.isTechnician(session)) {
            return "redirect:/messages/list";
        }

        Negotiation negotiation = negotiationDao.get(idNegotiation);
        if (negotiation == null || !isNegotiationForCurrentUser(session, negotiation)) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes enviar mensajes en esta conversación.");
            return "redirect:/messages/list";
        }

        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "El mensaje no puede estar vacío.");
            return "redirect:/messages/chat/" + idNegotiation;
        }

        ChatThreadSummary chat = buildChatSummary(negotiation);

        Message message = new Message();
        message.setMessageDateTime(LocalDateTime.now());
        message.setIdNegotiation(idNegotiation);
        message.setText(trimmed);
        message.setSender(getCurrentParticipantLabel(session));
        message.setReceiver(getCounterpartLabel(session, chat));
        messageDao.add(message);

        redirectAttributes.addFlashAttribute("successMessage", "Mensaje enviado correctamente.");
        return "redirect:/messages/chat/" + idNegotiation;
    }

    private List<ChatThreadSummary> buildChats(HttpSession session) {
        List<ChatThreadSummary> chats = new ArrayList<>();

        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            OviUser currentOviUser = sessionUserService.getCurrentOviUser(session);
            if (idOviUser == null || currentOviUser == null) {
                return chats;
            }
            for (Request request : requestDao.getByOviUser(idOviUser)) {
                for (Negotiation negotiation : negotiationDao.getByRequest(request.getIdRequest())) {
                    ChatThreadSummary chat = buildChatSummary(negotiation);
                    if (chat != null) {
                        chats.add(chat);
                    }
                }
            }
        } else if (sessionUserService.isPapPati(session)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null) {
                return chats;
            }
            for (Negotiation negotiation : negotiationDao.getByPapPati(idPapPati)) {
                ChatThreadSummary chat = buildChatSummary(negotiation);
                if (chat != null) {
                    chats.add(chat);
                }
            }
        }

        chats.sort(Comparator.comparing(this::chatSortKey).reversed());
        return chats;
    }

    private ChatThreadSummary buildChatSummary(Negotiation negotiation) {
        if (negotiation == null || negotiation.getIdRequest() == null || negotiation.getIdPapPati() == null) {
            return null;
        }

        Request request = requestDao.get(negotiation.getIdRequest());
        PapPati papPati = papPatiDao.get(negotiation.getIdPapPati());
        if (request == null || papPati == null || !isChatVisible(negotiation)) {
            return null;
        }

        OviUser oviUser = oviUserDao.get(request.getIdOviUser());
        if (oviUser == null) {
            return null;
        }

        List<Message> threadMessages = messageDao.getByNegotiation(negotiation.getIdNegotiation());
        Contract contract = contractDao.getByNegotiationId(negotiation.getIdNegotiation());

        ChatThreadSummary chat = new ChatThreadSummary();
        chat.setNegotiation(negotiation);
        chat.setRequest(request);
        chat.setOviUser(oviUser);
        chat.setPapPati(papPati);
        chat.setContract(contract);
        chat.setMessageCount(threadMessages.size());
        chat.setLastMessage(threadMessages.isEmpty() ? null : threadMessages.get(threadMessages.size() - 1));
        chat.setActive(true);
        return chat;
    }

    private boolean isNegotiationForCurrentUser(HttpSession session, Negotiation negotiation) {
        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null || negotiation.getIdRequest() == null) {
                return false;
            }
            Request request = requestDao.get(negotiation.getIdRequest());
            return request != null && idOviUser.equals(request.getIdOviUser());
        }

        if (sessionUserService.isPapPati(session)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            return idPapPati != null && idPapPati.equals(negotiation.getIdPapPati());
        }

        return false;
    }

    private boolean isChatVisible(Negotiation negotiation) {
        if (negotiation == null || negotiation.getStateOfApproval() == null) {
            return false;
        }
        return !NEGOTIATION_REJECTED.equals(negotiation.getStateOfApproval())
                && !NEGOTIATION_CANCELLED.equals(negotiation.getStateOfApproval());
    }

    private String getCurrentParticipantLabel(HttpSession session) {
        if (sessionUserService.isOviUser(session)) {
            OviUser user = sessionUserService.getCurrentOviUser(session);
            return user == null ? "OVI" : compactLabel(user.getName(), user.getLastName());
        }
        if (sessionUserService.isPapPati(session)) {
            PapPati user = sessionUserService.getCurrentPapPati(session);
            return user == null ? "PAP" : compactLabel(user.getName(), user.getLastName());
        }
        return "usuario";
    }

    private String getCounterpartLabel(HttpSession session, ChatThreadSummary chat) {
        if (chat == null) {
            return "destinatario";
        }
        if (sessionUserService.isOviUser(session)) {
            return compactLabel(chat.getPapPati().getName(), chat.getPapPati().getLastName());
        }
        if (sessionUserService.isPapPati(session)) {
            return compactLabel(chat.getOviUser().getName(), chat.getOviUser().getLastName());
        }
        return "destinatario";
    }

    private String compactLabel(String name, String lastName) {
        String label = ((name == null ? "" : name) + " " + (lastName == null ? "" : lastName)).trim();
        if (label.isEmpty()) {
            return "usuario";
        }
        return label.length() <= 20 ? label : label.substring(0, 20);
    }

    private java.time.LocalDateTime chatSortKey(ChatThreadSummary chat) {
        if (chat == null) {
            return java.time.LocalDateTime.MIN;
        }
        if (chat.getLastMessage() != null && chat.getLastMessage().getMessageDateTime() != null) {
            return chat.getLastMessage().getMessageDateTime();
        }
        return chat.getRequest() != null && chat.getRequest().getStartDate() != null
                ? chat.getRequest().getStartDate().atStartOfDay()
                : java.time.LocalDateTime.MIN;
    }
}
