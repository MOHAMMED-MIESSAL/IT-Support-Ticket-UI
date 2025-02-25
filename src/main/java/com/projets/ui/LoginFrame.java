package com.projets.ui;


import com.projets.api.AuthService;
import com.projets.util.JwtUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Couleurs du thème
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_BLUE = new Color(34, 139, 34); // Vert principal
    private final Color LIGHT_BLUE =  new Color(50, 205, 50);   // Vert clair
    private final Color DARK_BLUE = new Color(0, 100, 0);      // Vert foncé
    private final Color TEXT_COLOR =  new Color(33, 33, 33);
    private final Color PANEL_COLOR = new Color(255, 255, 255);
    private final Color BORDER_COLOR = new Color(225, 232, 240);

    public LoginFrame() {
        setTitle("IT Support - Connexion");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panneau principal avec bordure
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panneau du titre
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Système de Support IT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_BLUE);
        titlePanel.add(titleLabel);

        // Panneau du formulaire
        JPanel formPanel = new JPanel();
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setLayout(new GridLayout(2, 1, 10, 15));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Panel pour le nom d'utilisateur
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 0));
        usernamePanel.setBackground(BACKGROUND_COLOR);
        JLabel usernameLabel = new JLabel("Nom d'utilisateur:");
        usernameLabel.setForeground(TEXT_COLOR);
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Panel pour le mot de passe
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.setBackground(BACKGROUND_COLOR);
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setForeground(TEXT_COLOR);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Ajout des panels au formulaire
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);

        // Panneau du bouton
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        loginButton = new JButton("Se connecter");
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Effet de survol sur le bouton
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(LIGHT_BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(PRIMARY_BLUE);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(DARK_BLUE);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(LIGHT_BLUE);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Veuillez remplir tous les champs",
                            "Erreur de saisie",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Afficher un indicateur de chargement
                loginButton.setText("Connexion en cours...");
                loginButton.setEnabled(false);

                // Utiliser SwingWorker pour ne pas bloquer l'interface utilisateur
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        // Appel au service d'authentification qui retourne le token
                        return AuthService.loginAndGetToken(username, password);
                    }

                    @Override
                    protected void done() {
                        try {
                            String token = get();

                            if (token != null && !token.isEmpty()) {
                                // Stockage du token
                                JwtUtil.setToken(token);

                                // Vérification des autorisations (si nécessaire)
                                if (validateUserRole(token)) {
                                    JOptionPane.showMessageDialog(
                                            LoginFrame.this,
                                            "Connexion réussie !",
                                            "Succès",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );
                                    dispose();
                                    new DashboardFrame(token); // Ouvre le tableau de bord après connexion
                                } else {
                                    JOptionPane.showMessageDialog(
                                            LoginFrame.this,
                                            "Vous n'avez pas les autorisations nécessaires",
                                            "Accès refusé",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                    loginButton.setText("Se connecter");
                                    loginButton.setEnabled(true);
                                }
                            } else {
                                JOptionPane.showMessageDialog(
                                        LoginFrame.this,
                                        "Identifiants incorrects. Veuillez réessayer.",
                                        "Échec de la connexion",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                loginButton.setText("Se connecter");
                                loginButton.setEnabled(true);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                    LoginFrame.this,
                                    "Erreur de connexion : " + ex.getMessage(),
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            loginButton.setText("Se connecter");
                            loginButton.setEnabled(true);
                        }
                    }
                };
                worker.execute();
            }
        });

        buttonPanel.add(loginButton);

        // Ajout des panneaux au panneau principal
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajout du panneau principal à la fenêtre
        setContentPane(mainPanel);

        setLocationRelativeTo(null); // Centrer la fenêtre
        setResizable(false); // Empêcher le redimensionnement
        setVisible(true);
    }

    /**
     * Valide le rôle utilisateur à partir du token JWT
     * Cette fonction pourrait être déplacée dans JwtUtil
     */
    private boolean validateUserRole(String token) {
        try {
            // Vous pourriez ajouter ici une logique pour vérifier les rôles/permissions
            // en décodant le token JWT
            return true; // Par défaut, on considère que tout utilisateur authentifié a accès
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}