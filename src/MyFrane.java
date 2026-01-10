import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MyFrane extends JFrame {

    JButton addButton;
    JButton removeButton;
    JButton refreshButton;
    boolean removeMode = false;

    JPanel gridPanel;
    programHandler programHandler = new programHandler();

    public MyFrane() {
        // ===== Frame =====
        setTitle("Launcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // windowed fullscreen
        setAlwaysOnTop(false);
        setFocusable(true);
        requestFocus();

        Color bg = new Color(20, 20, 20);
        getContentPane().setBackground(bg);

        // ===== Header (Heroic-style) =====
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
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setBackground(bg);

        addButton = createControlButton("+");
        removeButton = createControlButton("–");
        refreshButton = createControlButton("↻");

        bottom.add(removeButton);
        bottom.add(addButton);
        bottom.add(refreshButton);

        add(bottom, BorderLayout.SOUTH);

        // Load apps
        createAppButtons();

        setVisible(true);
    }

    // ===== Square App Buttons =====
    void createAppButtons() {
        gridPanel.removeAll();
        String[] names = programHandler.allSaved();

        for (String name : names) {
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

        gridPanel.revalidate();
        gridPanel.repaint();
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
                programHandler.addProgram();
                createAppButtons();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == removeButton) {
            removeMode = !removeMode;
            removeButton.setBackground(removeMode ? new Color(120, 50, 50) : new Color(60, 60, 60));
        }
        else if(e.getSource() == refreshButton){
            createAppButtons();
        }
    }
}
