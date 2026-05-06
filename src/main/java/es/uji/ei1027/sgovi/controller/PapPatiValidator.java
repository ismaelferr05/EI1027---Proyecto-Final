package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.PapPati;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PapPatiValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return PapPati.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        PapPati papPati = (PapPati) obj;

        if (isBlank(papPati.getName())) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        } else if (papPati.getName().length() > 50) {
            errors.rejectValue("name", "maximo", "El nombre no puede superar 50 caracteres");
        }

        if (isBlank(papPati.getLastName())) {
            errors.rejectValue("lastName", "obligatorio", "Los apellidos son obligatorios");
        } else if (papPati.getLastName().length() > 50) {
            errors.rejectValue("lastName", "maximo", "Los apellidos no pueden superar 50 caracteres");
        }

        if (isBlank(papPati.getEmail())) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (papPati.getEmail().length() > 100) {
            errors.rejectValue("email", "maximo", "El email no puede superar 100 caracteres");
        }

        if (isBlank(papPati.getPhone())) {
            errors.rejectValue("phone", "obligatorio", "El teléfono es obligatorio");
        } else if (papPati.getPhone().length() > 20) {
            errors.rejectValue("phone", "maximo", "El teléfono no puede superar 20 caracteres");
        }

        if (isBlank(papPati.getPassword())) {
            errors.rejectValue("password", "obligatorio", "La contraseña es obligatoria");
        } else if (papPati.getPassword().length() > 255) {
            errors.rejectValue("password", "maximo", "La contraseña no puede superar 255 caracteres");
        }

        if (!isBlank(papPati.getProvince()) && papPati.getProvince().length() > 50) {
            errors.rejectValue("province", "maximo", "La provincia no puede superar 50 caracteres");
        }

        if (!isBlank(papPati.getTown()) && papPati.getTown().length() > 50) {
            errors.rejectValue("town", "maximo", "La población no puede superar 50 caracteres");
        }

        if (!isBlank(papPati.getPc()) && papPati.getPc().length() > 10) {
            errors.rejectValue("pc", "maximo", "El código postal no puede superar 10 caracteres");
        }

        if (papPati.getAge() != null && papPati.getAge() < 0) {
            errors.rejectValue("age", "minimo", "La edad no puede ser negativa");
        }

        if (!isBlank(papPati.getGender()) && papPati.getGender().length() > 10) {
            errors.rejectValue("gender", "maximo", "El género no puede superar 10 caracteres");
        }

        if (!isBlank(papPati.getCvUrl()) && papPati.getCvUrl().length() > 255) {
            errors.rejectValue("cvUrl", "maximo", "La URL del CV no puede superar 255 caracteres");
        }

        if (!isBlank(papPati.getTraining()) && papPati.getTraining().length() > 100) {
            errors.rejectValue("training", "maximo", "La formación no puede superar 100 caracteres");
        }

        if (!isBlank(papPati.getExperience()) && papPati.getExperience().length() > 50) {
            errors.rejectValue("experience", "maximo", "La experiencia no puede superar 50 caracteres");
        }

        if (!isBlank(papPati.getExperienceType()) && papPati.getExperienceType().length() > 50) {
            errors.rejectValue("experienceType", "maximo", "El tipo de experiencia no puede superar 50 caracteres");
        }

        if (isBlank(papPati.getStatus())) {
            errors.rejectValue("status", "obligatorio", "El estado es obligatorio");
        } else if (!isAllowedStatus(papPati.getStatus())) {
            errors.rejectValue("status", "invalido", "El estado no es válido");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isAllowedStatus(String status) {
        return "PENDING".equals(status)
                || "ACCEPTED".equals(status)
                || "REJECTED".equals(status);
    }
}

