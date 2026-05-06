package es.uji.ei1027.sgovi.controller;

import es.uji.ei1027.sgovi.model.ParticipantList;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ParticipantListValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return ParticipantList.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ParticipantList participantList = (ParticipantList) obj;

        if (participantList.getIdActivity() == null) {
            errors.rejectValue("idActivity", "obligatorio", "Debes seleccionar una actividad");
        }

        if (!isBlank(participantList.getAttendanceCertificateUrl()) && participantList.getAttendanceCertificateUrl().length() > 255) {
            errors.rejectValue("attendanceCertificateUrl", "maximo", "La URL del certificado no puede superar 255 caracteres");
        }

        if (participantList.getIdOviUser() == null && participantList.getIdPapPati() == null) {
            errors.rejectValue("idOviUser", "obligatorio", "Debes seleccionar al menos un usuario OVI o un PAP PATI");
            errors.rejectValue("idPapPati", "obligatorio", "Debes seleccionar al menos un usuario OVI o un PAP PATI");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

