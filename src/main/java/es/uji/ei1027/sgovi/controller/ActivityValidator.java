package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Activity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ActivityValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Activity.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Activity activity = (Activity) obj;

        if (isBlank(activity.getName())) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        } else if (activity.getName().length() > 100) {
            errors.rejectValue("name", "maximo", "El nombre no puede superar 100 caracteres");
        }

        if (activity.getDate() == null) {
            errors.rejectValue("date", "obligatorio", "La fecha es obligatoria");
        }

        if (activity.getDuration() < 1) {
            errors.rejectValue("duration", "minimo", "La duracion debe ser al menos 1 hora");
        }

        if (isBlank(activity.getLocation())) {
            errors.rejectValue("location", "obligatorio", "La localizacion es obligatoria");
        } else if (activity.getLocation().length() > 150) {
            errors.rejectValue("location", "maximo", "La localizacion no puede superar 150 caracteres");
        }

        if (isBlank(activity.getCategory())) {
            errors.rejectValue("category", "obligatorio", "La categoria es obligatoria");
        } else if (activity.getCategory().length() > 50) {
            errors.rejectValue("category", "maximo", "La categoria no puede superar 50 caracteres");
        }

        if (activity.getDescription() != null && activity.getDescription().length() > 1000) {
            errors.rejectValue("description", "maximo", "La descripcion no puede superar 1000 caracteres");
        }

        if (activity.getIdTrainer() == null) {
            errors.rejectValue("idTrainer", "obligatorio", "Debes seleccionar un formador");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

