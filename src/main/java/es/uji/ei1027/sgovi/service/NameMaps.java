package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.ActivityDao;
import es.uji.ei1027.sgovi.dao.NegotiationDao;
import es.uji.ei1027.sgovi.dao.OviUserDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.dao.RequestDao;
import es.uji.ei1027.sgovi.dao.TrainerDao;
import es.uji.ei1027.sgovi.model.Activity;
import es.uji.ei1027.sgovi.model.Negotiation;
import es.uji.ei1027.sgovi.model.OviUser;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.Request;
import es.uji.ei1027.sgovi.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Component("nameMaps")
public class NameMaps {

    @Autowired
    private OviUserDao oviUserDao;

    @Autowired
    private PapPatiDao papPatiDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private NegotiationDao negotiationDao;

    public Request requestById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            return requestDao.get(id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Negotiation negotiationById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            return negotiationDao.get(id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Activity activityById(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            return activityDao.get(id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String oviUserNameById(Integer id) {
        if (id == null) {
            return "—";
        }
        try {
            OviUser user = oviUserDao.get(id);
            return fullName(user.getName(), user.getLastName());
        } catch (EmptyResultDataAccessException e) {
            return "—";
        }
    }

    public String papPatiNameById(Integer id) {
        if (id == null) {
            return "—";
        }
        try {
            PapPati user = papPatiDao.get(id);
            return fullName(user.getName(), user.getLastName());
        } catch (EmptyResultDataAccessException e) {
            return "—";
        }
    }

    public String trainerNameById(Integer id) {
        if (id == null) {
            return "—";
        }
        try {
            Trainer trainer = trainerDao.get(id);
            return fullName(trainer.getName(), trainer.getLastName());
        } catch (EmptyResultDataAccessException e) {
            return "—";
        }
    }

    public String fullName(String name, String lastName) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasLastName = lastName != null && !lastName.isBlank();
        if (!hasName && !hasLastName) {
            return "—";
        }
        if (!hasName) {
            return lastName.trim();
        }
        if (!hasLastName) {
            return name.trim();
        }
        return name.trim() + " " + lastName.trim();
    }

    public String roleLabel(String role) {
        if (role == null) {
            return "—";
        }
        return switch (role) {
            case "OVIUSER" -> "Usuario OVI";
            case "PAPPATI" -> "PAP/PATI";
            case "TECNICO" -> "Técnico OVI";
            default -> role;
        };
    }

    public String recipientLabel(String type, Integer id) {
        if (type == null || id == null) {
            return "—";
        }
        if ("TECNICO".equals(type)) {
            return "Técnico OVI";
        }
        String name = "OVIUSER".equals(type) ? oviUserNameById(id) : papPatiNameById(id);
        if ("—".equals(name)) {
            return roleLabel(type) + " #" + id;
        }
        return name;
    }

    public String senderLabel(String role, Integer id) {
        if (role == null) {
            return "—";
        }
        if ("TECNICO".equals(role)) {
            return "Técnico OVI";
        }
        if (id == null) {
            return roleLabel(role);
        }
        String name = "OVIUSER".equals(role) ? oviUserNameById(id) : papPatiNameById(id);
        return "—".equals(name) ? roleLabel(role) + " #" + id : name;
    }
}
