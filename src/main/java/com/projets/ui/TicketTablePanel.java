package com.projets.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projets.api.TicketService;
import com.projets.util.JwtUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private TicketService ticketService;

    // Couleurs et polices personnalisées
    private static final Color PANEL_COLOR = new Color(245, 247, 250);
    private static final Color HEADER_COLOR = new Color(25, 118, 210);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);


    public TicketTablePanel(String jwtToken) {
        this.jwtToken = jwtToken;
        this.ticketService = new TicketService();
        setLayout(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marge autour du panneau

        // Vérifier le rôle de l'utilisateur à partir du token JWT
        String[] columns = getColumnsForUserRole();
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
        if ("IT_Support".equalsIgnoreCase(JwtUtil.extractRole(jwtToken))) {
            addUpdateStatusButtons();
        }
    }

    private String[] getColumnsForUserRole() {
        // Extraire le rôle de l'utilisateur depuis le JWT
        String role = JwtUtil.extractRole(jwtToken);

        // Définir les colonnes en fonction du rôle
        if ("IT_Support".equalsIgnoreCase(role)) {
            return new String[]{"ID", "Titre", "Date", "Priority", "Category", "Status", "Update Status"};
        } else {
            return new String[]{"ID", "Titre", "Date", "Priority", "Category", "Status"};
        }
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

    private void addUpdateStatusButtons() {
        // Ajouter un renderer et un éditeur pour afficher les boutons
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn updateStatusColumn = columnModel.getColumn(6); // Colonne "Update Status"

        // Utiliser un ButtonRenderer pour afficher le bouton
        updateStatusColumn.setCellRenderer(new ButtonRenderer());

        // Passer la référence de TicketService à ButtonEditor
        updateStatusColumn.setCellEditor(new ButtonEditor(new JCheckBox(), ticketService));
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Update Status");
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private TicketService ticketService;  // Ajouter une variable pour ticketService
        private String label;
        private JButton button;
        private String ticketId;

        // Constructeur modifié pour accepter ticketService
        public ButtonEditor(JCheckBox checkBox, TicketService ticketService) {
            super(checkBox);
            this.ticketService = ticketService;  // Initialisation de ticketService
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Récupérer l'ID du ticket à partir de la ligne sélectionnée
                    int row = table.getSelectedRow();
                    ticketId = table.getValueAt(row, 0).toString();

                    // Afficher un formulaire pour modifier le statut du ticket
                    showUpdateStatusForm(ticketId);
                    fireEditingStopped(); // Pour arrêter l'édition du bouton
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        private void showUpdateStatusForm(String ticketId) {
            // Créer une boîte de dialogue pour changer le statut
            JDialog dialog = new JDialog((Frame) null, "Update Ticket Status", true);
            dialog.setLayout(new GridLayout(3, 2));

            JLabel statusLabel = new JLabel("New Status:");
            String[] statusOptions = {"IN_PROGRESS", "RESOLVED"};
            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);

            JButton updateButton = new JButton("Update");
            JButton cancelButton = new JButton("Cancel");

            // Action pour le bouton "Update"
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newStatus = (String) statusComboBox.getSelectedItem();
                    String userId = JwtUtil.extractUserId(jwtToken);

                    // Mettre à jour le statut du ticket
                    if (ticketService != null) {
                        try {
                            ticketService.updateTicketStatus(ticketId, newStatus, userId, jwtToken);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        refreshTickets(); // Rafraîchir le tableau après la mise à jour
                    } else {
                        JOptionPane.showMessageDialog(null, "Ticket service is not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    dialog.dispose(); // Fermer la boîte de dialogue après mise à jour
                }
            });

            // Action pour le bouton "Cancel"
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose(); // Fermer la boîte de dialogue sans changer le statut
                }
            });

            // Ajouter les composants dans le dialogue
            dialog.add(statusLabel);
            dialog.add(statusComboBox);
            dialog.add(updateButton);
            dialog.add(cancelButton);

            // Configurer la taille de la boîte de dialogue
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(null); // Centrer la boîte de dialogue
            dialog.setVisible(true);
        }

    }


}