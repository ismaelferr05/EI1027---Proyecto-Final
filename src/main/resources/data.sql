-- Trainers
INSERT INTO Trainer (trainer_id, email, name, lastName, occupation, phone, address)
VALUES
    (1, 'carlos.garcia@trainer.com', 'Carlos', 'García', 'Fisioterapeuta', '612345678', 'Av. del Cid 12, Valencia'),
    (2, 'maria.lopez@trainer.com',   'María',  'López',  'Psicóloga',      '623456789', 'Carrer Major 5, Castellón')
ON CONFLICT (trainer_id) DO NOTHING;

-- Technicians
INSERT INTO Technician (technician_id, email, password, name, lastName)
VALUES
    (1, 'tecnico1@gmail.com', '1tecnico123', 'Técnico', 'Principal')
ON CONFLICT (technician_id) DO NOTHING;

-- Activities
INSERT INTO Activity (activity_id, name, date, duration, location, category, description, trainer_id)
VALUES
    (1, 'Taller de autonomía personal', '2026-05-10', 120, 'Valencia',  'Taller',    'Técnicas para la vida independiente', 1),
    (2, 'Yoga adaptado',               '2026-05-15',  90, 'Castellón', 'Deporte',   'Sesión de yoga adaptada a diversidad funcional', 2),
    (3, 'Gestión del hogar',           '2026-06-01', 180, 'Valencia',  'Formación', 'Organización doméstica y habilidades cotidianas', 1)
ON CONFLICT (activity_id) DO NOTHING;

-- OviUsers
INSERT INTO OviUser (oviuser_id, name, lastName, email, phone, password, province, town, pc, age, gender, status, lopdConsent)
VALUES
    (1, 'Ana',   'Martínez', 'ana.martinez@oviuser.com',   '611111111', 'password1', 'Valencia',   'Valencia',   '46001', 30, 'F', 'ACCEPTED', true),
    (2, 'Pedro', 'Sánchez',  'pedro.sanchez@oviuser.com',  '622222222', 'password2', 'Castellón',  'Castellón',  '12001', 25, 'M', 'PENDING',  true),
    (3, 'Laura', 'Gómez',    'laura.gomez@oviuser.com',    '633333333', 'password3', 'Alicante',   'Alicante',   '03001', 40, 'F', 'ACCEPTED', true)
ON CONFLICT (oviuser_id) DO NOTHING;

-- PapPatis
INSERT INTO PapPati (pappati_id, name, lastName, email, phone, password, province, town, pc, age, gender, cvUrl, training, experience, experienceType, status)
VALUES
    (1, 'Jordi', 'Puig',   'jordi.puig@pappati.com',   '644444444', 'password1', 'Valencia',  'Valencia',  '46002', 35, 'M', 'http://cv.sgovi.es/jordi',  'Grado en Trabajo Social',   '3',  'Formal',   'ACCEPTED'),
    (2, 'Marta', 'Vidal',  'marta.vidal@pappati.com',  '655555555', 'password2', 'Castellón', 'Castellón', '12002', 28, 'F', 'http://cv.sgovi.es/marta',  'FP Auxiliar Enfermería',    '2',  'Formal',   'ACCEPTED'),
    (3, 'Tomàs', 'Ferrer', 'tomas.ferrer@pappati.com', '666666666', 'password3', 'Alicante',  'Alicante',  '03002', 42, 'M', NULL,                        NULL,                        NULL, NULL,       'PENDING')
ON CONFLICT (pappati_id) DO NOTHING;

-- ParticipantLists (oviuser_id OR pappati_id, nunca los dos)
INSERT INTO ParticipantList (participantList_id, attendance, attendanceCertificateUrl, activity_id, oviuser_id, pappati_id)
VALUES
    (1, true,  'http://certs.sgovi.es/cert1', 1, 1,    NULL),
    (2, true,  'http://certs.sgovi.es/cert2', 1, NULL, 1),
    (3, false, NULL,                          2, 3,    NULL),
    (4, true,  'http://certs.sgovi.es/cert4', 2, NULL, 2)
ON CONFLICT (participantList_id) DO NOTHING;

-- Requests
INSERT INTO Request (request_id, description, training, startDate, endDate, experience, experienceType, preferredGender, preferredPc, preferredAge, status, oviuser_id)
VALUES
    (1, 'Necesito apoyo con tareas del hogar y desplazamientos diarios.', 'Auxiliar de enfermería', '2026-05-01', '2026-12-31', 2, 'Formal', 'F', '46001', 30, 'APPROVED',    1),
    (2, 'Busco asistente para ayuda en movilidad y actividades externas.', NULL,                    '2026-06-01', '2026-11-30', NULL, NULL,   NULL, NULL,    NULL, 'IN_REVIEW', 3)
ON CONFLICT (request_id) DO NOTHING;

-- Negotiations
INSERT INTO Negotiation (negotiation_id, stateOfApproval, request_id, pappati_id)
VALUES
    (1, 'ACCEPTED',    1, 1),
    (2, 'REJECTED',    1, 2),
    (3, 'IN_PROGRESS', 2, 1)
ON CONFLICT (negotiation_id) DO NOTHING;

-- Messages
INSERT INTO Message (messageId, messageDateTime, sender, receiver, text, negotiation_id)
VALUES
    (1, '2026-04-01 10:00:00', 'Jordi',  'Ana',   'Hola Ana, me interesa tu solicitud. ¿Podemos hablar?', 1),
    (2, '2026-04-01 11:30:00', 'Ana',    'Jordi', 'Hola Jordi, perfecto. ¿Cuándo podrías empezar?',       1),
    (3, '2026-04-01 12:00:00', 'Jordi',  'Ana',   'Podría empezar el 1 de mayo sin problema.',             1),
    (4, '2026-04-10 09:00:00', 'Jordi',  'Laura', 'Buenos días Laura, vi tu solicitud y me parece interesante.', 3)
ON CONFLICT (messageId) DO NOTHING;

-- Contracts
INSERT INTO Contract (contract_id, wage, startDate, endDate, url, negotiation_id)
VALUES
    (1, 15.50, '2026-05-01', '2026-12-31', 'http://contratos.sgovi.es/contrato1.pdf', 1)
ON CONFLICT (contract_id) DO NOTHING;

-- Alinea secuencias identity para que los siguientes inserts automaticos no repitan IDs.
SELECT setval(pg_get_serial_sequence('trainer', 'trainer_id'), COALESCE((SELECT MAX(trainer_id) FROM trainer), 1), true);
SELECT setval(pg_get_serial_sequence('technician', 'technician_id'), COALESCE((SELECT MAX(technician_id) FROM technician), 1), true);
SELECT setval(pg_get_serial_sequence('activity', 'activity_id'), COALESCE((SELECT MAX(activity_id) FROM activity), 1), true);
SELECT setval(pg_get_serial_sequence('oviuser', 'oviuser_id'), COALESCE((SELECT MAX(oviuser_id) FROM oviuser), 1), true);
SELECT setval(pg_get_serial_sequence('pappati', 'pappati_id'), COALESCE((SELECT MAX(pappati_id) FROM pappati), 1), true);
SELECT setval(pg_get_serial_sequence('participantlist', 'participantlist_id'), COALESCE((SELECT MAX(participantlist_id) FROM participantlist), 1), true);
SELECT setval(pg_get_serial_sequence('request', 'request_id'), COALESCE((SELECT MAX(request_id) FROM request), 1), true);
SELECT setval(pg_get_serial_sequence('negotiation', 'negotiation_id'), COALESCE((SELECT MAX(negotiation_id) FROM negotiation), 1), true);
SELECT setval(pg_get_serial_sequence('message', 'messageid'), COALESCE((SELECT MAX(messageid) FROM message), 1), true);
SELECT setval(pg_get_serial_sequence('contract', 'contract_id'), COALESCE((SELECT MAX(contract_id) FROM contract), 1), true);
