package com.neasaa.base.app.utils;

import org.jasypt.util.text.BasicTextEncryptor;

public class DBEncryptionUtil {
	
	private static final String DB_PASSWORD_ENCRYPTION_SALT_KEY = "DB_PASSWORD_ENCRYPTION_SALT_KEY";
	
	public static void main(String[] args) {
		if(args == null || args.length ==0) {
			System.out.println("Usage:" + DBEncryptionUtil.class.getName() + "<Password to encrypt>");
			System.out.println("Note: Set Database password encryption salt key as env variable " + DB_PASSWORD_ENCRYPTION_SALT_KEY);
			System.err.println("Failed to encrypt password");
			System.exit(1);
		}
		System.out.println("Encrypted value for <" + args[0] + "> is <" + encrypt(args[0]) + ">.");
    }
	
	private static BasicTextEncryptor getEncryptorInstance () {
		String dbPasswordEncryptionSaltKey = System.getenv("DB_PASSWORD_ENCRYPTION_SALT_KEY");
        if(dbPasswordEncryptionSaltKey == null) {
        	throw new RuntimeException("Database password encryption salt key is not set. Set env variable " + DB_PASSWORD_ENCRYPTION_SALT_KEY);
        }
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(dbPasswordEncryptionSaltKey);
        return encryptor;
	}
	
	private static String encrypt(String plainText) {
        return getEncryptorInstance().encrypt(plainText);
    }

    public static String decrypt(String encryptedText) {
        return getEncryptorInstance().decrypt(encryptedText);
    }
}
