import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;

public class MyFrane extends JFrame {

    JButton addButton;
    JButton removeButton;
    JButton refreshButton;
    boolean removeMode = false;

    JPanel gridPanel;
    JPanel bottomPanel;
    programHandler programHandler = new programHandler();

    // Track current mode
    String currentMode = "main";

    public MyFrane() {
        // ===== Frame =====
        setTitle("tweaksLauncher");
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
        setAlwaysOnTop(false);
        setFocusable(true);
        requestFocus();

        Color bg = new Color(20, 20, 20);
        getContentPane().setBackground(bg);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setBackground(new Color(30, 30, 30));

        ImageIcon originalIcon = new ImageIcon("icon.png");
        Image scaledIcon = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(scaledIcon));
        icon.setPreferredSize(new Dimension(40, 40));

        JLabel title = new JLabel("tweaksLAuncher");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(icon);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ===== Sidebar =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(25, 25, 25));
        sidebar.setPreferredSize(new Dimension(120, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton mainButton = createSidebarButton("Main");
        JButton steamButton = createSidebarButton("Steam");
        JButton epicButton = createSidebarButton("Epic");

        mainButton.addActionListener(e -> switchMode("main", mainButton, steamButton, epicButton));
        steamButton.addActionListener(e -> switchMode("steam", mainButton, steamButton, epicButton));
        epicButton.addActionListener(e -> switchMode("epic", mainButton, steamButton, epicButton));

        sidebar.add(mainButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(steamButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(epicButton);
        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // ===== Grid Panel =====
        gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(bg);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bg);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Bottom Controls =====
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(bg);

        // Shared control buttons for both modes
        addButton = createControlButton("+");
        removeButton = createControlButton("–");
        refreshButton = createControlButton("↻");

        bottomPanel.add(removeButton);
        bottomPanel.add(addButton);
        bottomPanel.add(refreshButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load apps
        createAppButtons();

        setVisible(true);
    }

    // ===== Sidebar Buttons =====
    JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(45, 45, 45));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        return btn;
    }

    // ===== Switch Mode =====
    void switchMode(String mode, JButton mainBtn, JButton steamBtn, JButton epicBtn) {
        currentMode = mode;

        // Reset all buttons to default color
        mainBtn.setBackground(new Color(45, 45, 45));
        steamBtn.setBackground(new Color(45, 45, 45));
        epicBtn.setBackground(new Color(45, 45, 45));

        // Update button colors and load appropriate view
        if (mode.equals("main")) {
            mainBtn.setBackground(new Color(70, 120, 180));
            createAppButtons();
        } else if (mode.equals("steam")) {
            steamBtn.setBackground(new Color(70, 120, 180));
            createSteamButtons();
        } else if (mode.equals("epic")) {
            epicBtn.setBackground(new Color(70, 120, 180));
            createEpicButtons();
        }
    }

    // ===== Square App Buttons (Main Mode) =====
    void createAppButtons() {
        gridPanel.removeAll();
        String[] names = programHandler.allSaved();

        for (String name : names) {
            String path = programHandler.findPath(name);
            // Filter out Steam games (start with digit) and Epic games (start with 'e:')
            if (!((path.charAt(0) <= '9' && path.charAt(0) >= '0') || path.startsWith("e:"))) {
                JButton app = new JButton("<html><center>" + name + "</center></html>");
                app.setPreferredSize(new Dimension(180, 180));
                app.setBackground(new Color(45, 45, 45));
                app.setForeground(Color.WHITE);
                app.setFocusPainted(false);
                app.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
                app.setFont(new Font("Segoe UI", Font.BOLD, 14));

                app.addActionListener(e -> {
                    if (removeMode) {
                        programHandler.removeProgram(name);
                        createAppButtons();
                    } else {
                        programHandler.openProgram(programHandler.findPath(name));
                    }
                });

                gridPanel.add(app);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // ===== Steam App Buttons (Steam Mode) =====
    void createSteamButtons() {
        gridPanel.removeAll();
        String[] names = programHandler.allSaved();

        for (String name : names) {
            String path = programHandler.findPath(name);
            if (path.charAt(0) <= '9' && path.charAt(0) >= '0') {
                // Extract the app ID from the path
                String appId = path.split(":")[0];
                String imagePath = appId + ".png";

                // Create custom panel for hover effect
                SteamGamePanel gamePanel = new SteamGamePanel(name, imagePath);
                gamePanel.setPreferredSize(new Dimension(180, 180));

                gamePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (removeMode) {
                            programHandler.removeProgram(name);
                            createSteamButtons();
                        } else {
                            programHandler.openProgram(programHandler.findPath(name));
                        }
                    }
                });

                gridPanel.add(gamePanel);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // ===== Epic Games Buttons (Epic Mode) =====
    void createEpicButtons() {
        gridPanel.removeAll();
        String[] names = programHandler.allSaved();

        for (String name : names) {
            String path = programHandler.findPath(name);
            if (path.contains("aUniquePathForLegendaryGames")) {
                EpicGamePanel gamePanel = getEpicGamePanel(name);
                gridPanel.add(gamePanel);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private EpicGamePanel getEpicGamePanel(String name) {
        EpicGamePanel gamePanel = new EpicGamePanel(name);
        gamePanel.setPreferredSize(new Dimension(180, 180));

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (removeMode) {
                    programHandler.removeProgram(name);
                    createEpicButtons();
                } else {
                    programHandler.openProgram(programHandler.findPath(name));
                }
            }
        });
        return gamePanel;
    }

    // ===== Custom Panel for Steam Games with Hover Effect =====
    class SteamGamePanel extends JPanel {
        private String gameName;
        private BufferedImage coverImage;
        private boolean isHovered = false;

        public SteamGamePanel(String gameName, String imagePath) {
            this.gameName = gameName;
            setLayout(null);
            setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

            // Load cover image
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    coverImage = ImageIO.read(imageFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Add hover listener
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            // Draw cover image or fallback background
            if (coverImage != null) {
                g2d.drawImage(coverImage, 0, 0, w, h, null);
            } else {
                g2d.setColor(new Color(45, 45, 45));
                g2d.fillRect(0, 0, w, h);
            }

            // Draw hover effect with blurred gradient and text
            if (isHovered) {
                // Create gradient overlay from bottom
                int gradientHeight = 80;
                GradientPaint gradient = new GradientPaint(
                        0, h - gradientHeight, new Color(0, 0, 0, 200),
                        0, h - gradientHeight + 40, new Color(0, 0, 0, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, h - gradientHeight, w, gradientHeight);

                // Draw game name
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();

                // Word wrap the text if needed
                String[] words = gameName.split(" ");
                StringBuilder line = new StringBuilder();
                int y = h - 25;

                for (String word : words) {
                    String testLine = line.length() == 0 ? word : line + " " + word;
                    if (fm.stringWidth(testLine) > w - 20) {
                        if (line.length() > 0) {
                            int x = (w - fm.stringWidth(line.toString())) / 2;
                            g2d.drawString(line.toString(), x, y);
                            y += fm.getHeight();
                            line = new StringBuilder(word);
                        } else {
                            int x = (w - fm.stringWidth(word)) / 2;
                            g2d.drawString(word, x, y);
                            y += fm.getHeight();
                        }
                    } else {
                        line = new StringBuilder(testLine);
                    }
                }

                if (line.length() > 0) {
                    int x = (w - fm.stringWidth(line.toString())) / 2;
                    g2d.drawString(line.toString(), x, y);
                }
            }
        }
    }

    // ===== Custom Panel for Epic Games with Hover Effect =====
    class EpicGamePanel extends JPanel {
        private String gameName;
        private BufferedImage coverImage;
        private boolean isHovered = false;

        public EpicGamePanel(String gameName) {
            this.gameName = gameName;
            setLayout(null);
            setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

            // Add hover listener
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            // Draw cover image or fallback background
            if (coverImage != null) {
                g2d.drawImage(coverImage, 0, 0, w, h, null);
            } else {
                g2d.setColor(new Color(45, 45, 45));
                g2d.fillRect(0, 0, w, h);
            }

            // Draw hover effect with blurred gradient and text
            if (isHovered) {
                // Create gradient overlay from bottom
                int gradientHeight = 80;
                GradientPaint gradient = new GradientPaint(
                        0, h - gradientHeight, new Color(0, 0, 0, 200),
                        0, h - gradientHeight + 40, new Color(0, 0, 0, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, h - gradientHeight, w, gradientHeight);

                // Draw game name
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();

                // Word wrap the text if needed
                String[] words = gameName.split(" ");
                StringBuilder line = new StringBuilder();
                int y = h - 25;

                for (String word : words) {
                    String testLine = line.length() == 0 ? word : line + " " + word;
                    if (fm.stringWidth(testLine) > w - 20) {
                        if (line.length() > 0) {
                            int x = (w - fm.stringWidth(line.toString())) / 2;
                            g2d.drawString(line.toString(), x, y);
                            y += fm.getHeight();
                            line = new StringBuilder(word);
                        } else {
                            int x = (w - fm.stringWidth(word)) / 2;
                            g2d.drawString(word, x, y);
                            y += fm.getHeight();
                        }
                    } else {
                        line = new StringBuilder(testLine);
                    }
                }

                if (line.length() > 0) {
                    int x = (w - fm.stringWidth(line.toString())) / 2;
                    g2d.drawString(line.toString(), x, y);
                }
            }
        }
    }

    // ===== Control Buttons =====
    JButton createControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("DejaVu Sans", Font.PLAIN, 24));
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(60, 60));
        btn.addActionListener(this::handleControls);
        return btn;
    }

    void handleControls(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                if (currentMode.equals("main")) {
                    programHandler.addProgram();
                    createAppButtons();
                } else if(currentMode.equals("steam")) {
                    int save = JOptionPane.showConfirmDialog(null,"would you like to add steam games automatically");
                    if (save == JOptionPane.YES_OPTION) {
                        try {
                            programHandler.addSteamGames();
                            createAppButtons();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else {
                        save = JOptionPane.showConfirmDialog(null,"would you like to add steam games manually?");
                        if (save == JOptionPane.YES_OPTION) {
                            try {
                                programHandler.addSteamApp();
                                createAppButtons();
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    createSteamButtons();
                } else if(currentMode.equals("epic")) {
                    int save = JOptionPane.showConfirmDialog(null,"would you like to add epic games automatically?");
                    if (save == JOptionPane.YES_OPTION) {
                        try {
                            programHandler.allLegendaryGames();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    createEpicButtons();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == removeButton) {
            removeMode = !removeMode;
            removeButton.setBackground(removeMode ? new Color(120, 50, 50) : new Color(60, 60, 60));
        } else if (e.getSource() == refreshButton) {
            if (currentMode.equals("main")) {
                createAppButtons();
            } else if (currentMode.equals("steam")) {
                createSteamButtons();
            } else if (currentMode.equals("epic")) {
                createEpicButtons();
            }
        }
    }
}