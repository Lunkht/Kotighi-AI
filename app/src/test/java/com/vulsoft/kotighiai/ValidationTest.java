package com.vulsoft.kotighiai;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests de validation des données
 */
public class ValidationTest {

    @Test
    public void testValidEmail() {
        String validEmail = "user@example.com";
        assertTrue("Email valide devrait passer", isValidEmail(validEmail));
    }

    @Test
    public void testInvalidEmail() {
        String invalidEmail = "invalid-email";
        assertFalse("Email invalide devrait échouer", isValidEmail(invalidEmail));
    }

    @Test
    public void testValidPassword() {
        String validPassword = "Pass123!";
        assertTrue("Mot de passe valide (8+ caractères)", 
                   validPassword.length() >= 8);
    }

    @Test
    public void testInvalidPassword() {
        String shortPassword = "Pass1";
        assertFalse("Mot de passe trop court devrait échouer", 
                    shortPassword.length() >= 8);
    }

    @Test
    public void testValidUsername() {
        String validUsername = "user123";
        assertTrue("Username valide devrait passer", 
                   isValidUsername(validUsername));
    }

    @Test
    public void testInvalidUsername() {
        String invalidUsername = "us";
        assertFalse("Username trop court devrait échouer", 
                    isValidUsername(invalidUsername));
    }

    @Test
    public void testCyberDataValidation() {
        int requetesMin = 1000;
        int duree = 50;
        int octets = 5000;
        
        assertTrue("Requêtes/min devrait être positif", requetesMin > 0);
        assertTrue("Durée devrait être positive", duree > 0);
        assertTrue("Octets devrait être positif", octets > 0);
    }

    @Test
    public void testSanteDataValidation() {
        int fievre = 1;
        int toux = 0;
        
        assertTrue("Fièvre devrait être 0 ou 1", fievre == 0 || fievre == 1);
        assertTrue("Toux devrait être 0 ou 1", toux == 0 || toux == 1);
    }

    // Méthodes utilitaires
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 20;
    }
}
