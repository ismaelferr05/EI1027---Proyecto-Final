package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.model.CandidateProposal;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.service.RequestProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("requests", requestDao.getAll());
        return "request/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        Request request = new Request();
        request.setStatus("IN_REVIEW");
        model.addAttribute("request", request);
        model.addAttribute("oviUsers", oviUserDao.getAll());
        return "request/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("request") Request request, BindingResult bindingResult, Model model) {
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
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("request", requestDao.get(id));
        model.addAttribute("oviUsers", oviUserDao.getAll());
        return "request/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("request") Request request, BindingResult bindingResult, Model model) {
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
    public String delete(@PathVariable int id) {
        requestDao.delete(id);
        return "redirect:/requests/list";
    }

    @GetMapping("/frontoffice/add")
    public String frontOfficeAddForm(Model model) {
        Request request = new Request();
        request.setStatus("IN_REVIEW");
        model.addAttribute("request", request);
        model.addAttribute("oviUsers", oviUserDao.getAll());
        return "request/frontoffice-add";
    }

    @PostMapping("/frontoffice/add")
    public String frontOfficeAdd(@ModelAttribute("request") Request request, BindingResult bindingResult, Model model) {
        request.setStatus("IN_REVIEW");

        RequestValidator requestValidator = new RequestValidator();
        requestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("oviUsers", oviUserDao.getAll());
            return "request/frontoffice-add";
        }

        requestDao.add(request);
        return "redirect:/requests/frontoffice/track?idOviUser=" + request.getIdOviUser();
    }

    @GetMapping("/frontoffice/track")
    public String frontOfficeTrack(@RequestParam(value = "idOviUser", required = false) Integer idOviUser, Model model) {
        model.addAttribute("oviUsers", oviUserDao.getAll());
        model.addAttribute("selectedOviUser", idOviUser);
        model.addAttribute("requests", idOviUser == null ? new ArrayList<>() : requestDao.getByOviUser(idOviUser));
        return "request/frontoffice-track";
    }

    @GetMapping("/backoffice/list")
    public String backOfficeList(Model model) {
        model.addAttribute("pendingRequests", requestDao.getByStatus("IN_REVIEW"));
        model.addAttribute("approvedRequests", requestDao.getByStatus("APPROVED"));
        return "request/backoffice-list";
    }

    @GetMapping("/backoffice/review/{id}")
    public String backOfficeReview(@PathVariable int id,
                                   @RequestParam(value = "msg", required = false) String msg,
                                   Model model) {
        Request request = requestDao.get(id);
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
                                    @RequestParam(value = "selectedPapPatiIds", required = false) List<Integer> selectedPapPatiIds) {
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

        return "redirect:/requests/backoffice/review/" + idRequest + "?msg=Solicitud%20aprobada%20y%20propuesta%20generada";
    }

    @PostMapping("/backoffice/reject")
    public String backOfficeReject(@RequestParam("idRequest") int idRequest) {
        requestDao.updateStatus(idRequest, "REJECTED");
        return "redirect:/requests/backoffice/review/" + idRequest + "?msg=Solicitud%20rechazada";
    }
}

