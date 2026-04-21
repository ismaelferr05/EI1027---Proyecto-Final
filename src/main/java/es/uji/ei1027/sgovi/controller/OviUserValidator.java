package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.OviUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OviUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return OviUser.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        OviUser user = (OviUser) obj;

        if (isBlank(user.getName())) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        } else if (user.getName().length() > 50) {
            errors.rejectValue("name", "maximo", "El nombre no puede superar 50 caracteres");
        }

        if (isBlank(user.getPhone())) {
            errors.rejectValue("phone", "obligatorio", "El telefono es obligatorio");
        } else if (user.getPhone().length() > 20) {
            errors.rejectValue("phone", "maximo", "El telefono no puede superar 20 caracteres");
        }

        if (isBlank(user.getLastName())) {
            errors.rejectValue("lastName", "obligatorio", "Los apellidos son obligatorios");
        } else if (user.getLastName().length() > 50) {
            errors.rejectValue("lastName", "maximo", "Los apellidos no pueden superar 50 caracteres");
        }

        if (isBlank(user.getEmail())) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (user.getEmail().length() > 100) {
            errors.rejectValue("email", "maximo", "El email no puede superar 100 caracteres");
        }

        if (isBlank(user.getPassword())) {
            errors.rejectValue("password", "obligatorio", "La contrasena es obligatoria");
        } else if (user.getPassword().length() > 255) {
            errors.rejectValue("password", "maximo", "La contrasena no puede superar 255 caracteres");
        }

        if (!isBlank(user.getProvince()) && user.getProvince().length() > 50) {
            errors.rejectValue("province", "maximo", "La provincia no puede superar 50 caracteres");
        }

        if (!isBlank(user.getTown()) && user.getTown().length() > 50) {
            errors.rejectValue("town", "maximo", "La ciudad no puede superar 50 caracteres");
        }

        if (!isBlank(user.getPc()) && user.getPc().length() > 10) {
            errors.rejectValue("pc", "maximo", "El codigo postal no puede superar 10 caracteres");
        }

        if (user.getAge() != null && user.getAge() < 0) {
            errors.rejectValue("age", "minimo", "La edad no puede ser negativa");
        }

        if (!isBlank(user.getGender()) && user.getGender().length() > 10) {
            errors.rejectValue("gender", "maximo", "El genero no puede superar 10 caracteres");
        }

        if (isBlank(user.getStatus())) {
            errors.rejectValue("status", "obligatorio", "El estado es obligatorio");
        } else if (user.getStatus().length() > 20) {
            errors.rejectValue("status", "maximo", "El estado no puede superar 20 caracteres");
        }

        if (!user.isLopdConsent()) {
            errors.rejectValue("lopdConsent", "obligatorio", "Debes aceptar el consentimiento LOPD");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
