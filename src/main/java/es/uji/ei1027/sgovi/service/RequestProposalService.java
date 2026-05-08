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

@Service
public class RequestProposalService {

	@Autowired
	private PapPatiDao papPatiDao;

	@Autowired
	private ContractDao contractDao;

	public List<CandidateProposal> buildProposals(Request request) {
		List<CandidateProposal> proposals = new ArrayList<>();
		for (PapPati papPati : papPatiDao.getByStatus("ACCEPTED")) {
			if (!isAvailable(papPati, request)) {
				continue;
			}

			int score = 0;
			List<String> reasons = new ArrayList<>();

			int locationScore = scoreLocationMatch(papPati, request);
			score += locationScore;
			reasons.add("proximitat " + locationScore + "p");

			int genderScore = scoreGenderMatch(papPati, request);
			score += genderScore;
			reasons.add("genere " + genderScore + "p");

			int ageScore = scoreAgeMatch(papPati, request);
			score += ageScore;
			reasons.add("edat " + ageScore + "p");

			int trainingScore = scoreTrainingMatch(papPati, request);
			score += trainingScore;
			reasons.add("formacio " + trainingScore + "p");

			int experienceScore = scoreExperienceMatch(papPati, request);
			score += experienceScore;
			reasons.add("experiencia " + experienceScore + "p");

			int experienceTypeScore = scoreExperienceTypeMatch(papPati, request);
			score += experienceTypeScore;
			reasons.add("tipus experiencia " + experienceTypeScore + "p");

			// Priorizamos de forma clara la disponibilidad, criterio obligatorio del flujo.
			score += 25;
			reasons.add("disponible 25p");

			String summary = String.join(" · ", reasons);
			proposals.add(new CandidateProposal(papPati, score, summary));
		}

		proposals.sort(Comparator.comparingInt(CandidateProposal::getScore).reversed());
		return proposals;
	}

	private boolean isAvailable(PapPati papPati, Request request) {
		return !contractDao.hasOverlappingContractForPapPati(
				papPati.getIdPapPati(),
				request.getStartDate(),
				request.getEndDate()
		);
	}

	private int scoreLocationMatch(PapPati papPati, Request request) {
		if (isBlank(request.getPreferredPc())) {
			return 8;
		}
		if (isBlank(papPati.getPc())) {
			return 0;
		}

		if (request.getPreferredPc().equalsIgnoreCase(papPati.getPc())) {
			return 20;
		}

		String reqPrefix = request.getPreferredPc().length() >= 2 ? request.getPreferredPc().substring(0, 2) : request.getPreferredPc();
		String papPrefix = papPati.getPc().length() >= 2 ? papPati.getPc().substring(0, 2) : papPati.getPc();

		return reqPrefix.equalsIgnoreCase(papPrefix) ? 10 : 0;
	}

	private int scoreGenderMatch(PapPati papPati, Request request) {
		if (isBlank(request.getPreferredGender())) {
			return 5;
		}
		return normalizeGender(request.getPreferredGender()).equals(normalizeGender(papPati.getGender())) ? 15 : 0;
	}

	private int scoreAgeMatch(PapPati papPati, Request request) {
		if (request.getPreferredAge() == null || papPati.getAge() == null) {
			return 4;
		}

		int diff = Math.abs(request.getPreferredAge() - papPati.getAge());
		if (diff <= 5) {
			return 15;
		}
		if (diff <= 10) {
			return 8;
		}
		return 0;
	}

	private int scoreTrainingMatch(PapPati papPati, Request request) {
		if (isBlank(request.getTraining())) {
			return 4;
		}
		if (isBlank(papPati.getTraining())) {
			return 0;
		}

		String reqTraining = request.getTraining().toLowerCase(Locale.ROOT);
		String papTraining = papPati.getTraining().toLowerCase(Locale.ROOT);
		return papTraining.contains(reqTraining) ? 10 : 0;
	}

	private int scoreExperienceMatch(PapPati papPati, Request request) {
		if (request.getExperience() == null) {
			return 5;
		}

		Integer papYears = parseExperience(papPati.getExperience());
		if (papYears == null) {
			return 0;
		}

		if (papYears >= request.getExperience()) {
			return 10;
		}

		int gap = request.getExperience() - papYears;
		return Math.max(0, 10 - (gap * 2));
	}

	private int scoreExperienceTypeMatch(PapPati papPati, Request request) {
		if (isBlank(request.getExperienceType())) {
			return 3;
		}
		if (isBlank(papPati.getExperienceType())) {
			return 0;
		}

		return request.getExperienceType().trim().equalsIgnoreCase(papPati.getExperienceType().trim()) ? 5 : 0;
	}

	private Integer parseExperience(String value) {
		if (isBlank(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException ignored) {
			return null;
		}
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

