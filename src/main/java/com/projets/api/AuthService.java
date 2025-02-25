package com.projets.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projets.util.JwtUtil;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;

public class AuthService {
    private static final String API_URL = "http://localhost:8083/api/v1/auth/login";

    /**
     * Méthode pour se connecter et récupérer directement le token
     * @param email Nom d'utilisateur
     * @param password Mot de passe
     * @return Le token JWT si l'authentification réussit, sinon null
     */
    public static String loginAndGetToken(String email, String password) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");

            // Corps de la requête JSON
            String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());

                    // Extraire le token de la réponse
                    String token = jsonNode.get("token").asText();

                    // On retourne directement le token
                    return token;
                } else {
                    System.out.println("Échec de l'authentification : " + response.getCode());
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode existante conservée pour compatibilité
     */
    public static boolean login(String email, String password) {
        String token = loginAndGetToken(email, password);
        if (token != null && !token.isEmpty()) {
            JwtUtil.setToken(token);
            return true;
        }
        return false;
    }
}


//    public static boolean login(String email, String password) {
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            HttpPost request = new HttpPost(API_URL);
//            request.setHeader("Content-Type", "application/json");
//
//            // Corps de la requête JSON
//            String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
//            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
//
//            try (CloseableHttpResponse response = client.execute(request)) {
//                if (response.getCode() == 200) {
//                    // Lire la réponse JSON
//                    StringBuilder responseBody = new StringBuilder();
//                    try (var scanner = new Scanner(response.getEntity().getContent())) {
//                        while (scanner.hasNextLine()) {
//                            responseBody.append(scanner.nextLine());
//                        }
//                    }
//
//                    // Convertir la réponse JSON en objet
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
//
//                    // Extraire le token JWT
//                    String token = jsonNode.get("token").asText();
//
//                    // Stocker le token dans JwtUtil
//                    JwtUtil.setToken(token);
//
//                    return true; // Connexion réussie
//                } else {
//                    System.out.println("Échec de l'authentification : " + response.getCode());
//                    return false; // Connexion échouée
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false; // Connexion échouée
//        }
//    }


