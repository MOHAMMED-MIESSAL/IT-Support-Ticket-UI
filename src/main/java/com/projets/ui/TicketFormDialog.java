package com.projets.ui;

import javax.swing.*;


public class TicketFormDialog extends JDialog {
    public TicketFormDialog() {
        setTitle("Nouveau Ticket");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Formulaire de création de ticket"));
        add(panel);

        setVisible(true);
    }
}