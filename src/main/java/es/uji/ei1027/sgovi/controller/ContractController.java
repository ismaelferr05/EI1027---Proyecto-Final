package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.model.Contract;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private NegotiationDao negotiationDao;

    @Autowired
    private RequestDao requestDao;

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

        if (sessionUserService.isOviUser(session)) {
            return "redirect:/contracts/oviuser/list";
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

        List<Contract> contracts = contractDao.getByPapPatiId(idPapPati);
        LocalDate today = LocalDate.now();
        long activeContractCount = contracts.stream()
                .filter(contract -> !contract.getEndDate().isBefore(today))
                .count();

        model.addAttribute("contracts", contracts);
        model.addAttribute("contractCount", contracts.size());
        model.addAttribute("activeContractCount", activeContractCount);
        model.addAttribute("currentPapPati", sessionUserService.getCurrentPapPati(session));
        return "contracts/pappati-list";
    }

    @GetMapping("/oviuser/list")
    public String oviUserList(HttpSession session, Model model) {
        if (!sessionUserService.isOviUser(session)) {
            return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
        }

        Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
        if (idOviUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("contracts", contractDao.getByOviUserId(idOviUser));
        model.addAttribute("currentOviUser", sessionUserService.getCurrentOviUser(session));
        return "contracts/oviuser-list";
    }

    @GetMapping("/add")
    public String addForm(@RequestParam(value = "negotiationId", required = false) Integer negotiationId, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session) && !sessionUserService.isOviUser(session)) {
            return "redirect:/dashboard";
        }

        Contract contract = new Contract();
        contract.setIdNegotiation(negotiationId);
        model.addAttribute("contract", contract);
        model.addAttribute("negotiations", availableNegotiations(session));
        return "contracts/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("contract") Contract contract, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session) && !sessionUserService.isOviUser(session)) {
            return "redirect:/dashboard";
        }
        validateContract(contract, bindingResult, session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiations", availableNegotiations(session));
            return "contracts/add";
        }

        contractDao.add(contract);
        markRequestByContract(contract);
        redirectAttributes.addFlashAttribute("successMessage", "Contrato registrado correctamente.");
        return sessionUserService.isOviUser(session) ? "redirect:/contracts/oviuser/list" : "redirect:/contracts/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, HttpSession session, Model model) {
        if (!canManageContract(id, session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("contract", contractDao.get(id));
        model.addAttribute("negotiations", availableNegotiations(session));
        return "contracts/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!canManageContract(contract.getIdContract(), session)) {
            return "redirect:/dashboard";
        }
        validateContract(contract, bindingResult, session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiations", availableNegotiations(session));
            return "contracts/edit";
        }

        contractDao.update(contract);
        markRequestByContract(contract);
        redirectAttributes.addFlashAttribute("successMessage", "Contrato actualizado correctamente.");
        return sessionUserService.isOviUser(session) ? "redirect:/contracts/oviuser/list" : "redirect:/contracts/list";
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

        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null || !contractDao.belongsToOviUser(id, idOviUser)) {
                return "redirect:/contracts/oviuser/list";
            }
            model.addAttribute("contract", contractDao.get(id));
            return "contracts/view";
        }

        return "redirect:/dashboard";
    }

    private boolean canManageContract(int idContract, HttpSession session) {
        if (sessionUserService.isTechnician(session)) {
            return true;
        }
        Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
        return idOviUser != null && contractDao.belongsToOviUser(idContract, idOviUser);
    }

    private List<Negotiation> availableNegotiations(HttpSession session) {
        if (sessionUserService.isTechnician(session)) {
            return negotiationDao.getAll();
        }
        Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
        List<Negotiation> negotiations = new ArrayList<>();
        if (idOviUser == null) {
            return negotiations;
        }
        for (Request request : requestDao.getByOviUser(idOviUser)) {
            if ("APPROVED".equals(request.getStatus()) || "CONTRACT_ACTIVE".equals(request.getStatus())) {
                negotiations.addAll(negotiationDao.getByRequest(request.getIdRequest()));
            }
        }
        return negotiations;
    }

    private void validateContract(Contract contract, BindingResult bindingResult, HttpSession session) {
        if (contract.getWage() == null || contract.getWage().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("wage", "invalid", "El salario debe ser mayor que 0");
        }
        if (contract.getStartDate() == null) {
            bindingResult.rejectValue("startDate", "required", "La fecha de inicio es obligatoria");
        }
        if (contract.getEndDate() == null) {
            bindingResult.rejectValue("endDate", "required", "La fecha de fin es obligatoria");
        }
        if (contract.getStartDate() != null && contract.getEndDate() != null && contract.getEndDate().isBefore(contract.getStartDate())) {
            bindingResult.rejectValue("endDate", "invalid", "La fecha de fin no puede ser anterior a la de inicio");
        }
        if (contract.getUrl() == null || contract.getUrl().isBlank()) {
            bindingResult.rejectValue("url", "required", "La URL del contrato es obligatoria");
        }
        if (contract.getIdNegotiation() == null) {
            bindingResult.rejectValue("idNegotiation", "required", "Debes seleccionar una negociación");
        } else if (!sessionUserService.isTechnician(session)) {
            boolean allowed = availableNegotiations(session).stream()
                    .anyMatch(n -> n.getIdNegotiation() == contract.getIdNegotiation());
            if (!allowed) {
                bindingResult.rejectValue("idNegotiation", "forbidden", "No puedes registrar contrato para esta negociación");
            }
        }
    }

    private void markRequestByContract(Contract contract) {
        if (contract.getIdNegotiation() == null) {
            return;
        }
        Negotiation negotiation = negotiationDao.get(contract.getIdNegotiation());
        if (negotiation == null || negotiation.getIdRequest() == null) {
            return;
        }
        String status = contract.getEndDate() != null && contract.getEndDate().isBefore(LocalDate.now())
                ? "CONTRACT_FINISHED"
                : "CONTRACT_ACTIVE";
        requestDao.updateStatus(negotiation.getIdRequest(), status);
        negotiation.setStateOfApproval("ACCEPTED");
        negotiationDao.update(negotiation);
    }
}
