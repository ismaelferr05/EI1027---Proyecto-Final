package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Request;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

public class RequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Request.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Request request = (Request) obj;

        if (isBlank(request.getDescription())) {
            errors.rejectValue("description", "obligatorio", "La descripción es obligatoria");
        }

        if (!isBlank(request.getTraining()) && request.getTraining().length() > 100) {
            errors.rejectValue("training", "maximo", "La formación no puede superar 100 caracteres");
        }

        if (request.getStartDate() == null) {
            errors.rejectValue("startDate", "obligatorio", "La fecha de inicio es obligatoria");
        } else if (request.getStartDate().isBefore(LocalDate.now())) {
            errors.rejectValue("startDate", "pasada", "La fecha de inicio no puede ser anterior a hoy");
        }

        if (request.getEndDate() == null) {
            errors.rejectValue("endDate", "obligatorio", "La fecha de fin es obligatoria");
        } else if (request.getEndDate().isBefore(LocalDate.now())) {
            errors.rejectValue("endDate", "pasada", "La fecha de fin no puede ser anterior a hoy");
        }

        if (request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            errors.rejectValue("endDate", "rango", "La fecha de fin debe ser igual o posterior a la fecha de inicio");
        }

        if (request.getAvailabilityDate() == null && "IN_REVIEW".equals(request.getStatus())) {
            errors.rejectValue("availabilityDate", "obligatorio", "La fecha de disponibilidad es obligatoria");
        } else if (request.getAvailabilityDate() != null) {
            if (request.getAvailabilityDate().isBefore(LocalDate.now())) {
                errors.rejectValue("availabilityDate", "pasada", "La fecha de disponibilidad no puede ser anterior a hoy");
            } else if (request.getStartDate() != null && request.getAvailabilityDate().isAfter(request.getStartDate())) {
                errors.rejectValue("availabilityDate", "rango", "La disponibilidad debe ser anterior o igual al inicio del periodo");
            }
        }

        if (request.getExperience() != null && request.getExperience() < 0) {
            errors.rejectValue("experience", "minimo", "La experiencia no puede ser negativa");
        }

        if (!isBlank(request.getExperienceType()) && request.getExperienceType().length() > 50) {
            errors.rejectValue("experienceType", "maximo", "El tipo de experiencia no puede superar 50 caracteres");
        }

        if (!isBlank(request.getPreferredGender()) && request.getPreferredGender().length() > 10) {
            errors.rejectValue("preferredGender", "maximo", "El género preferido no puede superar 10 caracteres");
        }

        if (!isBlank(request.getPreferredPc()) && request.getPreferredPc().length() > 10) {
            errors.rejectValue("preferredPc", "maximo", "El código postal preferido no puede superar 10 caracteres");
        }

        if (request.getPreferredAge() != null && request.getPreferredAge() < 0) {
            errors.rejectValue("preferredAge", "minimo", "La edad preferida no puede ser negativa");
        }

        if (isBlank(request.getStatus())) {
            errors.rejectValue("status", "obligatorio", "El estado es obligatorio");
        } else if (!isAllowedStatus(request.getStatus())) {
            errors.rejectValue("status", "invalido", "El estado no es válido");
        }

        if (request.getIdOviUser() == null) {
            errors.rejectValue("idOviUser", "obligatorio", "Debes seleccionar un usuario OVI");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isAllowedStatus(String status) {
        return "IN_REVIEW".equals(status)
                || "APPROVED".equals(status)
                || "REJECTED".equals(status)
                || "CONTRACT_ACTIVE".equals(status)
                || "CONTRACT_FINISHED".equals(status);
    }
}
