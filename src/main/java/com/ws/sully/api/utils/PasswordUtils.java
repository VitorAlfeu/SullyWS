package com.ws.sully.api.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {

	//private static final String SALT = "$2a$15$llw0G6IyibUob8h5XRt9xuRczaGdCm/{AX@)$;[Mxd8p]c-65e#<";
	
	public PasswordUtils() {
	}

	/**
	 * Gera um hash utilizando o BCrypt.
	 * 
	 * @param senha
	 * @return String
	 */
	public static String criptografarString(String string) {
		if (string == null) {
			return string;
		}	
		return BCrypt.hashpw(string, BCrypt.gensalt(15));
	}
	
	public static boolean descriptografarString(String string, String stringCriptografada) {
		if ((string == null) || (stringCriptografada == null)) {
			return false;
		}
		
		return BCrypt.checkpw(string, stringCriptografada); 
	}
}