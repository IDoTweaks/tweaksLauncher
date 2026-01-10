import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

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

    public String [] allSteamSaved(){
        String line;
        int count = allSavedInt();
        String [] arr = new String[count];
        int i = 0;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            {
                line = reader.readLine();
                while (line != null) {
                    if (line.contains("path")) {
                        String modLine = line.replace("\"","");
                        modLine = modLine.replace(" ","");
                        String [] lineSplit = modLine.split(":");
                        if (lineSplit[1].charAt(0) <= '9' && lineSplit[1].charAt(0) >= '0' ) {
                            arr[i] = line;
                            i++;
                        }
                    }
                    line = reader.readLine();
                }

            }
            return arr;
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
    public String findName(String path) {
        String line;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of("data.json"));
            BufferedReader reader2 = Files.newBufferedReader(Path.of("data.json"));
            line = reader.readLine();
            line = reader.readLine();
            String line2 = reader2.readLine();
            {
                while (line != null) {
                    if (line.contains(path)) {
                        String[] parts = line2.split(":");
                        line = parts[1];
                        return line.replace("\"", "").replace(" ","").trim();
                    }
                    line = reader.readLine();
                    line2 = reader2.readLine();
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

    public void removeProgram(String name){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Path.of("data.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(name)) {
                int from = Math.max(0, i - 1);
                int to = Math.min(lines.size(), i + 3);
                lines.subList(from, to).clear();
                break;
            }
        }

        try {
            Files.write(Path.of("data.json"), lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void openProgram(String path) {
        if (path.contains("com.") || path.contains("org.") || path.contains("us.")){
            ProcessBuilder pb = new ProcessBuilder("flatpak","run",path);
            try {
                pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (path.charAt(0) >= '0' && path.charAt(0) <= '9'){
            System.out.println("steam");
            ProcessBuilder pb = new ProcessBuilder("steam","-applaunch",path);
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

    //steam
    public void addSteamApp() throws IOException {
        String text = JOptionPane.showInputDialog("enter steam gameid");
        ProcessBuilder pb = new ProcessBuilder("steam", "-applaunch","<" + text + ">");
        int save = JOptionPane.showConfirmDialog(null,"wanna save the game?");
        if (!savedJson(text) && save == JOptionPane.YES_OPTION) {
            String name = JOptionPane.showInputDialog("enter game name");
            String json = "{\n" + " \"name\": \"" + name + "\",\n" + " \"path\": \"" + text + "\"\n" + "}" + "\n";
            Files.writeString(Path.of("data.json"), json, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            JOptionPane.showMessageDialog(null,"SAVED!");
            downloadCover(text);
        }
        pb.start();
    }

    public void downloadCover(String appid){
            String imageUrl = "https://cdn.cloudflare.steamstatic.com/steam/apps/"
                    + appid + "/header.jpg";

            try (InputStream in = new URL(imageUrl).openStream()) {
                Files.copy(in, Paths.get(appid + ".png"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

}
