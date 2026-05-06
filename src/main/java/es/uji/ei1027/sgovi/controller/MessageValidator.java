package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.Message;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class MessageValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Message.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Message message = (Message) obj;

        if (message.getMessageDateTime() == null) {
            errors.rejectValue("messageDateTime", "obligatorio", "La fecha y hora son obligatorias");
        }

        if (isBlank(message.getSender())) {
            errors.rejectValue("sender", "obligatorio", "El remitente es obligatorio");
        } else if (message.getSender().length() > 20) {
            errors.rejectValue("sender", "maximo", "El remitente no puede superar 20 caracteres");
        }

        if (isBlank(message.getReceiver())) {
            errors.rejectValue("receiver", "obligatorio", "El destinatario es obligatorio");
        } else if (message.getReceiver().length() > 20) {
            errors.rejectValue("receiver", "maximo", "El destinatario no puede superar 20 caracteres");
        }

        if (isBlank(message.getText())) {
            errors.rejectValue("text", "obligatorio", "El texto es obligatorio");
        } else if (message.getText().length() > 300) {
            errors.rejectValue("text", "maximo", "El texto no puede superar 300 caracteres");
        }

        if (message.getIdNegotiation() == null) {
            errors.rejectValue("idNegotiation", "obligatorio", "Debes seleccionar una negociación");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

