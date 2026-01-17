import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;

public class MyFrane extends JFrame {

    JButton addButton, removeButton, refreshButton;
    JButton steamStoreBtn, steamMarketBtn, epicStoreBtn;
    boolean removeMode = false;
    JPanel gridPanel, bottomPanel, headerPanel;
    programHandler programHandler = new programHandler();
    String currentMode = "main";

    private final Color SPACE_BLACK = new Color(10, 10, 22);
    private final Color NEBULA_PURPLE = new Color(110, 80, 230);
    private final Color STAR_WHITE = new Color(230, 230, 255);
    private final Color GLASS_BG = new Color(255, 255, 255, 15);

    private ArrayList<Particle> particles = new ArrayList<>();
    private Timer animationTimer;
    private VolatileImage vImg;

    public MyFrane() {
        System.setProperty("sun.java2d.opengl", "true");
        setupGlobalUI();

        setTitle("tweaksLauncher");
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Hardware Accelerated Background
        JPanel mainContent = new JPanel(new BorderLayout()) {
            {
                Random rand = new Random();
                for (int i = 0; i < 80; i++) {
                    particles.add(new Particle(rand.nextInt(2560), rand.nextInt(1440)));
                }
                animationTimer = new Timer(30, e -> {
                    for (Particle p : particles) p.move(getWidth(), getHeight());
                    repaint();
                });
                animationTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                do {
                    int returnCode = (vImg == null) ? VolatileImage.IMAGE_INCOMPATIBLE : vImg.validate(getGraphicsConfiguration());
                    if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE || returnCode == VolatileImage.IMAGE_RESTORED) {
                        vImg = createVolatileImage(getWidth(), getHeight());
                    }
                    Graphics2D gv = vImg.createGraphics();
                    gv.setColor(SPACE_BLACK);
                    gv.fillRect(0, 0, getWidth(), getHeight());
                    gv.setColor(new Color(255, 255, 255, 180));
                    for (Particle p : particles) { gv.fillRect((int)p.x, (int)p.y, p.size, p.size); }
                    gv.dispose();
                    g.drawImage(vImg, 0, 0, this);
                } while (vImg.contentsLost());
            }
        };
        setContentPane(mainContent);

        // Header
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("tweaksLauncher");
        title.setForeground(STAR_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(title);

        // Search bar
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(new Color(40, 40, 80, 150));
        searchField.setForeground(STAR_WHITE);
        searchField.setCaretColor(STAR_WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEBULA_PURPLE, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                if (searchText.isEmpty()) {
                    if (currentMode.equals("main")) createAppButtons();
                    else if (currentMode.equals("steam")) createSteamButtons();
                    else if (currentMode.equals("epic")) createEpicButtons();
                } else {
                    if (currentMode.equals("main")) createAppSearchButtons(searchText);
                    else if (currentMode.equals("steam")) createSteamSearchButtons(searchText);
                    else if (currentMode.equals("epic")) createEpicSearchButtons(searchText);
                }
            }
        });
        headerPanel.add(searchField);
        steamStoreBtn = createHeaderButton("Store");
        steamStoreBtn.addActionListener(e -> openURL("https://store.steampowered.com"));
        steamStoreBtn.setVisible(false);
        headerPanel.add(steamStoreBtn);

        steamMarketBtn = createHeaderButton("Market");
        steamMarketBtn.addActionListener(e -> openURL("https://steamcommunity.com/market"));
        steamMarketBtn.setVisible(false);
        headerPanel.add(steamMarketBtn);

        epicStoreBtn = createHeaderButton("Store");
        epicStoreBtn.addActionListener(e -> openURL("https://store.epicgames.com"));
        epicStoreBtn.setVisible(false);
        headerPanel.add(epicStoreBtn);

        add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(160, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JButton mainBtn = createSidebarButton("Main");
        JButton steamBtn = createSidebarButton("Steam");
        JButton epicBtn = createSidebarButton("Epic");

        mainBtn.addActionListener(e -> switchMode("main", mainBtn, steamBtn, epicBtn));
        steamBtn.addActionListener(e -> switchMode("steam", mainBtn, steamBtn, epicBtn));
        epicBtn.addActionListener(e -> switchMode("epic", mainBtn, steamBtn, epicBtn));

        sidebar.add(mainBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(steamBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(epicBtn);
        add(sidebar, BorderLayout.WEST);

        // GRID PANEL FIX: Uses a container that respects preferred sizes
        gridPanel = new JPanel(new GridLayout(0, 4, 25, 25));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Use a wrapper panel to prevent GridLayout from stretching items vertically
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = NEBULA_PURPLE;
                this.trackColor = new Color(0,0,0,0);
            }
            @Override protected JButton createDecreaseButton(int r) { return new JButton() {{setPreferredSize(new Dimension(0,0));}}; }
            @Override protected JButton createIncreaseButton(int r) { return new JButton() {{setPreferredSize(new Dimension(0,0));}}; }
        });
        add(scrollPane, BorderLayout.CENTER);

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        bottomPanel.setOpaque(false);
        addButton = createControlButton("+");
        removeButton = createControlButton("–");
        refreshButton = createControlButton("↻");
        bottomPanel.add(removeButton);
        bottomPanel.add(addButton);
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        createAppButtons();
        setVisible(true);
    }

    private void setupGlobalUI() {
        UIManager.put("OptionPane.background", SPACE_BLACK);
        UIManager.put("Panel.background", SPACE_BLACK);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", NEBULA_PURPLE);
        UIManager.put("Button.foreground", Color.WHITE);
    }

    JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(NEBULA_PURPLE);
        btn.setForeground(STAR_WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 120, 255), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(GLASS_BG);
        btn.setForeground(STAR_WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(130, 45));
        btn.setBorder(BorderFactory.createLineBorder(NEBULA_PURPLE, 1));
        return btn;
    }

    JButton createControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(GLASS_BG);
        btn.setForeground(STAR_WHITE);
        btn.setPreferredSize(new Dimension(55, 55));
        btn.setBorder(BorderFactory.createLineBorder(NEBULA_PURPLE, 1));
        btn.addActionListener(this::handleControls);
        return btn;
    }

    void switchMode(String mode, JButton m, JButton s, JButton e) {
        currentMode = mode;
        m.setBackground(GLASS_BG); s.setBackground(GLASS_BG); e.setBackground(GLASS_BG);
        steamStoreBtn.setVisible(false);
        steamMarketBtn.setVisible(false);
        epicStoreBtn.setVisible(false);

        if (mode.equals("main")) {
            m.setBackground(NEBULA_PURPLE);
            createAppButtons();
        }
        else if (mode.equals("steam")) {
            s.setBackground(NEBULA_PURPLE);
            steamStoreBtn.setVisible(true);
            steamMarketBtn.setVisible(true);
            createSteamButtons();
        }
        else if (mode.equals("epic")) {
            e.setBackground(NEBULA_PURPLE);
            epicStoreBtn.setVisible(true);
            createEpicButtons();
        }

        headerPanel.revalidate();
        headerPanel.repaint();
    }

    void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not open browser: " + ex.getMessage());
        }
    }

    void createAppButtons() {
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (!((path.charAt(0) <= '9' && path.charAt(0) >= '0') || path.startsWith("e:") || path.contains("aUniquePathForLegendaryGames"))) {
                JButton app = new JButton("<html><center>" + name + "</center></html>");
                app.setPreferredSize(new Dimension(200, 200));
                app.setBackground(new Color(40, 40, 80, 120));
                app.setForeground(Color.WHITE);
                app.setBorder(BorderFactory.createLineBorder(NEBULA_PURPLE));
                app.setFocusPainted(false);
                app.addActionListener(e -> {
                    if (removeMode) { programHandler.removeProgram(name); createAppButtons(); }
                    else programHandler.openProgram(path);
                });
                gridPanel.add(app);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    void createAppSearchButtons(String text) {
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (name.toLowerCase().contains(text.toLowerCase())&&!((path.charAt(0) <= '9' && path.charAt(0) >= '0') || path.startsWith("e:") || path.contains("aUniquePathForLegendaryGames"))) {
                JButton app = new JButton("<html><center>" + name + "</center></html>");
                app.setPreferredSize(new Dimension(200, 200));
                app.setBackground(new Color(40, 40, 80, 120));
                app.setForeground(Color.WHITE);
                app.setBorder(BorderFactory.createLineBorder(NEBULA_PURPLE));
                app.setFocusPainted(false);
                app.addActionListener(e -> {
                    if (removeMode) { programHandler.removeProgram(name); createAppButtons(); }
                    else programHandler.openProgram(path);
                });
                gridPanel.add(app);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }


    void createSteamButtons() {
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (path.charAt(0) <= '9' && path.charAt(0) >= '0') {
                SteamGamePanel p = new SteamGamePanel(name, path.split(":")[0] + ".png");
                p.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (removeMode) { programHandler.removeProgram(name); createSteamButtons(); }
                        else programHandler.openProgram(path);
                    }
                });
                gridPanel.add(p);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    void createSteamSearchButtons(String text) {
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (path.charAt(0) <= '9' && path.charAt(0) >= '0' && name.toLowerCase().contains(text.toLowerCase())) {
                SteamGamePanel p = new SteamGamePanel(name, path.split(":")[0] + ".png");
                p.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (removeMode) { programHandler.removeProgram(name); createSteamButtons(); }
                        else programHandler.openProgram(path);
                    }
                });
                gridPanel.add(p);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    void createEpicButtons() {
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (path.contains("aUniquePathForLegendaryGames")) {
                EpicGamePanel p = new EpicGamePanel(name);
                p.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (removeMode) { programHandler.removeProgram(name); createEpicButtons(); }
                        else programHandler.openProgram(path);
                    }
                });
                gridPanel.add(p);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    void createEpicSearchButtons(String text){
        gridPanel.removeAll();
        for (String name : programHandler.allSaved()) {
            String path = programHandler.findPath(name);
            if (path.contains("aUniquePathForLegendaryGames") && name.toLowerCase().contains(text.toLowerCase())) {
                EpicGamePanel p = new EpicGamePanel(name);
                p.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (removeMode) { programHandler.removeProgram(name); createEpicButtons(); }
                        else programHandler.openProgram(path);
                    }
                });
                gridPanel.add(p);
            }
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    class SteamGamePanel extends JPanel {
        private String name; private BufferedImage img; private boolean hover = false;
        public SteamGamePanel(String n, String path) {
            this.name = n; setOpaque(false);
            setPreferredSize(new Dimension(200, 200));
            try {
                File f = new File(path);
                if(f.exists()) {
                    BufferedImage raw = ImageIO.read(f);
                    img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = img.createGraphics();
                    g.drawImage(raw, 0, 0, 200, 200, null);
                    g.dispose();
                }
            } catch (Exception e) {}
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            if (img != null) g2.drawImage(img, 0, 0, w, h, null);
            else { g2.setColor(GLASS_BG); g2.fillRect(0,0,w,h); }
            if (hover) {
                g2.setPaint(new GradientPaint(0, h*0.6f, new Color(0,0,0,0), 0, h, new Color(0,0,0,200)));
                g2.fillRect(0,0,w,h);
                g2.setColor(Color.WHITE);
                g2.drawString(name, 10, h - 15);
            }
            g2.setColor(hover ? NEBULA_PURPLE : new Color(255,255,255,40));
            g2.drawRect(0,0,w-1,h-1);
        }
    }

    class EpicGamePanel extends JPanel {
        private String name; private boolean hover = false;
        public EpicGamePanel(String n) {
            this.name = n; setOpaque(false);
            setPreferredSize(new Dimension(200, 200));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            g.setColor(hover ? NEBULA_PURPLE : GLASS_BG);
            g.fillRect(0,0,w,h);
            g.setColor(Color.WHITE);
            g.drawString(name, 15, h/2);
            g.drawRect(0,0,w-1,h-1);
        }
    }

    void handleControls(ActionEvent e) {
        try {
            if (e.getSource() == addButton) {
                if (currentMode.equals("main")) { programHandler.addProgram(); createAppButtons(); }
                else if (currentMode.equals("steam")) {

                    int choice = JOptionPane.showConfirmDialog(this, "Auto-import Steam games?");
                    if (choice == JOptionPane.YES_OPTION) programHandler.addSteamGames();
                    createSteamButtons();
                } else if (currentMode.equals("epic")) {
                    int choice = JOptionPane.showConfirmDialog(this, "Auto-import Epic games?");
                    if (choice == JOptionPane.YES_OPTION) programHandler.allLegendaryGames();
                    createEpicButtons();
                }
            } else if (e.getSource() == removeButton) {
                removeMode = !removeMode;
                removeButton.setBackground(removeMode ? new Color(200, 50, 50) : GLASS_BG);
            } else if (e.getSource() == refreshButton) {
                if (currentMode.equals("main")) createAppButtons();
                else if (currentMode.equals("steam")) createSteamButtons();
                else createEpicButtons();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private class Particle {
        double x, y, sx, sy; int size;
        Particle(int x, int y) {
            this.x = x; this.y = y; Random r = new Random();
            this.sx = (r.nextDouble() - 0.5) * 1.1;
            this.sy = (r.nextDouble() - 0.5) * 1.1;
            this.size = r.nextInt(2) + 1;
        }
        void move(int w, int h) {
            x += sx; y += sy;
            if (x < 0) x = w; if (x > w) x = 0;
            if (y < 0) y = h; if (y > h) y = 0;
        }
    }
}