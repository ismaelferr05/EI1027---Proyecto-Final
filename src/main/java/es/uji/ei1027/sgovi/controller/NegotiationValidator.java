package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Negotiation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class NegotiationValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Negotiation.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Negotiation negotiation = (Negotiation) obj;

        if (isBlank(negotiation.getStateOfApproval())) {
            errors.rejectValue("stateOfApproval", "obligatorio", "El estado es obligatorio");
        } else if (!isAllowedState(negotiation.getStateOfApproval())) {
            errors.rejectValue("stateOfApproval", "invalido", "El estado no es válido");
        }

        if (negotiation.getIdRequest() == null) {
            errors.rejectValue("idRequest", "obligatorio", "Debes seleccionar una solicitud");
        }

        if (negotiation.getIdPapPati() == null) {
            errors.rejectValue("idPapPati", "obligatorio", "Debes seleccionar un PAP PATI");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isAllowedState(String state) {
        return "PENDING".equals(state)
                || "ACCEPTED".equals(state)
                || "IN_PROGRESS".equals(state)
                || "REJECTED".equals(state)
                || "CANCELLED".equals(state);
    }
}

