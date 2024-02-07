package com.cloud.test;

public class CaesarCipher {

  
    public static String encrypt(String plainText, int key) {
        StringBuilder encryptedText = new StringBuilder();

        for (char character : plainText.toCharArray()) {
            if (Character.isLetter(character)) {
                char shifted = (char) (((character - 'a' + key) % 26) + 'a');
                encryptedText.append(shifted);
            } else {
                encryptedText.append(character);
            }
        }

        return encryptedText.toString();
    }

   
    public static String decrypt(String encryptedText, int key) {
        StringBuilder decryptedText = new StringBuilder();

        for (char character : encryptedText.toCharArray()) {
            if (Character.isLetter(character)) {
                char shifted = (char) (((character - 'a' - key + 26) % 26) + 'a');
                decryptedText.append(shifted);
            } else {
                decryptedText.append(character);
            }
        }

        return decryptedText.toString();
    }
}
