package com.projets.ui;

import com.projets.util.JwtUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFrame extends JFrame {

    // User Token JWT
    private String jwtToken;

    // Couleurs du thème (mêmes que LoginFrame)
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private final Color LIGHT_BLUE = new Color(64, 169, 255);
    private final Color DARK_BLUE = new Color(12, 84, 157);
    private final Color TEXT_COLOR = new Color(33, 33, 33);
    private final Color PANEL_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(225, 232, 240);


    // Constructor with JWT Token
    public DashboardFrame(String jwtToken) {

        this.jwtToken = jwtToken;

        // Extraire le rôle et l'ID de l'utilisateur
        String role = JwtUtil.extractRole(jwtToken);
        String userId = JwtUtil.extractUserId(jwtToken);

        // Afficher le rôle et l'ID dans l'interface utilisateur
//        System.out.println("Rôle : " + role);
//        System.out.println("ID utilisateur : " + userId);

        setTitle("Tableau de bord - Système de Support IT");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panneau principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panneau d'en-tête
        JPanel headerPanel = createHeaderPanel();

        // Panneau de navigation latérale
        JPanel sidebarPanel = createSidebarPanel();

        // Panneau de contenu
        JPanel contentPanel = createContentPanel(role);

        // Ajouter les panneaux au panneau principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        setContentPane(mainPanel);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setMinimumSize(new Dimension(800, 600)); // Taille minimale
        setVisible(true);
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Système de Support IT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Utilisateur connecté | ");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);

        JLabel logoutLabel = new JLabel("Déconnexion");
        logoutLabel.setFont(new Font("Arial", Font.BOLD, 12));
        logoutLabel.setForeground(Color.WHITE);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setText("<html><u>Déconnexion</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutLabel.setText("Déconnexion");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        headerPanel,
                        "Êtes-vous sûr de vouloir vous déconnecter ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();  // Fermer la fenêtre actuelle
                    new LoginFrame();  // Réouvrir la fenêtre de connexion
                }
            }
        });

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(PRIMARY_BLUE);
        userPanel.add(userLabel);
        userPanel.add(logoutLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(PANEL_COLOR);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Arial", Font.BOLD, 14));
        navTitle.setForeground(TEXT_COLOR);
        navTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Créer les boutons de navigation
        JButton dashboardButton = createNavButton("Tableau de bord", true);
        JButton ticketsButton = createNavButton("Mes Tickets", false);
        JButton newTicketButton = createNavButton("Nouveau Ticket", false);
        JButton historyButton = createNavButton("Historique", false);
        JButton settingsButton = createNavButton("Paramètres", false);

        // Ajouter un peu d'espace entre les boutons
        sidebarPanel.add(navTitle);
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebarPanel.add(ticketsButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebarPanel.add(newTicketButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebarPanel.add(historyButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        sidebarPanel.add(settingsButton);

        // Ajouter un espace extensible en bas
        sidebarPanel.add(Box.createVerticalGlue());

        return sidebarPanel;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Ajouter de la marge à gauche du texte
        button.setMargin(new Insets(0, 15, 0, 0));

        if (active) {
            button.setBackground(LIGHT_BLUE);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(PANEL_COLOR);
            button.setForeground(TEXT_COLOR);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    button.setBackground(new Color(240, 245, 255));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    button.setBackground(PANEL_COLOR);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!active) {
                    button.setBackground(LIGHT_BLUE);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!active) {
                    button.setBackground(new Color(240, 245, 255));
                    button.setForeground(TEXT_COLOR);
                }
            }
        });

        return button;
    }

    private JPanel createContentPanel(String role) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel contentTitle = new JLabel("Vue d'ensemble");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        contentTitle.setForeground(TEXT_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.add(contentTitle, BorderLayout.WEST);

        // ✅ Réintégration du TicketTablePanel pour afficher les tickets
        TicketTablePanel ticketTablePanel = new TicketTablePanel(jwtToken);

        // Ajout du bouton seulement si l'utilisateur est un EMPLOYEE
        if ("EMPLOYEE".equalsIgnoreCase(role)) {
            JButton addTicketButton = new JButton("Ajouter un Ticket");
            addTicketButton.setFont(new Font("Arial", Font.BOLD, 14));
            addTicketButton.setBackground(new Color(25, 118, 210));
            addTicketButton.setForeground(Color.WHITE);
            addTicketButton.setFocusPainted(false);
            addTicketButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            addTicketButton.addActionListener(e -> {
                JDialog addTicketDialog = new JDialog();
                addTicketDialog.setTitle("Créer un Ticket");
                addTicketDialog.setSize(400, 350);
                addTicketDialog.setModal(true);
                addTicketDialog.setLocationRelativeTo(null);
                addTicketDialog.add(new AddTicketPanel(jwtToken, ticketTablePanel)); // Passer la référence
                addTicketDialog.setVisible(true);
            });

            topPanel.add(addTicketButton, BorderLayout.EAST);
        }


        // Panneau des tickets récents
        JPanel recentTicketsPanel = new JPanel(new BorderLayout());
        recentTicketsPanel.setBackground(PANEL_COLOR);
        recentTicketsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel recentTitle = new JLabel("Tickets récents");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 16));
        recentTitle.setForeground(TEXT_COLOR);
        recentTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        recentTicketsPanel.add(recentTitle, BorderLayout.NORTH);
        recentTicketsPanel.add(ticketTablePanel, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(recentTicketsPanel, BorderLayout.CENTER);

        return contentPanel;
    }




}