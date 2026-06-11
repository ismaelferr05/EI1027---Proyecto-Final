-- Datos de demo coherentes. Se recargan en cada arranque junto con schema.sql.
--
-- Cuentas de prueba:
--   Técnico:  tecnico1@gmail.com / 1tecnico123
--   OVI:      ana.martinez@oviuser.com / password1
--             laura.gomez@oviuser.com / password3
--             miguel.torres@oviuser.com / password5
--   PAP/PATI: jordi.puig@pappati.com / password1
--             marta.vidal@pappati.com / password2
--             elena.costa@pappati.com / password4

INSERT INTO Trainer (trainer_id, email, name, lastName, occupation, phone, address) VALUES
    (1, 'carlos.garcia@trainer.com', 'Carlos', 'García', 'Fisioterapeuta', '612345678', 'Av. del Cid 12, Valencia'),
    (2, 'maria.lopez@trainer.com',   'María',  'López',  'Psicóloga',      '623456789', 'Carrer Major 5, Castellón'),
    (3, 'david.serra@trainer.com',   'David',  'Serra',  'Educador social','634567890', 'Plaza Mayor 3, Alicante');

INSERT INTO Technician (technician_id, email, password, name, lastName) VALUES
    (1, 'tecnico1@gmail.com', '1tecnico123', 'Técnico', 'Principal');

INSERT INTO Activity (activity_id, name, date, duration, location, category, description, trainer_id) VALUES
    (1, 'Taller de autonomía personal', '2026-05-10', 120, 'Valencia',  'Taller',    'Técnicas para la vida independiente', 1),
    (2, 'Yoga adaptado',               '2026-05-15',  90, 'Castellón', 'Deporte',   'Sesión de yoga adaptada', 2),
    (3, 'Gestión del hogar',           '2026-06-01', 180, 'Valencia',  'Formación', 'Organización doméstica', 1),
    (4, 'Taller de comunicación',      '2026-07-20',  90, 'Valencia',  'Taller',    'Comunicación interpersonal', 1),
    (5, 'Actividad física adaptada',   '2026-08-15',  60, 'Castellón', 'Deporte',   'Ejercicio adaptado en grupo', 2),
    (6, 'Primeros auxilios básicos',   '2026-09-10', 120, 'Alicante',  'Formación', 'Emergencias domésticas', 3);

INSERT INTO OviUser (oviuser_id, name, lastName, email, phone, password, province, town, pc, age, gender, status, rejectionReason, lopdConsent) VALUES
    (1, 'Ana',    'Martínez', 'ana.martinez@oviuser.com',  '611111111', 'password1', 'Valencia', 'Valencia', '46001', 30, 'F', 'ACCEPTED', NULL, true),
    (2, 'Pedro',  'Sánchez',  'pedro.sanchez@oviuser.com', '622222222', 'password2', 'Castellón','Castellón','12001', 25, 'M', 'PENDING',  NULL, true),
    (3, 'Laura',  'Gómez',    'laura.gomez@oviuser.com',   '633333333', 'password3', 'Alicante', 'Alicante', '03001', 40, 'F', 'ACCEPTED', NULL, true),
    (4, 'Carmen', 'Ruiz',     'carmen.ruiz@oviuser.com',   '644444441', 'password4', 'Valencia', 'Valencia', '46003', 55, 'F', 'REJECTED', 'Documentación incompleta.', true),
    (5, 'Miguel', 'Torres',   'miguel.torres@oviuser.com', '655555552', 'password5', 'Valencia', 'Valencia', '46004', 45, 'M', 'ACCEPTED', NULL, true);

INSERT INTO PapPati (pappati_id, name, lastName, email, phone, password, province, town, pc, age, gender, cvUrl, training, experience, experienceType, availabilityStartDate, availabilityEndDate, status, rejectionReason) VALUES
    (1, 'Jordi', 'Puig',    'jordi.puig@pappati.com',    '644444444', 'password1', 'Valencia',  'Valencia',  '46002', 35, 'M', 'http://cv.sgovi.es/jordi', 'Grado en Trabajo Social',   '3', 'Formal', '2026-01-01', '2026-12-31', 'ACCEPTED', NULL),
    (2, 'Marta', 'Vidal',   'marta.vidal@pappati.com',   '655555555', 'password2', 'Castellón', 'Castellón', '12002', 28, 'F', 'http://cv.sgovi.es/marta', 'FP Auxiliar Enfermería',    '2', 'Formal', '2026-01-01', '2026-06-30', 'ACCEPTED', NULL),
    (3, 'Elena', 'Costa',   'elena.costa@pappati.com',   '677777777', 'password4', 'Valencia',  'Valencia',  '46001', 32, 'F', 'http://cv.sgovi.es/elena', 'Grado en Educación Social', '4', 'Formal', '2026-01-01', '2026-12-31', 'ACCEPTED', NULL),
    (4, 'Pablo', 'Navarro', 'pablo.navarro@pappati.com', '688888888', 'password5', 'Valencia',  'Valencia',  '46005', 38, 'M', 'http://cv.sgovi.es/pablo', 'Técnico en Cuidados Aux.',  '5', 'Formal', '2026-01-01', '2026-12-31', 'ACCEPTED', NULL),
    (5, 'Tomàs', 'Ferrer',  'tomas.ferrer@pappati.com',  '666666666', 'password3', 'Alicante',  'Alicante',  '03002', 42, 'M', NULL,                       NULL,                        NULL, NULL,     '2026-01-01', '2026-12-31', 'PENDING',  NULL);

-- Solicitud 1: Ana con contrato activo (Jordi).
-- Solicitud 2: Laura aprobada, chat en curso con Jordi.
-- Solicitud 3: Ana aprobada, 3 PAP/PATI propuestos pendientes de chat.
-- Solicitud 4: Miguel en revisión (aprobar con varios candidatos).
-- Solicitud 5: Miguel rechazada (ejemplo).
INSERT INTO Request (request_id, description, training, startDate, endDate, experience, experienceType, preferredGender, preferredPc, preferredAge, status, rejectionReason, oviuser_id) VALUES
    (1, 'Apoyo con tareas del hogar y desplazamientos diarios.',
        'Auxiliar de enfermería', '2026-07-01', '2026-12-31', 2, 'Formal', 'M', '46001', 30, 'CONTRACT_ACTIVE', NULL, 1),
    (2, 'Asistente para movilidad y actividades externas.',
        NULL, '2026-07-01', '2026-11-30', NULL, NULL, 'F', '03001', 40, 'APPROVED', NULL, 3),
    (3, 'Apoyo vespertino los fines de semana.',
        'Educación social', '2026-08-01', '2026-12-15', 1, 'Formal', NULL, '46001', NULL, 'APPROVED', NULL, 1),
    (4, 'Ayuda con medicación y citas médicas.',
        'Auxiliar de enfermería', '2026-09-01', '2026-12-31', 2, 'Formal', 'F', '46004', 45, 'IN_REVIEW', NULL, 5),
    (5, 'Acompañamiento en desplazamientos laborales.',
        NULL, '2026-01-01', '2026-06-30', NULL, NULL, NULL, NULL, NULL, 'REJECTED', 'No hay candidatos disponibles en la zona.', 5);

INSERT INTO Negotiation (negotiation_id, stateOfApproval, request_id, pappati_id) VALUES
    (1, 'ACCEPTED',    1, 1),
    (2, 'REJECTED',    1, 2),
    (3, 'IN_PROGRESS', 2, 1),
    (4, 'PENDING',     3, 3),
    (5, 'PENDING',     3, 4);

INSERT INTO Message (messageId, messageDateTime, sender, receiver, text, negotiation_id) VALUES
    (1, '2026-06-01 10:00:00', 'Jordi Puig',   'Ana Martínez', 'Hola Ana, me interesa tu solicitud.', 1),
    (2, '2026-06-01 11:30:00', 'Ana Martínez', 'Jordi Puig',   'Perfecto, ¿cuándo podrías empezar?', 1),
    (3, '2026-06-01 12:00:00', 'Jordi Puig',   'Ana Martínez', 'Podría empezar en julio.', 1),
    (4, '2026-06-05 09:00:00', 'Jordi Puig',   'Laura Gómez',  'Buenos días Laura, vi tu solicitud.', 3),
    (5, '2026-06-05 10:15:00', 'Laura Gómez',  'Jordi Puig',   '¿Tienes experiencia con movilidad reducida?', 3),
    (6, '2026-06-05 11:00:00', 'Jordi Puig',   'Laura Gómez',  'Sí, dos años en centros de día.', 3);

INSERT INTO Contract (contract_id, wage, startDate, endDate, url, negotiation_id) VALUES
    (1, 15.50, '2026-07-01', '2026-12-31', '/contracts/view/1', 1);

INSERT INTO ParticipantList (participantList_id, attendance, attendanceCertificateUrl, activity_id, oviuser_id, pappati_id) VALUES
    (1, true,  'http://certs.sgovi.es/1', 1, 1,    NULL),
    (2, true,  'http://certs.sgovi.es/2', 1, NULL, 1),
    (3, false, NULL,                      4, 3,    NULL),
    (4, false, NULL,                      5, NULL, 3);

INSERT INTO TechnicianCommunication (communication_id, communicationDateTime, senderRole, senderId, recipientType, recipientId, subject, text) VALUES
    (1, '2026-06-02 09:00:00', 'OVIUSER', 1, 'TECNICO', 1, 'Duda sobre mi contrato', '¿Puedo ampliar el periodo del contrato?'),
    (2, '2026-06-02 11:00:00', 'TECNICO', 1, 'OVIUSER', 1, 'Re: Duda sobre mi contrato', 'Sí, puedes solicitar una ampliación desde negociaciones activas.'),
    (3, '2026-06-03 10:30:00', 'PAPPATI', 1, 'TECNICO', 1, 'Disponibilidad julio', 'Estaré disponible todo julio.'),
    (4, '2026-06-03 12:00:00', 'TECNICO', 1, 'PAPPATI', 1, 'Re: Disponibilidad julio', 'Gracias, lo tendremos en cuenta.'),
    (5, '2026-06-08 08:45:00', 'OVIUSER', 5, 'TECNICO', 1, 'Documentación médica', '¿Debo adjuntar informes médicos?'),
    (6, '2026-06-08 09:30:00', 'TECNICO', 1, 'OVIUSER', 5, 'Re: Documentación médica', 'No es obligatorio, pero ayuda a priorizar candidaturas.');

SELECT setval(pg_get_serial_sequence('trainer', 'trainer_id'), (SELECT MAX(trainer_id) FROM trainer));
SELECT setval(pg_get_serial_sequence('technician', 'technician_id'), (SELECT MAX(technician_id) FROM technician));
SELECT setval(pg_get_serial_sequence('activity', 'activity_id'), (SELECT MAX(activity_id) FROM activity));
SELECT setval(pg_get_serial_sequence('oviuser', 'oviuser_id'), (SELECT MAX(oviuser_id) FROM oviuser));
SELECT setval(pg_get_serial_sequence('pappati', 'pappati_id'), (SELECT MAX(pappati_id) FROM pappati));
SELECT setval(pg_get_serial_sequence('participantlist', 'participantlist_id'), (SELECT MAX(participantlist_id) FROM participantlist));
SELECT setval(pg_get_serial_sequence('request', 'request_id'), (SELECT MAX(request_id) FROM request));
SELECT setval(pg_get_serial_sequence('negotiation', 'negotiation_id'), (SELECT MAX(negotiation_id) FROM negotiation));
SELECT setval(pg_get_serial_sequence('message', 'messageid'), (SELECT MAX(messageid) FROM message));
SELECT setval(pg_get_serial_sequence('contract', 'contract_id'), (SELECT MAX(contract_id) FROM contract));
SELECT setval(pg_get_serial_sequence('techniciancommunication', 'communication_id'), (SELECT MAX(communication_id) FROM techniciancommunication));
