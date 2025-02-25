package com.projets.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projets.dto.TicketCreateDto;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketService {
    private static final String TICKET_API_URL = "http://localhost:8083/api/v1/tickets";

    public static void createTicket(TicketCreateDto ticket, String jwtToken) throws Exception {
        URL url = new URL(TICKET_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
        conn.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(ticket);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
            throw new RuntimeException("Erreur HTTP : " + conn.getResponseCode());
        }
    }

    public static void updateTicketStatus(String ticketId, String newStatus, String userId, String jwtToken) throws Exception {
        // URL de l'API
        URL url = new URL("http://localhost:8083/api/v1/tickets/status/" + ticketId );

        // Ouverture de la connexion HTTP
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");  // Utiliser POST
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
        conn.setDoOutput(true);

        // Créer l'objet de la requête
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("status", newStatus);
        requestBody.put("userId", userId);

        // Convertir l'objet en JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestBody);

        // Envoyer le corps de la requête
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        // Vérifier la réponse du serveur
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Erreur HTTP : " + conn.getResponseCode());
        } else {
            // Optionnel: Traiter la réponse si nécessaire
            try (InputStreamReader in = new InputStreamReader(conn.getInputStream());
                 BufferedReader reader = new BufferedReader(in)) {
                String response = reader.lines().collect(Collectors.joining("\n"));
                System.out.println("Réponse serveur : " + response);
            }
        }
    }



}
