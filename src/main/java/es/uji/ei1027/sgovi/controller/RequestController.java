package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.model.CandidateProposal;
import es.uji.ei1027.sgovi.model.ChatThreadSummary;
import es.uji.ei1027.sgovi.model.Contract;
import es.uji.ei1027.sgovi.model.EmailContent;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.EmailService;
import es.uji.ei1027.sgovi.service.NameMaps;
import es.uji.ei1027.sgovi.service.RequestProposalService;
import es.uji.ei1027.sgovi.service.SessionUserService;
import es.uji.ei1027.sgovi.service.TableViewService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestProposalService requestProposalService;

    @Autowired
    private SessionUserService sessionUserService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TableViewService tableViewService;

    @Autowired
    private NameMaps nameMaps;

    @GetMapping("/list")
    public String list(HttpSession session, Model model,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "type", required = false) String type,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "sort", required = false) String sort,
                       @RequestParam(value = "dir", required = false) String dir) {
        UserDetails currentUser = sessionUserService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        String role = currentUser.getRole();
        List<Request> requests;
        if ("OVIUSER".equals(role)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null) {
                return "redirect:/login";
            }
            requests = requestDao.getByOviUser(idOviUser);
        } else if ("PAPPATI".equals(role)) {
            return "redirect:/contracts/pappati/list";
        } else {
            requests = requestDao.getAll();
        }

        // Filtrado opcional por estado y/o tipo (training)
        if (status != null && !status.isBlank()) {
            String st = status.trim();
            requests = requests.stream()
                    .filter(r -> st.equals(r.getStatus()))
                    .collect(Collectors.toList());
        }
        if (type != null && !type.isBlank()) {
            String tp = tableViewService.normalize(type);
            requests = requests.stream()
                    .filter(r -> tableViewService.normalize(r.getTraining()).contains(tp))
                    .collect(Collectors.toList());
        }

        addRequestsTable(model, "requests", "/requests/list", requests, q, sort, dir);

        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));
        return "request/list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = new Request();
        request.setStatus("IN_REVIEW");
        model.addAttribute("request", request);
        model.addAttribute("oviUsers", oviUserDao.getAll());
        addRequestFormAttributes(model);
        return "request/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUsers", oviUserDao.getAll());
            addRequestFormAttributes(model);
            return "request/add";
        }

        requestDao.add(request);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud creada correctamente.");
        return "redirect:/requests/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("request", requestDao.get(id));
        model.addAttribute("oviUsers", oviUserDao.getAll());
        addRequestFormAttributes(model);
        return "request/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUsers", oviUserDao.getAll());
            addRequestFormAttributes(model);
            return "request/edit";
        }

        requestDao.update(request);
        redirectAttributes.addFlashAttribute("successMessage", "Solicitud editada correctamente.");
        return "redirect:/requests/list";
    }

    @Deprecated
    @GetMapping("/delete/{id}")
    public String deleteDeprecated(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Eliminación física ya no permitida. Use la acción de rechazar para conservar el historial.
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Operación no permitida. Use Rechazar para conservar el historial.");
        return "redirect:/requests/list";
    }

    @PostMapping("/reject")
    public String rejectRequest(@RequestParam("idRequest") int idRequest,
                                @RequestParam(value = "reason", required = false) String reason,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(idRequest);
        if (request == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Solicitud no encontrada.");
            return "redirect:/requests/list";
        }

        request.setStatus("REJECTED");
        request.setRejectionReason(reason);
        OviUser oviUser = oviUserDao.get(request.getIdOviUser());
        requestDao.updateStatus(idRequest, "REJECTED", reason);
        EmailContent email = emailService.sendRejectionEmail(request, oviUser);
        return showConfirmation(model, request, oviUser, null, email, "rejection");
    }

    @PostMapping("/accept")
    public String acceptRequest(@RequestParam("idRequest") int idRequest,
                                @RequestParam(value = "selectedPapPatiId", required = false) Integer selectedPapPatiId,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(idRequest);
        if (request == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Solicitud no encontrada.");
            return "redirect:/requests/list";
        }

        OviUser oviUser = oviUserDao.get(request.getIdOviUser());
        PapPati selectedPapPati = selectedPapPatiId != null ? papPatiDao.get(selectedPapPatiId) : null;

        requestDao.updateStatus(idRequest, "APPROVED");
        request.setStatus("APPROVED");

        if (selectedPapPatiId != null && !negotiationDao.existsByRequestAndPapPati(idRequest, selectedPapPatiId)) {
            Negotiation negotiation = new Negotiation();
            negotiation.setStateOfApproval("PENDING");
            negotiation.setIdRequest(idRequest);
            negotiation.setIdPapPati(selectedPapPatiId);
            negotiationDao.add(negotiation);
        }

        EmailContent email = emailService.sendAcceptanceEmail(request, oviUser, selectedPapPati);
        return showConfirmation(model, request, oviUser, selectedPapPati, email, "acceptance");
    }

    @GetMapping("/frontoffice/add")
    public String frontOfficeAddForm(HttpSession session, Model model) {
        if (!sessionUserService.isOviUser(session)) {
            return "redirect:/dashboard";
        }

        Request request = new Request();
        request.setStatus("IN_REVIEW");
        OviUser currentOviUser = sessionUserService.getCurrentOviUser(session);
        if (currentOviUser == null) {
            return "redirect:/login";
        }
        request.setIdOviUser(currentOviUser.getIdOviUser());
        model.addAttribute("request", request);
        model.addAttribute("currentOviUser", currentOviUser);
        addRequestFormAttributes(model);
        return "request/frontoffice-add";
    }

    @PostMapping("/frontoffice/add")
    public String frontOfficeAdd(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isOviUser(session)) {
            return "redirect:/dashboard";
        }

        OviUser currentOviUser = sessionUserService.getCurrentOviUser(session);
        if (currentOviUser == null) {
            return "redirect:/login";
        }

        request.setIdOviUser(currentOviUser.getIdOviUser());
        request.setStatus("IN_REVIEW");

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentOviUser", currentOviUser);
            addRequestFormAttributes(model);
            return "request/frontoffice-add";
        }

        requestDao.add(request);
        EmailContent email = emailService.sendRequestCreatedEmail(request, currentOviUser);
        return showConfirmation(model, request, currentOviUser, null, email, "creation");
    }

    @GetMapping("/frontoffice/track")
    public String frontOfficeTrack(HttpSession session, Model model,
                                   @RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "sort", required = false) String sort,
                                   @RequestParam(value = "dir", required = false) String dir) {
        if (!sessionUserService.isOviUser(session) && !sessionUserService.isTechnician(session)) {
            return "redirect:/login";
        }

        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));

        List<Request> requests;
        if (sessionUserService.isTechnician(session)) {
            requests = requestDao.getAll();
            addRequestsTable(model, "requests", "/requests/frontoffice/track", requests, q, sort, dir);
        } else {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null) {
                return "redirect:/login";
            }
            requests = requestDao.getByOviUser(idOviUser);
            addRequestsTable(model, "requests", "/requests/frontoffice/track", requests, q, sort, dir);
            model.addAttribute("currentOviUser", sessionUserService.getCurrentOviUser(session));
        }
        model.addAttribute("followUpByRequestId", buildFollowUpByRequestId(requests));

        return "request/frontoffice-track";
    }

    @GetMapping("/frontoffice/view/{id}")
    public String frontOfficeView(@PathVariable("id") int id, HttpSession session, Model model) {
        // Solo técnicos o el OviUser que creó la solicitud pueden verla
        if (!sessionUserService.isTechnician(session) && !sessionUserService.isOviUser(session)) {
            return "redirect:/login";
        }

        Request request = requestDao.get(id);
        if (request == null) {
            return "redirect:/requests/frontoffice/track";
        }

        // Si es OviUser, comprobar que la solicitud es suya
        if (sessionUserService.isOviUser(session)) {
            Integer currentOviId = sessionUserService.getCurrentOviUserId(session);
            if (currentOviId == null || !currentOviId.equals(request.getIdOviUser())) {
                return "redirect:/requests/frontoffice/track";
            }
        }

        List<Negotiation> negotiations = negotiationDao.getByRequest(id);

        // Propuestas del técnico pendientes de que el usuario OVI inicie conversación.
        List<ChatThreadSummary> proposedChats = new java.util.ArrayList<>();
        // Asistentes asignados: solo si hay contrato o negociación aceptada.
        List<ChatThreadSummary> assignedChats = new java.util.ArrayList<>();
        for (Negotiation n : negotiations) {
            boolean closedNegotiation = "REJECTED".equals(n.getStateOfApproval()) || "CANCELLED".equals(n.getStateOfApproval());
            Contract contract = contractDao.getByNegotiationId(n.getIdNegotiation());
            if (n.getIdPapPati() == null || closedNegotiation) {
                continue;
            }
            es.uji.ei1027.sgovi.model.PapPati papPati = papPatiDao.get(n.getIdPapPati());
            if (papPati == null) {
                continue;
            }
            if ("PENDING".equals(n.getStateOfApproval()) || "IN_PROGRESS".equals(n.getStateOfApproval())) {
                ChatThreadSummary proposedChat = new ChatThreadSummary();
                proposedChat.setNegotiation(n);
                proposedChat.setRequest(request);
                proposedChat.setPapPati(papPati);
                proposedChats.add(proposedChat);
            } else if ("ACCEPTED".equals(n.getStateOfApproval()) || contract != null) {
                ChatThreadSummary assignedChat = new ChatThreadSummary();
                assignedChat.setNegotiation(n);
                assignedChat.setRequest(request);
                assignedChat.setPapPati(papPati);
                assignedChat.setContract(contract);
                assignedChats.add(assignedChat);
            }
        }

        model.addAttribute("request", request);
        model.addAttribute("proposedChats", proposedChats);
        model.addAttribute("assignedChats", assignedChats);
        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        return "request/frontoffice-view";
    }

    @PostMapping("/frontoffice/start-chat")
    public String frontOfficeStartChat(@RequestParam("idRequest") int idRequest,
                                       @RequestParam("idPapPati") int idPapPati,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isOviUser(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(idRequest);
        Integer currentOviId = sessionUserService.getCurrentOviUserId(session);
        if (request == null || currentOviId == null || !currentOviId.equals(request.getIdOviUser()) || !"APPROVED".equals(request.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes iniciar esta conversación.");
            return "redirect:/requests/frontoffice/track";
        }

        Negotiation negotiation = negotiationDao.getByRequestAndPapPati(idRequest, idPapPati);
        if (negotiation == null) {
            negotiation = new Negotiation();
            negotiation.setStateOfApproval("IN_PROGRESS");
            negotiation.setIdRequest(idRequest);
            negotiation.setIdPapPati(idPapPati);
            negotiationDao.add(negotiation);
            negotiation = negotiationDao.getByRequestAndPapPati(idRequest, idPapPati);
        } else if ("REJECTED".equals(negotiation.getStateOfApproval()) || "CANCELLED".equals(negotiation.getStateOfApproval())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Esta propuesta ya no está disponible para iniciar conversación.");
            return "redirect:/requests/frontoffice/view/" + idRequest;
        } else if ("PENDING".equals(negotiation.getStateOfApproval())) {
            negotiation.setStateOfApproval("IN_PROGRESS");
            negotiationDao.update(negotiation);
        }

        return "redirect:/messages/chat/" + negotiation.getIdNegotiation();
    }

    @GetMapping("/backoffice/list")
    public String legacyBackOfficeList() {
        return "redirect:/requests/list";
    }

    private void addRequestsTable(Model model, String attribute, String action, List<Request> requests, String q, String sort, String dir) {
        model.addAttribute(attribute, sortedFilteredRequests(requests, q, sort, dir));
        Map<String, String> options;
        if ("/requests/frontoffice/track".equals(action)) {
            options = tableViewService.options("description", "Descripción", "startDate", "Inicio", "endDate", "Fin", "status", "Estado", "id", "ID");
        } else {
            options = tableViewService.options("oviUser", "Usuario OVI", "description", "Descripción", "startDate", "Inicio", "endDate", "Fin", "status", "Estado", "id", "ID");
        }
        tableViewService.addState(model, action, q, sort, dir, options, tableViewService.requestStatusOptions());
    }

    private void addRequestFormAttributes(Model model) {
        model.addAttribute("today", LocalDate.now());
    }

    private List<Request> sortedFilteredRequests(List<Request> requests, String q, String sort, String dir) {
        Map<String, Function<Request, ?>> sorters = new LinkedHashMap<>();
        sorters.put("oviUser", request -> nameMaps.oviUserNameById(request.getIdOviUser()));
        sorters.put("description", Request::getDescription);
        sorters.put("startDate", Request::getStartDate);
        sorters.put("endDate", Request::getEndDate);
        sorters.put("training", Request::getTraining);
        sorters.put("status", Request::getStatus);
        sorters.put("experience", Request::getExperience);
        sorters.put("preferredAge", Request::getPreferredAge);
        sorters.put("preferredPc", Request::getPreferredPc);
        sorters.put("id", Request::getIdRequest);

        return tableViewService.apply(requests, q, sort, dir, sorters,
                tableViewService.fields(
                        Request::getIdRequest,
                        Request::getDescription,
                        request -> nameMaps.oviUserNameById(request.getIdOviUser()),
                        Request::getTraining,
                        Request::getStartDate,
                        Request::getEndDate,
                        Request::getStatus,
                        Request::getExperience,
                        Request::getExperienceType,
                        Request::getPreferredGender,
                        Request::getPreferredPc,
                        Request::getPreferredAge,
                        Request::getRejectionReason
                ),
                Request::getStatus);
    }

    @GetMapping({"/review/{id}", "/backoffice/review/{id}"})
    public String reviewRequest(@PathVariable("id") int id,
                                   @RequestParam(value = "msg", required = false) String msg,
                                   @RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "sort", required = false) String sort,
                                   @RequestParam(value = "dir", required = false) String dir,
                                   HttpSession session,
                                   Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(id);
        if (request == null) {
            return "redirect:/requests/list";
        }

        // Esta pantalla muestra candidaturas por afinidad: siempre por puntuación descendente.
        sort = "score";
        dir = "desc";

        List<CandidateProposal> proposals = requestProposalService.buildProposals(request);
        List<Negotiation> negotiations = negotiationDao.getByRequest(id).stream()
                .sorted(Comparator.comparing(Negotiation::getIdNegotiation).reversed())
                .collect(Collectors.toList());
        List<Contract> associatedContracts = getContractsForRequest(id).stream()
                .sorted(Comparator.comparing(Contract::getIdContract).reversed())
                .collect(Collectors.toList());
        Set<Integer> existingPapPatis = new HashSet<>();
        List<PapPati> proposedPapPatis = new java.util.ArrayList<>();
        Map<Integer, Contract> contractByNegotiationId = new LinkedHashMap<>();
        for (Negotiation negotiation : negotiations) {
            existingPapPatis.add(negotiation.getIdPapPati());
            proposedPapPatis.add(papPatiDao.get(negotiation.getIdPapPati()));
            Contract contract = contractDao.getByNegotiationId(negotiation.getIdNegotiation());
            if (contract != null) {
                contractByNegotiationId.put(negotiation.getIdNegotiation(), contract);
            }
        }
        List<CandidateProposal> sortedProposals = sortedFilteredProposals(proposals, q, sort, dir);
        List<CandidateProposal> availableProposals = sortedProposals.stream()
                .filter(proposal -> !existingPapPatis.contains(proposal.getPapPati().getIdPapPati()))
                .collect(Collectors.toList());

        model.addAttribute("request", request);
        model.addAttribute("proposals", sortedProposals);
        model.addAttribute("availableProposals", availableProposals);
        model.addAttribute("negotiations", negotiations);
        model.addAttribute("associatedContracts", associatedContracts);
        model.addAttribute("contractByNegotiationId", contractByNegotiationId);
        model.addAttribute("existingPapPatis", existingPapPatis);
        model.addAttribute("proposedPapPatis", proposedPapPatis);
        model.addAttribute("msg", msg);
        model.addAttribute("tableSortLocked", true);
        tableViewService.addState(model, "/requests/review/" + id, q, sort, dir,
                tableViewService.options("score", "Puntuación", "name", "PAP/PATI", "pc", "CP", "gender", "Género", "age", "Edad", "detail", "Detalle"));
        return "request/review";
    }

    private List<CandidateProposal> sortedFilteredProposals(List<CandidateProposal> proposals, String q, String sort, String dir) {
        Map<String, Function<CandidateProposal, ?>> sorters = new LinkedHashMap<>();
        sorters.put("name", proposal -> nameMaps.fullName(proposal.getPapPati().getName(), proposal.getPapPati().getLastName()));
        sorters.put("score", CandidateProposal::getScore);
        sorters.put("pc", proposal -> proposal.getPapPati().getPc());
        sorters.put("gender", proposal -> proposal.getPapPati().getGender());
        sorters.put("age", proposal -> proposal.getPapPati().getAge());
        sorters.put("detail", CandidateProposal::getMatchSummary);
        sorters.put("id", proposal -> proposal.getPapPati().getIdPapPati());

        return tableViewService.apply(proposals, q, sort, dir, sorters,
                tableViewService.fields(
                        proposal -> proposal.getPapPati().getIdPapPati(),
                        CandidateProposal::getScore,
                        CandidateProposal::getMatchSummary,
                        proposal -> String.join(" ", proposal.getReasonDetails()),
                        proposal -> nameMaps.fullName(proposal.getPapPati().getName(), proposal.getPapPati().getLastName()),
                        proposal -> proposal.getPapPati().getPc(),
                        proposal -> proposal.getPapPati().getGender(),
                        proposal -> proposal.getPapPati().getAge(),
                        proposal -> proposal.getPapPati().getTraining(),
                        proposal -> proposal.getPapPati().getExperience(),
                        proposal -> proposal.getPapPati().getExperienceType()
                ));
    }

    private boolean isValidNegotiationState(String stateOfApproval) {
        return "PENDING".equals(stateOfApproval)
                || "ACCEPTED".equals(stateOfApproval)
                || "IN_PROGRESS".equals(stateOfApproval)
                || "REJECTED".equals(stateOfApproval)
                || "CANCELLED".equals(stateOfApproval);
    }

    @PostMapping({"/review/approve", "/backoffice/approve"})
    public String reviewApprove(@RequestParam("idRequest") int idRequest,
                                    @RequestParam(value = "selectedPapPatiIds", required = false) List<Integer> selectedPapPatiIds,
                                    HttpSession session,
                                    Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        if (selectedPapPatiIds == null || selectedPapPatiIds.isEmpty()) {
            return "redirect:/requests/review/" + idRequest + "?msg=Debes seleccionar al menos un candidato para poder aprobar la solicitud";
        }

        Request request = requestDao.get(idRequest);
        if (request == null) {
            return "redirect:/requests/list";
        }

        OviUser oviUser = oviUserDao.get(request.getIdOviUser());
        List<PapPati> selectedPapPatis = new java.util.ArrayList<>();
        for (Integer selectedPapPatiId : selectedPapPatiIds.stream().distinct().toList()) {
            PapPati selectedPapPati = papPatiDao.get(selectedPapPatiId);
            if (selectedPapPati == null) {
                continue;
            }
            selectedPapPatis.add(selectedPapPati);
            if (!negotiationDao.existsByRequestAndPapPati(idRequest, selectedPapPatiId)) {
                Negotiation negotiation = new Negotiation();
                negotiation.setStateOfApproval("PENDING");
                negotiation.setIdRequest(idRequest);
                negotiation.setIdPapPati(selectedPapPatiId);
                negotiationDao.add(negotiation);
            }
        }

        if (selectedPapPatis.isEmpty()) {
            return "redirect:/requests/review/" + idRequest + "?msg=No se pudo registrar ningún candidato válido";
        }

        requestDao.updateStatus(idRequest, "APPROVED");
        request.setStatus("APPROVED");

        EmailContent email = emailService.sendAcceptanceEmail(request, oviUser, selectedPapPatis);
        return showConfirmation(model, request, oviUser, selectedPapPatis.get(0), email, "acceptance");
    }

    @PostMapping({"/review/propose", "/backoffice/propose"})
    public String reviewPropose(@RequestParam("idRequest") int idRequest,
                                    @RequestParam("selectedPapPatiId") int selectedPapPatiId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(idRequest);
        PapPati selectedPapPati = papPatiDao.get(selectedPapPatiId);
        if (request == null || selectedPapPati == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Solicitud o PAP/PATI no encontrado.");
            return "redirect:/requests/list";
        }

        if (!"APPROVED".equals(request.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Esta acción solo está disponible para solicitudes aprobadas.");
            return "redirect:/requests/review/" + idRequest;
        }

        if (negotiationDao.existsByRequestAndPapPati(idRequest, selectedPapPatiId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ese PAP/PATI ya está propuesto para esta solicitud.");
            return "redirect:/requests/review/" + idRequest;
        }

        Negotiation negotiation = new Negotiation();
        negotiation.setStateOfApproval("PENDING");
        negotiation.setIdRequest(idRequest);
        negotiation.setIdPapPati(selectedPapPatiId);
        negotiationDao.add(negotiation);
        redirectAttributes.addFlashAttribute("successMessage", "PAP/PATI propuesto correctamente.");
        return "redirect:/requests/review/" + idRequest;
    }

    @PostMapping({"/review/unpropose", "/backoffice/unpropose"})
    public String reviewUnpropose(@RequestParam("idNegotiation") int idNegotiation,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Negotiation negotiation = negotiationDao.get(idNegotiation);
        if (negotiation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Negociación no encontrada.");
            return "redirect:/requests/list";
        }

        int idRequest = negotiation.getIdRequest();
        negotiationDao.delete(idNegotiation);
        redirectAttributes.addFlashAttribute("successMessage", "PAP/PATI despropuesto correctamente.");
        return "redirect:/requests/review/" + idRequest;
    }

    @PostMapping({"/review/negotiation-status", "/backoffice/negotiation-status"})
    public String reviewNegotiationStatus(@RequestParam("idNegotiation") int idNegotiation,
                                              @RequestParam("stateOfApproval") String stateOfApproval,
                                              HttpSession session,
                                              RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Negotiation negotiation = negotiationDao.get(idNegotiation);
        if (negotiation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Negociación no encontrada.");
            return "redirect:/requests/list";
        }

        if (!isValidNegotiationState(stateOfApproval)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Estado de negociación no válido.");
            return "redirect:/requests/review/" + negotiation.getIdRequest();
        }

        negotiation.setStateOfApproval(stateOfApproval);
        negotiationDao.update(negotiation);
        redirectAttributes.addFlashAttribute("successMessage", "Estado de la negociación actualizado.");
        return "redirect:/requests/review/" + negotiation.getIdRequest();
    }

    @PostMapping({"/review/reject", "/backoffice/reject"})
    public String reviewReject(@RequestParam("idRequest") int idRequest,
                                   @RequestParam(value = "reason", required = false) String reason,
                                   HttpSession session,
                                   Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(idRequest);
        if (request == null) {
            return "redirect:/requests/list";
        }

        String rejectionReason = reason != null && !reason.isBlank()
                ? reason.trim()
                : "No cumple los criterios de la asistencia solicitada.";
        request.setStatus("REJECTED");
        request.setRejectionReason(rejectionReason);
        OviUser oviUser = oviUserDao.get(request.getIdOviUser());
        requestDao.updateStatus(idRequest, "REJECTED", request.getRejectionReason());
        EmailContent email = emailService.sendRejectionEmail(request, oviUser);
        return showConfirmation(model, request, oviUser, null, email, "rejection");
    }

    private String showConfirmation(Model model,
                                    Request request,
                                    OviUser oviUser,
                                    PapPati selectedPapPati,
                                    EmailContent email,
                                    String action) {
        model.addAttribute("request", request);
        model.addAttribute("oviUser", oviUser);
        model.addAttribute("selectedPapPati", selectedPapPati);
        model.addAttribute("email", email);
        model.addAttribute("action", action);
        if ("creation".equals(action)) {
            model.addAttribute("returnUrl", "/requests/frontoffice/track");
            model.addAttribute("returnLabel", "Ver mis solicitudes");
        } else {
            model.addAttribute("returnUrl", "/requests/list");
            model.addAttribute("returnLabel", "Volver al listado");
        }
        return "request/confirmation";
    }

    private boolean hasAnyContractForRequest(int idRequest) {
        return !getContractsForRequest(idRequest).isEmpty();
    }

    private List<Contract> getContractsForRequest(int idRequest) {
        List<Contract> contracts = new java.util.ArrayList<>();
        for (Negotiation negotiation : negotiationDao.getByRequest(idRequest)) {
            Contract contract = contractDao.getByNegotiationId(negotiation.getIdNegotiation());
            if (contract != null) {
                contracts.add(contract);
            }
        }
        return contracts;
    }

    private Map<Integer, RequestFollowUp> buildFollowUpByRequestId(List<Request> requests) {
        Map<Integer, RequestFollowUp> followUpByRequestId = new LinkedHashMap<>();
        for (Request request : requests) {
            followUpByRequestId.put(request.getIdRequest(), buildRequestFollowUp(request.getIdRequest()));
        }
        return followUpByRequestId;
    }

    private RequestFollowUp buildRequestFollowUp(int idRequest) {
        Integer contractId = null;
        Integer chatNegotiationId = null;
        for (Negotiation negotiation : negotiationDao.getByRequest(idRequest)) {
            if ("REJECTED".equals(negotiation.getStateOfApproval())
                    || "CANCELLED".equals(negotiation.getStateOfApproval())) {
                continue;
            }
            Contract contract = contractDao.getByNegotiationId(negotiation.getIdNegotiation());
            if (contract != null && contractId == null) {
                contractId = contract.getIdContract();
            }
            if (chatNegotiationId == null
                    && ("IN_PROGRESS".equals(negotiation.getStateOfApproval())
                    || "ACCEPTED".equals(negotiation.getStateOfApproval()))) {
                chatNegotiationId = negotiation.getIdNegotiation();
            }
        }
        return new RequestFollowUp(contractId, chatNegotiationId);
    }

    private static final class RequestFollowUp {
        private final Integer contractId;
        private final Integer chatNegotiationId;

        private RequestFollowUp(Integer contractId, Integer chatNegotiationId) {
            this.contractId = contractId;
            this.chatNegotiationId = chatNegotiationId;
        }

        public Integer getContractId() {
            return contractId;
        }

        public Integer getChatNegotiationId() {
            return chatNegotiationId;
        }
    }
}
