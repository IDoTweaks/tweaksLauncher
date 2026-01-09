import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MyFrane extends JFrame implements ActionListener {
    JButton addButton;
    JPanel appsPanel = new JPanel();
    private programHandler programHandler = new programHandler();
    MyFrane() {
        // Frame setup
        this.setSize(600, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.DARK_GRAY);
        this.setLayout(new BorderLayout()); // easier for fixed + scrollable buttons

        // Fixed "+" button at the bottom
        addButton = new JButton("+");
        addButton.setFont(new Font("Arial", Font.BOLD, 24));
        addButton.setBackground(Color.GRAY);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        addButton.addActionListener(this);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.add(addButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Scrollable panel for dynamic buttons
        appsPanel.setLayout(new BoxLayout(appsPanel, BoxLayout.Y_AXIS));
        appsPanel.setBackground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(appsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, BorderLayout.CENTER);

        // Initially create buttons from saved programs
        createAppButtons();

        this.setVisible(true);
    }

    public void createAppButtons(){
        String[] names = programHandler.allSaved();
        appsPanel.removeAll(); // clear existing buttons first

        for (String name : names) {
            JButton appButton = new JButton(name);
            appButton.setBackground(Color.GRAY);
            appButton.setForeground(Color.WHITE);
            appButton.setFocusPainted(false);
            appButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // full width
            appButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Optional: add action listener
            appButton.addActionListener(e -> {
                String path = programHandler.findPath(name);
                programHandler.openProgram(path);
            });

            appsPanel.add(appButton);
            appsPanel.add(Box.createRigidArea(new Dimension(0, 5))); // spacing
        }

        appsPanel.revalidate();
        appsPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton){
            try {
                programHandler.addProgram();
                createAppButtons();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
