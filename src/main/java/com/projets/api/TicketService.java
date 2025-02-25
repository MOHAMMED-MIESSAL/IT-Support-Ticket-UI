package com.projets.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projets.dto.TicketCreateDto;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

}
