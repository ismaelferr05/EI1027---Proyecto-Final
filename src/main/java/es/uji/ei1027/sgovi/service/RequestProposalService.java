package es.uji.ei1027.sgovi.service;

import es.uji.ei1027.sgovi.dao.ContractDao;
import es.uji.ei1027.sgovi.dao.PapPatiDao;
import es.uji.ei1027.sgovi.model.CandidateProposal;
import es.uji.ei1027.sgovi.model.PapPati;
import es.uji.ei1027.sgovi.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RequestProposalService {

	private static final int MAX_SCORE_TOTAL = 100;
	private static final int MAX_SCORE_UBICACION = 20;
	private static final int MAX_SCORE_GENERO = 15;
	private static final int MAX_SCORE_EDAD = 15;
	private static final int MAX_SCORE_FORMACION = 10;
	private static final int MAX_SCORE_EXPERIENCIA = 10;
	private static final int MAX_SCORE_TIPO_EXPERIENCIA = 5;
	private static final int MAX_SCORE_DISPONIBILIDAD = 25;
	private static final Pattern FIRST_NUMBER = Pattern.compile("\\d+");

	private record CriterionScore(int points, String detail, String label) {}

	@Autowired
	private PapPatiDao papPatiDao;

	@Autowired
	private ContractDao contractDao;

	public List<CandidateProposal> buildProposals(Request request) {
		List<CandidateProposal> proposals = new ArrayList<>();
		if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
			return proposals;
		}

		for (PapPati papPati : papPatiDao.getByStatus("ACCEPTED")) {
			boolean available = isAvailable(papPati, request);

			int score = 0;
			List<String> reasons = new ArrayList<>();

			CriterionScore locationScore = scoreLocationMatch(papPati, request);
			score += addReason(reasons, locationScore, MAX_SCORE_UBICACION);

			CriterionScore genderScore = scoreGenderMatch(papPati, request);
			score += addReason(reasons, genderScore, MAX_SCORE_GENERO);

			CriterionScore ageScore = scoreAgeMatch(papPati, request);
			score += addReason(reasons, ageScore, MAX_SCORE_EDAD);

			CriterionScore trainingScore = scoreTrainingMatch(papPati, request);
			score += addReason(reasons, trainingScore, MAX_SCORE_FORMACION);

			CriterionScore experienceScore = scoreExperienceMatch(papPati, request);
			score += addReason(reasons, experienceScore, MAX_SCORE_EXPERIENCIA);

			CriterionScore experienceTypeScore = scoreExperienceTypeMatch(papPati, request);
			score += addReason(reasons, experienceTypeScore, MAX_SCORE_TIPO_EXPERIENCIA);

			score += addReason(reasons, scoreAvailability(available), MAX_SCORE_DISPONIBILIDAD);

			String summary = score + "/" + MAX_SCORE_TOTAL;
			proposals.add(new CandidateProposal(papPati, score, summary, List.copyOf(reasons)));
		}

		proposals.sort(Comparator.comparingInt(CandidateProposal::getScore).reversed());
		return proposals;
	}

	private boolean isAvailable(PapPati papPati, Request request) {
		if (papPati == null || request == null || request.getStartDate() == null || request.getEndDate() == null) {
			return false;
		}
		return !contractDao.hasOverlappingContractForPapPati(
				papPati.getIdPapPati(),
				request.getStartDate(),
				request.getEndDate()
		);
	}

	private CriterionScore scoreAvailability(boolean available) {
		if (available) {
			return new CriterionScore(MAX_SCORE_DISPONIBILIDAD, "disponible en todo el periodo solicitado", "Disponibilidad");
		}
		return new CriterionScore(0, "tiene un contrato que se solapa con el periodo solicitado", "Disponibilidad");
	}

	private CriterionScore scoreLocationMatch(PapPati papPati, Request request) {
		if (isBlank(request.getPreferredPc())) {
			return new CriterionScore(8, "sin preferencia de CP", "Ubicación");
		}
		if (isBlank(papPati.getPc())) {
			return new CriterionScore(0, "el PAP/PATI no tiene CP registrado", "Ubicación");
		}

		if (request.getPreferredPc().equalsIgnoreCase(papPati.getPc())) {
			return new CriterionScore(20, "CP exacto", "Ubicación");
		}

		String reqPrefix = request.getPreferredPc().length() >= 2 ? request.getPreferredPc().substring(0, 2) : request.getPreferredPc();
		String papPrefix = papPati.getPc().length() >= 2 ? papPati.getPc().substring(0, 2) : papPati.getPc();

		if (reqPrefix.equalsIgnoreCase(papPrefix)) {
			return new CriterionScore(10, "mismo prefijo de CP", "Ubicación");
		}

		return new CriterionScore(0, "CP distinto", "Ubicación");
	}

	private CriterionScore scoreGenderMatch(PapPati papPati, Request request) {
		if (isBlank(request.getPreferredGender())) {
			return new CriterionScore(5, "sin preferencia de género", "Género");
		}
		if (normalizeGender(request.getPreferredGender()).equals(normalizeGender(papPati.getGender()))) {
			return new CriterionScore(15, "coincide con el género preferido", "Género");
		}
		return new CriterionScore(0, "género no coincidente", "Género");
	}

	private CriterionScore scoreAgeMatch(PapPati papPati, Request request) {
		if (request.getPreferredAge() == null || papPati.getAge() == null) {
			return new CriterionScore(4, "falta edad para comparar", "Edad");
		}

		int diff = Math.abs(request.getPreferredAge() - papPati.getAge());
		if (diff <= 5) {
			return new CriterionScore(15, "diferencia de edad menor o igual a 5 años", "Edad");
		}
		if (diff <= 10) {
			return new CriterionScore(8, "diferencia de edad menor o igual a 10 años", "Edad");
		}
		return new CriterionScore(0, "diferencia de edad superior a 10 años", "Edad");
	}

	private CriterionScore scoreTrainingMatch(PapPati papPati, Request request) {
		if (isBlank(request.getTraining())) {
			return new CriterionScore(4, "sin preferencia de formación", "Formación");
		}
		if (isBlank(papPati.getTraining())) {
			return new CriterionScore(0, "el PAP/PATI no indica formación", "Formación");
		}

		String reqTraining = normalizeText(request.getTraining());
		String papTraining = normalizeText(papPati.getTraining());
		if (papTraining.contains(reqTraining) || reqTraining.contains(papTraining)) {
			return new CriterionScore(10, "la formación del PAP/PATI contiene la formación solicitada", "Formación");
		}
		if (sharesRelevantToken(reqTraining, papTraining)) {
			return new CriterionScore(5, "formación relacionada parcialmente", "Formación");
		}
		return new CriterionScore(2, "formación distinta, pero se mantiene como candidato", "Formación");
	}

	private CriterionScore scoreExperienceMatch(PapPati papPati, Request request) {
		if (request.getExperience() == null) {
			return new CriterionScore(5, "sin preferencia de experiencia", "Experiencia");
		}

		Integer papYears = parseExperience(papPati.getExperience());
		if (papYears == null) {
			return new CriterionScore(0, "la experiencia del PAP/PATI no es numérica o no existe", "Experiencia");
		}

		if (papYears >= request.getExperience()) {
			return new CriterionScore(10, "experiencia igual o superior a la solicitada", "Experiencia");
		}

		int gap = request.getExperience() - papYears;
		int points = Math.max(0, 10 - (gap * 2));
		return new CriterionScore(points, "experiencia inferior en " + gap + " año(s)", "Experiencia");
	}

	private CriterionScore scoreExperienceTypeMatch(PapPati papPati, Request request) {
		if (isBlank(request.getExperienceType())) {
			return new CriterionScore(3, "sin preferencia de tipo de experiencia", "Tipo de experiencia");
		}
		if (isBlank(papPati.getExperienceType())) {
			return new CriterionScore(0, "el PAP/PATI no indica tipo de experiencia", "Tipo de experiencia");
		}

		String requestType = normalizeText(request.getExperienceType());
		String papType = normalizeText(papPati.getExperienceType());
		if (requestType.equals(papType) || requestType.contains(papType) || papType.contains(requestType)) {
			return new CriterionScore(5, "coincide el tipo de experiencia", "Tipo de experiencia");
		}
		if (sharesRelevantToken(requestType, papType)) {
			return new CriterionScore(3, "tipo de experiencia relacionado parcialmente", "Tipo de experiencia");
		}
		return new CriterionScore(1, "tipo de experiencia distinto, pero se mantiene como candidato", "Tipo de experiencia");
	}

	private int addReason(List<String> reasons, CriterionScore score, int maxPuntos) {
		reasons.add(String.format(Locale.ROOT, "%s: %d/%d - %s", score.label(), score.points(), maxPuntos, score.detail()));
		return score.points();
	}

	private Integer parseExperience(String value) {
		if (isBlank(value)) {
			return null;
		}
		Matcher matcher = FIRST_NUMBER.matcher(value);
		return matcher.find() ? Integer.parseInt(matcher.group()) : null;
	}

	private boolean sharesRelevantToken(String left, String right) {
		if (isBlank(left) || isBlank(right)) {
			return false;
		}
		for (String leftToken : left.split("\\s+")) {
			if (leftToken.length() < 4) {
				continue;
			}
			for (String rightToken : right.split("\\s+")) {
				if (rightToken.length() >= 4 && (leftToken.contains(rightToken) || rightToken.contains(leftToken))) {
					return true;
				}
			}
		}
		return false;
	}

	private String normalizeText(String value) {
		if (isBlank(value)) {
			return "";
		}
		String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.toLowerCase(Locale.ROOT);
		return normalized.replaceAll("[^\\p{Alnum}]+", " ").trim().replaceAll("\\s+", " ");
	}

	private String normalizeGender(String value) {
		if (isBlank(value)) {
			return "";
		}

		String normalized = value.trim().toUpperCase(Locale.ROOT);
		if ("MASCULINO".equals(normalized)) {
			return "M";
		}
		if ("FEMENINO".equals(normalized)) {
			return "F";
		}
		return normalized;
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
