package es.uji.ei1027.sgovi.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio para encapsular el encriptado/chequeo de contraseñas usando Jasypt BasicPasswordEncryptor
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
			if (legacyEncryptor.checkPassword(plainPassword, encrypted)) {
				return true;
			}
		} catch (RuntimeException ignored) {
			// Compatible con contraseñas antiguas guardadas en texto plano o hashes inválidos.
		}
		return plainPassword.equals(encrypted);
	}
}

