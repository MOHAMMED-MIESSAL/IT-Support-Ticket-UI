package com.projets.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projets.dto.CommentCreateDto;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class CommentService {

    public static void addComment(CommentCreateDto commentCreateDto , String jwtToken) throws Exception {
        URL url = new URL("http://localhost:8083/api/v1/comments");  // URL de ton backend
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");  // Utiliser POST
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
        conn.setDoOutput(true);
        // Crée un objet ObjectMapper pour convertir l'objet en JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(commentCreateDto);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
            os.flush();
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
            throw new RuntimeException("Erreur HTTP : " + conn.getResponseCode());
        } else {
            System.out.println("Commentaire ajouté avec succès.");
        }
    }

}
