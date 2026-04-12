package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Trainer;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TrainerValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Trainer.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Trainer trainer = (Trainer) obj;

        if (isBlank(trainer.getName())) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        } else if (trainer.getName().length() > 50) {
            errors.rejectValue("name", "maximo", "El nombre no puede superar 50 caracteres");
        }

        if (isBlank(trainer.getLastName())) {
            errors.rejectValue("lastName", "obligatorio", "El apellido es obligatorio");
        } else if (trainer.getLastName().length() > 50) {
            errors.rejectValue("lastName", "maximo", "El apellido no puede superar 50 caracteres");
        }

        if (trainer.getOccupation() != null && trainer.getOccupation().length() > 100) {
            errors.rejectValue("occupation", "maximo", "La ocupacion no puede superar 100 caracteres");
        }

        if (isBlank(trainer.getEmail())) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (trainer.getEmail().length() > 100) {
            errors.rejectValue("email", "maximo", "El email no puede superar 100 caracteres");
        }

        if (trainer.getPhone() != null && trainer.getPhone().length() > 20) {
            errors.rejectValue("phone", "maximo", "El telefono no puede superar 20 caracteres");
        }

        if (trainer.getAddress() != null && trainer.getAddress().length() > 150) {
            errors.rejectValue("address", "maximo", "La direccion no puede superar 150 caracteres");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
