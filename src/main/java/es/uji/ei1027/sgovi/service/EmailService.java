package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.model.EmailContent;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String FROM_ADDRESS = "no-reply@sgovi.local";

    public EmailContent sendRequestCreatedEmail(Request request, OviUser oviUser) {
        EmailContent email = createEmail(
                oviUser != null ? oviUser.getEmail() : null,
                "Solicitud registrada correctamente",
                buildRequestCreatedBody(request, oviUser)
        );
        logEmail(email);
        return email;
    }

    public EmailContent sendAcceptanceEmail(Request request, OviUser oviUser, PapPati selectedPapPati) {
        EmailContent email = createEmail(
                oviUser != null ? oviUser.getEmail() : null,
                "Solicitud aceptada - " + safeText(request != null ? request.getDescription() : null, "Solicitud"),
                buildAcceptanceBody(request, oviUser, selectedPapPati)
        );
        logEmail(email);
        return email;
    }

    public EmailContent sendRejectionEmail(Request request, OviUser oviUser) {
        EmailContent email = createEmail(
                oviUser != null ? oviUser.getEmail() : null,
                "Solicitud rechazada - " + safeText(request != null ? request.getDescription() : null, "Solicitud"),
                buildRejectionBody(request, oviUser)
        );
        logEmail(email);
        return email;
    }

    private EmailContent createEmail(String to, String subject, String body) {
        return new EmailContent(
                safeText(to, "desconocido@sgovi.local"),
                FROM_ADDRESS,
                subject,
                body,
                LocalDateTime.now()
        );
    }

    private String buildRequestCreatedBody(Request request, OviUser oviUser) {
        StringBuilder body = new StringBuilder();
        body.append("Hola ").append(formatUserName(oviUser)).append(",\n\n");
        body.append("Hemos recibido correctamente su solicitud y ha quedado en estado EN REVISIÓN.\n\n");
        appendRequestDetails(body, request);
        body.append("\nEn breve revisaremos la solicitud y le informaremos de cualquier novedad.\n\n");
        body.append("Saludos,\n");
        body.append("Equipo SGOVI");
        return body.toString();
    }

    private String buildAcceptanceBody(Request request, OviUser oviUser, PapPati selectedPapPati) {
        StringBuilder body = new StringBuilder();
        body.append("Hola ").append(formatUserName(oviUser)).append(",\n\n");
        body.append("Su solicitud ha sido ACEPTADA");
        if (selectedPapPati != null) {
            body.append(" y se ha asignado un asistente.\n\n");
            body.append("Asistente asignado: ")
                    .append(selectedPapPati.getName()).append(" ").append(selectedPapPati.getLastName()).append("\n");
            body.append("CP: ").append(safeText(selectedPapPati.getPc(), ""))
                    .append(" | Género: ").append(safeText(selectedPapPati.getGender(), ""))
                    .append(" | Edad: ").append(selectedPapPati.getAge() != null ? selectedPapPati.getAge() : "").append("\n\n");
        } else {
            body.append(".\n\n");
            body.append("Por el momento todavía no se ha asignado un asistente concreto.\n\n");
        }
        appendRequestDetails(body, request);
        body.append("\nSaludos,\n");
        body.append("Equipo SGOVI");
        return body.toString();
    }

    private String buildRejectionBody(Request request, OviUser oviUser) {
        StringBuilder body = new StringBuilder();
        body.append("Hola ").append(formatUserName(oviUser)).append(",\n\n");
        body.append("Lamentamos informarle de que su solicitud ha sido RECHAZADA.\n\n");
        appendRequestDetails(body, request);
        body.append("\nSi desea más información, puede contactar con el equipo gestor.\n\n");
        body.append("Saludos,\n");
        body.append("Equipo SGOVI");
        return body.toString();
    }

    private void appendRequestDetails(StringBuilder body, Request request) {
        if (request == null) {
            return;
        }
        body.append("Detalles de la solicitud:\n");
        body.append("- ID: ").append(request.getIdRequest()).append("\n");
        body.append("- Descripción: ").append(safeText(request.getDescription(), "")).append("\n");
        body.append("- Formación: ").append(safeText(request.getTraining(), "Sin formación indicada")).append("\n");
        body.append("- Periodo: ").append(request.getStartDate()).append(" -> ").append(request.getEndDate()).append("\n");
        body.append("- Estado: ").append(safeText(request.getStatus(), "")).append("\n");
    }

    private String formatUserName(OviUser oviUser) {
        if (oviUser == null) {
            return "usuario";
        }
        return safeText(oviUser.getName(), "") + (oviUser.getLastName() != null ? " " + oviUser.getLastName() : "");
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void logEmail(EmailContent email) {
        log.info("\n===== EMAIL SIMULADO =====\nPara: {}\nDe: {}\nAsunto: {}\nFecha: {}\nCuerpo:\n{}\n==========================",
                email.getTo(), email.getFrom(), email.getSubject(), email.getFormattedDate(), email.getBody());
    }
}

