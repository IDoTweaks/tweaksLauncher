import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class programHandler {

    public programHandler() {
    }

    public void addProgram() throws IOException {
        String text = JOptionPane.showInputDialog("1 to enter a path or the program you want to TRY to find");
        if (text.equals("1")) {
            text = JOptionPane.showInputDialog("enter a path");
            String name = JOptionPane.showInputDialog("please enter a name for this app");
            openProgram(text);
            String json = "{\n" + " \"name\": \"" + name + "\",\n" + " \"path\": \"" + text + "\"\n" + "}" + "\n";
            Files.writeString(Path.of("data.json"), json, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } else {
            String path = findProgram(text);
            if (path == null) {
                JOptionPane.showMessageDialog(null, "error");
            } else {
                int save = JOptionPane.showConfirmDialog(null,"would you like to save it for the future?");
                if (!savedJson(text) && save == JOptionPane.YES_OPTION) {
                    String json = "{\n" + " \"name\": \"" + text + "\",\n" + " \"path\": \"" + path + "\"\n" + "}" + "\n";
                    Files.writeString(Path.of("data.json"), json, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    JOptionPane.showMessageDialog(null,"SAVED!");
                }
                openProgram(path);

            }
        }
    }

    public boolean savedJson(String name) {
        String line;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            {
                line = reader.readLine();
                while (line != null) {
                    if (line.contains(name)) {
                        return true;
                    }
                    line = reader.readLine();
                }
                return false;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int allSavedInt() {
        String line;
        int count = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            {
                line = reader.readLine();
                while (line != null) {
                    if (line.contains("name")) {
                        count++;
                    }
                    line = reader.readLine();
                }

            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String [] allSaved() {
        String line;
        int count = allSavedInt();
        String [] arr = new String[count];
        int i = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            {
                line = reader.readLine();
                while (line != null) {
                    if (line.contains("name")) {
                        arr[i] = line;
                        i++;
                    }
                    line = reader.readLine();
                }

            }
            return arr;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String findPath(String name) {
        String line;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            {
                line = reader.readLine();
                while (line != null) {
                    if (line.contains(name)) {
                        line = reader.readLine();
                        String[] parts = line.split(":");
                        line = parts[1];
                        return line.replace("\"", "").trim();
                    }
                    line = reader.readLine();
                }
                return line;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String findProgram(String name) {
        if (savedJson(name)) {
            return findPath(name);
        }
        ProcessBuilder pb = new ProcessBuilder("which", name);
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(line);
        if (line == null) {
            pb = new ProcessBuilder("bash", "-c", "flatpak list --app | grep -i " + name);
            process = null;
            try {
                JOptionPane.showMessageDialog(null, "trying to search your whole damn system " +
                        "this will take way too much time JUST WRITE A PATH GNG T_T");
                process = pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            try {
                line = reader.readLine();
                if (line != null && line.contains("com.")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length > 1)
                        line = parts[1];
                    else
                        line = parts[0];
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return line;
    }

    public void openProgram(String path) {
        if (path.contains("com.")){
            ProcessBuilder pb = new ProcessBuilder("flatpak","run",path);
            try {
                pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            ProcessBuilder pb = new ProcessBuilder(path).inheritIO();
            Process process = null;
            try {
                process = pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
