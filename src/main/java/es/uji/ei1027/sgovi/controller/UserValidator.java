package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.validation.Errors;

public class UserValidator {

    public boolean supports(Class<?> cls) {
        return UserDetails.class.isAssignableFrom(cls);
    }
    public void validate(Object target, Errors errors) {
        es.uji.ei1027.sgovi.model.UserDetails user = (es.uji.ei1027.sgovi.model.UserDetails) target;

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "error.email", "El email no puede estar vacío");
        } else if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.rejectValue("email", "error.email", "El email no tiene un formato válido");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "error.password", "La contraseña no puede estar vacía");
        }
    }
}
