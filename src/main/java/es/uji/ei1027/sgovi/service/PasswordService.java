package es.uji.ei1027.sgovi.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para encapsular el encriptado y la verificación de contraseñas con BCrypt.
 * Mantiene compatibilidad con hashes antiguos generados con Jasypt.
 */
@Service
public class PasswordService {
	private final BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder();
	private final BasicPasswordEncryptor legacyEncryptor = new BasicPasswordEncryptor();

	public String encrypt(String plainPassword) {
		if (plainPassword == null) return null;
		return encryptor.encode(plainPassword);
	}

	public boolean check(String plainPassword, String encrypted) {
		if (plainPassword == null || encrypted == null) return false;
		if (encrypted.startsWith("$2")) {
			return encryptor.matches(plainPassword, encrypted);
		}
		try {
			return legacyEncryptor.checkPassword(plainPassword, encrypted);
		} catch (RuntimeException ignored) {
			return false;
		}
	}
}
