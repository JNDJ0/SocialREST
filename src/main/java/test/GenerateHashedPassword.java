package test;

import util.PasswordUtils;

public class GenerateHashedPassword {
	public static void main(String[] args) {
		System.out.println(PasswordUtils.hashPassword("admin"));
	}
}
