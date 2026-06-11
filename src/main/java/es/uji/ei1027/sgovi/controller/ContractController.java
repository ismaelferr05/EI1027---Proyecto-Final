package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.model.Contract;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.service.NameMaps;
import es.uji.ei1027.sgovi.service.SessionUserService;
import es.uji.ei1027.sgovi.service.TableViewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
            addContractsTable(model, "/contracts/list", contractDao.getAll(), q, sort, dir);
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
    public String papPatiList(HttpSession session, Model model,
                              @RequestParam(value = "q", required = false) String q,
                              @RequestParam(value = "sort", required = false) String sort,
                              @RequestParam(value = "dir", required = false) String dir) {
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
    public String oviUserList(HttpSession session, Model model,
                              @RequestParam(value = "q", required = false) String q,
                              @RequestParam(value = "sort", required = false) String sort,
                              @RequestParam(value = "dir", required = false) String dir) {
        if (!sessionUserService.isOviUser(session)) {
            return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
        }

        Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
        if (idOviUser == null) {
            return "redirect:/login";
        }

        addContractsTable(model, "/contracts/oviuser/list", contractDao.getByOviUserId(idOviUser), q, sort, dir);
        model.addAttribute("currentOviUser", sessionUserService.getCurrentOviUser(session));
        return "contracts/oviuser-list";
    }

    private void addContractsTable(Model model, String action, List<Contract> contracts, String q, String sort, String dir) {
        Map<String, Function<Contract, ?>> sorters = new LinkedHashMap<>();
        sorters.put("oviUser", contract -> {
            Negotiation negotiation = nameMaps.negotiationById(contract.getIdNegotiation());
            Request request = negotiation != null ? nameMaps.requestById(negotiation.getIdRequest()) : null;
            return request != null ? nameMaps.oviUserNameById(request.getIdOviUser()) : "";
        });
        sorters.put("papPati", contract -> {
            Negotiation negotiation = nameMaps.negotiationById(contract.getIdNegotiation());
            return negotiation != null ? nameMaps.papPatiNameById(negotiation.getIdPapPati()) : "";
        });
        sorters.put("wage", Contract::getWage);
        sorters.put("startDate", Contract::getStartDate);
        sorters.put("endDate", Contract::getEndDate);
        sorters.put("status", contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO" : "ACTIVO");
        sorters.put("negotiation", Contract::getIdNegotiation);
        sorters.put("document", Contract::getUrl);
        sorters.put("id", Contract::getIdContract);

        model.addAttribute("contracts", tableViewService.apply(contracts, q, sort, dir, sorters,
                tableViewService.fields(
                        Contract::getIdContract,
                        Contract::getWage,
                        Contract::getStartDate,
                        Contract::getEndDate,
                        Contract::getUrl,
                        Contract::getIdNegotiation,
                        contract -> {
                            Negotiation negotiation = nameMaps.negotiationById(contract.getIdNegotiation());
                            Request request = negotiation != null ? nameMaps.requestById(negotiation.getIdRequest()) : null;
                            return request != null ? request.getDescription() : "";
                        },
                        contract -> {
                            Negotiation negotiation = nameMaps.negotiationById(contract.getIdNegotiation());
                            Request request = negotiation != null ? nameMaps.requestById(negotiation.getIdRequest()) : null;
                            return request != null ? nameMaps.oviUserNameById(request.getIdOviUser()) : "";
                        },
                        contract -> {
                            Negotiation negotiation = nameMaps.negotiationById(contract.getIdNegotiation());
                            return negotiation != null ? nameMaps.papPatiNameById(negotiation.getIdPapPati()) : "";
                        },
                        contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO finalizado" : "ACTIVO activo"
                ),
                contract -> contract.getEndDate().isBefore(LocalDate.now()) ? "FINALIZADO" : "ACTIVO"));
        if ("/contracts/list".equals(action)) {
            tableViewService.addState(model, action, q, sort, dir,
                    tableViewService.options("oviUser", "Usuario OVI", "papPati", "PAP/PATI", "wage", "Salario", "startDate", "Inicio", "endDate", "Fin", "document", "Documento", "id", "ID"));
        } else if ("/contracts/oviuser/list".equals(action)) {
            tableViewService.addState(model, action, q, sort, dir,
                    tableViewService.options("papPati", "PAP/PATI", "wage", "Salario", "startDate", "Inicio", "endDate", "Fin", "status", "Estado", "document", "Documento", "id", "ID"),
                    tableViewService.contractStatusOptions());
        } else if ("/contracts/pappati/list".equals(action)) {
            tableViewService.addState(model, action, q, sort, dir,
                    tableViewService.options("oviUser", "Usuario OVI", "wage", "Salario", "startDate", "Inicio", "endDate", "Fin", "status", "Estado", "document", "Documento", "id", "ID"),
                    tableViewService.contractStatusOptions());
        } else {
            tableViewService.addState(model, action, q, sort, dir,
                    tableViewService.options("status", "Estado", "startDate", "Inicio", "endDate", "Fin", "document", "Documento", "id", "ID"),
                    tableViewService.contractStatusOptions());
        }
    }

    @GetMapping("/add")
    public String addForm(@RequestParam(value = "negotiationId", required = false) Integer negotiationId, HttpSession session, Model model) {
        if (!sessionUserService.isTechnician(session) && !sessionUserService.isOviUser(session)) {
            return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
        }

        Contract contract = new Contract();
        Negotiation negotiation = null;
        Request request = null;

        if (negotiationId != null) {
            negotiation = negotiationDao.get(negotiationId);
            if (negotiation == null || !canCreateContractForNegotiation(session, negotiation)) {
                return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
            }

            if (contractDao.getByNegotiationId(negotiation.getIdNegotiation()) != null) {
                return sessionUserService.isOviUser(session) ? "redirect:/contracts/oviuser/list" : "redirect:/contracts/list";
            }

            request = requestDao.get(negotiation.getIdRequest());
            if (request != null) {
                contract.setIdNegotiation(negotiation.getIdNegotiation());
                contract.setStartDate(request.getStartDate());
                contract.setEndDate(request.getEndDate());
                contract.setUrl(buildAutoContractUrl(negotiation.getIdNegotiation()));
            }
        }

        model.addAttribute("contract", contract);
        model.addAttribute("negotiations", availableNegotiations(session));
        model.addAttribute("requestBasedContract", request != null);
        model.addAttribute("selectedNegotiation", negotiation);
        model.addAttribute("selectedRequest", request);
        model.addAttribute("isOviUserCreator", sessionUserService.isOviUser(session));
        return "contracts/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("contract") Contract contract, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessionUserService.isTechnician(session) && !sessionUserService.isOviUser(session)) {
            return sessionUserService.getCurrentUser(session) == null ? "redirect:/login" : "redirect:/dashboard";
        }

        boolean requestBasedContract = false;
        Negotiation negotiation = null;
        Request request = null;
        if (!sessionUserService.isTechnician(session)) {
            if (contract.getIdNegotiation() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Debes abrir el contrato desde un chat para usar esta opción.");
                return sessionUserService.isOviUser(session) ? "redirect:/messages/list" : "redirect:/contracts/list";
            }

            negotiation = negotiationDao.get(contract.getIdNegotiation());
            if (negotiation == null || !canCreateContractForNegotiation(session, negotiation)) {
                redirectAttributes.addFlashAttribute("errorMessage", "No puedes crear este contrato.");
                return sessionUserService.isOviUser(session) ? "redirect:/messages/list" : "redirect:/contracts/list";
            }

            request = requestDao.get(negotiation.getIdRequest());
            if (request != null) {
                requestBasedContract = true;
                contract.setStartDate(request.getStartDate());
                contract.setEndDate(request.getEndDate());
                if (contract.getUrl() == null || contract.getUrl().isBlank()) {
                    contract.setUrl(buildAutoContractUrl(negotiation.getIdNegotiation()));
                }
            }
        }

        validateContract(contract, bindingResult, session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("requestBasedContract", requestBasedContract);
            model.addAttribute("selectedRequest", request);
            model.addAttribute("selectedNegotiation", negotiation);
            model.addAttribute("isOviUserCreator", sessionUserService.isOviUser(session));
            model.addAttribute("negotiations", availableNegotiations(session));
            return "contracts/add";
        }

        int createdContractId;
        try {
            createdContractId = contractDao.add(contract);
        } catch (DataIntegrityViolationException ex) {
            // Defensa ante concurrencia: si otro usuario crea el contrato entre validación e inserción.
            bindingResult.rejectValue("idNegotiation", "duplicate", "Ya existe un contrato para esta negociación");
            model.addAttribute("requestBasedContract", requestBasedContract);
            model.addAttribute("selectedRequest", request);
            model.addAttribute("selectedNegotiation", negotiation);
            model.addAttribute("isOviUserCreator", sessionUserService.isOviUser(session));
            model.addAttribute("negotiations", availableNegotiations(session));
            return "contracts/add";
        }
        if (requestBasedContract) {
            String finalUrl = "/contracts/view/" + createdContractId;
            contract.setUrl(finalUrl);
            contractDao.updateUrl(createdContractId, finalUrl);
        }
        markRequestByContract(contract);
        redirectAttributes.addFlashAttribute("successMessage", "Contrato registrado correctamente.");
        return sessionUserService.isOviUser(session) ? "redirect:/contracts/oviuser/list" : "redirect:/contracts/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") int id, HttpSession session, Model model) {
        if (!canManageContract(id, session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("contract", contractDao.get(id));
        model.addAttribute("negotiations", availableNegotiations(session));
        model.addAttribute("isOviUserEditor", sessionUserService.isOviUser(session));
        return "contracts/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("contract") Contract contract, BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!canManageContract(contract.getIdContract(), session)) {
            return "redirect:/dashboard";
        }

        Contract persisted = contractDao.get(contract.getIdContract());
        // Las fechas del contrato son inmutables en edición.
        contract.setStartDate(persisted.getStartDate());
        contract.setEndDate(persisted.getEndDate());

        if (sessionUserService.isOviUser(session)) {
            // El usuario OVI conserva la negociación original del contrato.
            contract.setIdNegotiation(persisted.getIdNegotiation());
        }

        validateContract(contract, bindingResult, session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("negotiations", availableNegotiations(session));
            model.addAttribute("isOviUserEditor", sessionUserService.isOviUser(session));
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

    @GetMapping("/document/{id}")
    public String document(@PathVariable int id, HttpSession session) {
        if (sessionUserService.getCurrentUser(session) == null) {
            return "redirect:/login";
        }

        Contract contract = contractDao.get(id);
        if (contract == null || contract.getUrl() == null || contract.getUrl().isBlank()) {
            return "redirect:/dashboard";
        }

        if (sessionUserService.isTechnician(session)) {
            return "redirect:" + contract.getUrl();
        }

        if (sessionUserService.isPapPati(session)) {
            Integer idPapPati = sessionUserService.getCurrentPapPatiId(session);
            if (idPapPati == null || !contractDao.belongsToPapPati(id, idPapPati)) {
                return "redirect:/contracts/pappati/list";
            }
            return "redirect:" + contract.getUrl();
        }

        if (sessionUserService.isOviUser(session)) {
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            if (idOviUser == null || !contractDao.belongsToOviUser(id, idOviUser)) {
                return "redirect:/contracts/oviuser/list";
            }
            return "redirect:" + contract.getUrl();
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
            return negotiationDao.getAll().stream()
                    .filter(negotiation -> contractDao.getByNegotiationId(negotiation.getIdNegotiation()) == null)
                    .toList();
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
        return negotiations.stream()
                .filter(this::isContractableNegotiation)
                .filter(negotiation -> contractDao.getByNegotiationId(negotiation.getIdNegotiation()) == null)
                .toList();
    }

    private boolean canCreateContractForNegotiation(HttpSession session, Negotiation negotiation) {
        if (sessionUserService.isTechnician(session)) {
            return true;
        }

        if (!isContractableNegotiation(negotiation)) {
            return false;
        }

        if (sessionUserService.isOviUser(session) && negotiation.getIdRequest() != null) {
            Request request = requestDao.get(negotiation.getIdRequest());
            Integer idOviUser = sessionUserService.getCurrentOviUserId(session);
            return request != null && idOviUser != null && idOviUser.equals(request.getIdOviUser());
        }

        return false;
    }

    private boolean isContractableNegotiation(Negotiation negotiation) {
        if (negotiation == null || negotiation.getStateOfApproval() == null) {
            return false;
        }
        String state = negotiation.getStateOfApproval();
        return "IN_PROGRESS".equals(state) || "ACCEPTED".equals(state);
    }

    private String buildAutoContractUrl(int idNegotiation) {
        return "AUTO_CONTRACT_NEGOTIATION_" + idNegotiation;
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
        } else if (sessionUserService.isOviUser(session)) {
            boolean allowed = availableNegotiations(session).stream()
                    .anyMatch(n -> n.getIdNegotiation() == contract.getIdNegotiation());
            if (!allowed && contract.getIdContract() > 0 && canManageContract(contract.getIdContract(), session)) {
                Contract persisted = contractDao.get(contract.getIdContract());
                allowed = persisted != null && contract.getIdNegotiation().equals(persisted.getIdNegotiation());
            }
            if (!allowed) {
                bindingResult.rejectValue("idNegotiation", "forbidden", "No puedes registrar contrato para esta negociación");
            }
        }

        if (contract.getIdNegotiation() != null) {
            Contract existing = contractDao.getByNegotiationId(contract.getIdNegotiation());
            boolean isDifferentContract = existing != null && existing.getIdContract() != contract.getIdContract();
            if (isDifferentContract) {
                bindingResult.rejectValue("idNegotiation", "duplicate", "Ya existe un contrato para esta negociación");
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
