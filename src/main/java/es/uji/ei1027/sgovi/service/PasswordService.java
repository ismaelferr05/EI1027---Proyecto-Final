package es.uji.ei1027.sgovi.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.stereotype.Service;

/**
 * Servicio para encapsular el encriptado/chequeo de contraseñas usando Jasypt BasicPasswordEncryptor
 */
@Service
public class PasswordService {
	private final BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();

	public String encrypt(String plainPassword) {
		if (plainPassword == null) return null;
		return encryptor.encryptPassword(plainPassword);
	}

	public boolean check(String plainPassword, String encrypted) {
		if (plainPassword == null || encrypted == null) return false;
		return encryptor.checkPassword(plainPassword, encrypted);
	}
}


