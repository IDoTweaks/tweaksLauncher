import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SystemMonitor extends JFrame {
    private JLabel cpuLabel, ramLabel, gpuTempLabel, fpsLabel;
    private Timer updateTimer;
    private List<Long> frameTimes = new ArrayList<>();
    private long lastFrameTime = System.nanoTime();

    public SystemMonitor() {
        setTitle("System Monitor");
        setAlwaysOnTop(true);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 180));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0, 0, 0, 200));
        panel.setBorder(BorderFactory.createLineBorder(new Color(110, 80, 230), 2));

        cpuLabel = createLabel("CPU: ---%");
        ramLabel = createLabel("RAM: --- MB");
        gpuTempLabel = createLabel("GPU: --°C");
        fpsLabel = createLabel("FPS: --");

        panel.add(cpuLabel);
        panel.add(ramLabel);
        panel.add(gpuTempLabel);
        panel.add(fpsLabel);

        JButton closeBtn = new JButton("X");
        closeBtn.setBackground(new Color(200, 50, 50));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        panel.add(closeBtn);

        add(panel);
        pack();
        setLocation(10, 10);

        updateTimer = new Timer(1000, e -> updateStats());
        updateTimer.start();

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private void updateStats() {
        new Thread(() -> {
            try {
                // CPU Usage
                String cpu = execCommand("top -bn1 | grep \"Cpu(s)\" | awk '{print $2}'");
                SwingUtilities.invokeLater(() -> cpuLabel.setText("CPU: " + cpu + "%"));

                // RAM Usage
                String ram = execCommand("free -m | grep Mem | awk '{print $3}'");
                SwingUtilities.invokeLater(() -> ramLabel.setText("RAM: " + ram + " MB"));

                // GPU Temperature (NVIDIA)
                String gpuTemp = execCommand("nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader,nounits");
                if (gpuTemp.isEmpty()) {
                    // Try AMD
                    gpuTemp = execCommand("cat /sys/class/drm/card0/device/hwmon/hwmon*/temp1_input | awk '{print $1/1000}'");
                }
                String finalTemp = gpuTemp;
                SwingUtilities.invokeLater(() -> gpuTempLabel.setText("GPU: " + finalTemp + "°C"));

                // FPS calculation (approximate based on update rate)
                long now = System.nanoTime();
                long frameDelta = now - lastFrameTime;
                lastFrameTime = now;
                frameTimes.add(frameDelta);
                if (frameTimes.size() > 60) frameTimes.remove(0);

                long avgFrameTime = frameTimes.stream().mapToLong(Long::longValue).sum() / frameTimes.size();
                int fps = (int)(1_000_000_000.0 / avgFrameTime);
                SwingUtilities.invokeLater(() -> fpsLabel.setText("Monitor FPS: " + fps));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String execCommand(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = reader.readLine();
            p.waitFor();
            return result != null ? result.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
            updateTimer = null;
        }
        // Kill any running processes
        frameTimes.clear();
        super.dispose();
    }

    public void cleanup() {
        dispose();
    }
}