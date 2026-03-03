package com.vulsoft.kotighiai;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.json.JSONObject;

/**
 * Tests unitaires pour ApiClient
 */
public class ApiClientTest {

    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
    }

    @Test
    public void testApiClientNotNull() {
        assertNotNull("ApiClient ne devrait pas être null", apiClient);
    }

    @Test
    public void testBuildUrlWithEndpoint() {
        String endpoint = "/predict/cyber";
        String url = apiClient.buildUrl(endpoint);
        
        assertNotNull("L'URL ne devrait pas être null", url);
        assertTrue("L'URL devrait contenir l'endpoint", url.contains(endpoint));
        assertTrue("L'URL devrait commencer par http", url.startsWith("http"));
    }

    @Test
    public void testJsonObjectCreation() {
        try {
            JSONObject json = new JSONObject();
            json.put("username", "test");
            json.put("password", "test123");
            
            assertEquals("test", json.getString("username"));
            assertEquals("test123", json.getString("password"));
        } catch (Exception e) {
            fail("La création de JSONObject ne devrait pas échouer");
        }
    }

    @Test
    public void testTokenStorage() {
        String testToken = "test_token_123";
        apiClient.setToken(testToken);
        
        assertEquals("Le token devrait être stocké correctement", 
                     testToken, apiClient.getToken());
    }

    @Test
    public void testTokenClear() {
        apiClient.setToken("some_token");
        apiClient.clearToken();
        
        assertNull("Le token devrait être null après clear", 
                   apiClient.getToken());
    }
}
