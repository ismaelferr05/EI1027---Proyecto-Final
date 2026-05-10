package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.model.CandidateProposal;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.UserDetails;
import es.uji.ei1027.sgovi.service.RequestProposalService;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestProposalService requestProposalService;

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
            model.addAttribute("requests", requestDao.getByOviUser(idOviUser));
        } else {
            model.addAttribute("requests", requestDao.getAll());
        }

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
        return "request/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUsers", oviUserDao.getAll());
            return "request/add";
        }

        requestDao.add(request);
        return "redirect:/requests/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("request", requestDao.get(id));
        model.addAttribute("oviUsers", oviUserDao.getAll());
        return "request/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUsers", oviUserDao.getAll());
            return "request/edit";
        }

        requestDao.update(request);
        return "redirect:/requests/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        requestDao.delete(id);
        return "redirect:/requests/list";
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
        return "request/frontoffice-add";
    }

    @PostMapping("/frontoffice/add")
    public String frontOfficeAdd(@ModelAttribute("request") Request request, BindingResult bindingResult, HttpSession session, Model model) {
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
            return "request/frontoffice-add";
        }

        requestDao.add(request);
        return "redirect:/requests/frontoffice/track";
    }

    @GetMapping("/frontoffice/track")
    public String frontOfficeTrack(HttpSession session, Model model) {
        if (!sessionUserService.isOviUser(session) && !sessionUserService.isTechnician(session)) {
            return "redirect:/login";
        }

        model.addAttribute("isTechnician", sessionUserService.isTechnician(session));
        model.addAttribute("isOviUser", sessionUserService.isOviUser(session));
        model.addAttribute("isPapPati", sessionUserService.isPapPati(session));

        if (sessionUserService.isTechnician(session)) {
            model.addAttribute("requests", requestDao.getAll());
        } else {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null) {
                return "redirect:/login";
            }
            model.addAttribute("requests", requestDao.getByOviUser(idOviUser));
            model.addAttribute("currentOviUser", sessionUserService.getCurrentOviUser(session));
        }

        return "request/frontoffice-track";
    }

    @GetMapping("/backoffice/list")
    public String backOfficeList(HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pendingRequests", requestDao.getByStatus("IN_REVIEW"));
        model.addAttribute("approvedRequests", requestDao.getByStatus("APPROVED"));
        return "request/backoffice-list";
    }

    @GetMapping("/backoffice/review/{id}")
    public String backOfficeReview(@PathVariable int id,
                                   @RequestParam(value = "msg", required = false) String msg,
                                   HttpSession session,
                                   Model model) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        Request request = requestDao.get(id);
        if (request == null) {
            return "redirect:/requests/backoffice/list";
        }

        List<CandidateProposal> proposals = requestProposalService.buildProposals(request);
        Set<Integer> existingPapPatis = new HashSet<>();
        for (Negotiation negotiation : negotiationDao.getByRequest(id)) {
            existingPapPatis.add(negotiation.getIdPapPati());
        }

        model.addAttribute("request", request);
        model.addAttribute("proposals", proposals);
        model.addAttribute("existingPapPatis", existingPapPatis);
        model.addAttribute("msg", msg);
        return "request/backoffice-review";
    }

    @PostMapping("/backoffice/approve")
    public String backOfficeApprove(@RequestParam("idRequest") int idRequest,
                                    @RequestParam(value = "selectedPapPatiIds", required = false) List<Integer> selectedPapPatiIds,
                                    HttpSession session) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        requestDao.updateStatus(idRequest, "APPROVED");

        if (selectedPapPatiIds != null) {
            for (Integer idPapPati : selectedPapPatiIds) {
                if (!negotiationDao.existsByRequestAndPapPati(idRequest, idPapPati)) {
                    Negotiation negotiation = new Negotiation();
                    negotiation.setStateOfApproval("PENDING");
                    negotiation.setIdRequest(idRequest);
                    negotiation.setIdPapPati(idPapPati);
                    negotiationDao.add(negotiation);
                }
            }
        }

        String redirectUrl = "/requests/backoffice/review/" + idRequest + "?msg=Solicitud%20aprobada%20y%20propuesta%20generada";
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/backoffice/reject")
    public String backOfficeReject(@RequestParam("idRequest") int idRequest, HttpSession session) {
        if (!sessionUserService.isTechnician(session)) {
            return "redirect:/dashboard";
        }

        requestDao.updateStatus(idRequest, "REJECTED");
        String redirectUrl = "/requests/backoffice/review/" + idRequest + "?msg=Solicitud%20rechazada";
        return "redirect:" + redirectUrl;
    }
}

