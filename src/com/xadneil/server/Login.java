package com.xadneil.server;

import java.util.HashMap;
import java.util.Map;

public class Login {

	private static Map<String, String> login = new HashMap<>();

	static {
		login.put("2", "");
		login.put("1", "");
	}

	public static boolean check(String username, String password) {
		return login.containsKey(username)
				&& login.get(username).equals(password);
	}
}
