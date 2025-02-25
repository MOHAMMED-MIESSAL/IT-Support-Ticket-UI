package com.projets.ui;

import com.projets.api.TicketService;
import com.projets.dto.TicketCreateDto;
import com.projets.util.JwtUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTicketPanel extends JPanel {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityBox;
    private JComboBox<String> categoryBox;
    private JButton submitButton;
    private String jwtToken;
    private TicketTablePanel ticketTablePanel;

    public AddTicketPanel(String jwtToken, TicketTablePanel ticketTablePanel) {
        this.jwtToken = jwtToken;
        this.ticketTablePanel = ticketTablePanel;

        // Configuration du layout principal
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre du formulaire
        JLabel titleLabel = new JLabel("Créer un Nouveau Ticket");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panneau du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Paramètres de contrainte de GridBag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Champ Titre
        formPanel.add(new JLabel("Titre :"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Champ Description
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Description :"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Champ Priorité
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Priorité :"), gbc);
        gbc.gridx = 1;
        priorityBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        formPanel.add(priorityBox, gbc);

        // Champ Catégorie
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1;
        categoryBox = new JComboBox<>(new String[]{"HARDWARE", "SOFTWARE", "NETWORK"});
        formPanel.add(categoryBox, gbc);

        // Bouton de soumission
        gbc.gridx = 1;
        gbc.gridy++;
        submitButton = new JButton("Soumettre");
        submitButton.setBackground(new Color(25, 118, 210));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(new SubmitAction());
        formPanel.add(submitButton, gbc);

        // Ajouter les composants au panel principal
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

    }

    // Classe pour gérer l'action du bouton de soumission
    private class SubmitAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();
            String userId = JwtUtil.extractUserId(jwtToken);

            if (title.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TicketCreateDto ticket = new TicketCreateDto(title, description, priority, category, userId);

            try {
                TicketService.createTicket(ticket, jwtToken);  // Envoi du ticket au backend
                JOptionPane.showMessageDialog(null, "Ticket ajouté avec succès !");

                // Rafraîchir le tableau des tickets
                if (ticketTablePanel != null) {
                    ticketTablePanel.refreshTickets();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la création du ticket.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

