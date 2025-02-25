package com.projets.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer; // Importation manquante
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TicketTablePanel extends JPanel {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    private JTable table;
    private DefaultTableModel tableModel;
    private String jwtToken;

    // Couleurs et polices personnalisées
    private static final Color PANEL_COLOR = new Color(245, 247, 250);
    private static final Color HEADER_COLOR = new Color(25, 118, 210);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);


    public TicketTablePanel(String jwtToken) {
        this.jwtToken = jwtToken;
        setLayout(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marge autour du panneau

        // Définir les colonnes
        String[] columns = {"ID", "Titre", "Date", "Priority", "Category", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        // Appliquer le style au tableau
        styleTable();

        // Ajouter le tableau dans un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Supprimer la bordure du JScrollPane
        add(scrollPane, BorderLayout.CENTER);

        // Charger les tickets
        loadTickets();
    }


    private void styleTable() {
        // Personnalisation de l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Personnalisation des cellules
        table.setFont(TABLE_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(TEXT_COLOR);

        // Alternance des couleurs des lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Couleur de fond alternée pour les lignes
                if (row % 2 == 0) {
                    c.setBackground(new Color(250, 250, 250)); // Lignes paires
                } else {
                    c.setBackground(Color.WHITE); // Lignes impaires
                }

                // Centrer le texte dans les cellules
                setHorizontalAlignment(SwingConstants.CENTER);

                // Couleur du texte
                c.setForeground(TEXT_COLOR);

                return c;
            }
        });

        // Ajuster la largeur des colonnes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    private void loadTickets() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                URL url = new URL("http://localhost:8083/api/v1/tickets?page=0&size=10");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, Object> response = objectMapper.readValue(reader, new TypeReference<>() {
                });
                reader.close();

                return (List<Map<String, Object>>) response.get("content"); // Extraire la liste des tickets
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> tickets = get();
                    tableModel.setRowCount(0); // Effacer les données existantes
                    for (Map<String, Object> ticket : tickets) {
                        Object dateObj = ticket.get("creationDate");
                        String formattedDate = "";

                        if (dateObj instanceof String) {
                            formattedDate = LocalDateTime.parse((String) dateObj).format(formatter);
                        }

                        tableModel.addRow(new Object[]{
                                ticket.get("id"),
                                ticket.get("title"),
                                formattedDate,  // Date formatée
                                ticket.get("priority"),
                                ticket.get("category"),
                                ticket.get("status")
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TicketTablePanel.this, "Erreur lors du chargement des tickets", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    public void refreshTickets() {
        loadTickets();
    }

}