import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        new Thread(() -> {
            try {
                if (path.contains("com.") || path.contains("org.") || path.contains("us.")) {
                    new ProcessBuilder("flatpak", "run", path).start();
                }
                else if (path.charAt(0) >= '0' && path.charAt(0) <= '9') {
                    System.out.println("steam");
                    new ProcessBuilder("steam", "-applaunch", path).start();
                }
                else if (path.contains("aUniquePathForLegendaryGames")) {
                    System.out.println("detected");
                    String gamePath = path.replace("aUniquePathForLegendaryGames", "");

                    ProcessBuilder pb = new ProcessBuilder("legendary", "launch", gamePath);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    boolean finished = process.waitFor(3, TimeUnit.SECONDS);
                    if (finished) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()))) {
                            String output = reader.lines().collect(Collectors.joining("\n"));

                            System.out.println("Output: " + output);

                            if (output.contains("ERROR") && output.contains("not") && output.contains("installed")) {
                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(null,
                                                "Installing/updating/repairing game. please wait until confirmation appears"));

                                ProcessBuilder installPb = new ProcessBuilder("legendary", "install", gamePath, "--yes");
                                installPb.inheritIO();
                                process = installPb.start();
                                process.waitFor();

                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(null, "Installed!"));
                            }
                        }
                    } else {
                        process.destroy();
                        System.out.println("timed out");
                    }
                }
                else {
                    new ProcessBuilder(path).inheritIO().start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                                "Error launching: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null,
                                "Launch interrupted",
                                "Error",
                                JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

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


    public void downloadCover(String appid) throws IOException {
        String imageUrl = "https://cdn.cloudflare.steamstatic.com/steam/apps/"
                + appid + "/header.jpg";
        HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int code = conn.getResponseCode();
        if (code != 200) {
            return;
        }

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(appid + ".png"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getSteamGames(String apiKey, String steamId) throws Exception {

        String urlStr = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/"
                + "?key=" + apiKey
                + "&steamid=" + steamId
                + "&include_appinfo=0"
                + "&include_played_free_games=1";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        String json = response.toString();
        ArrayList<String> appIds = new ArrayList<>();
        int index = 0;
        while ((index = json.indexOf("\"appid\":", index)) != -1) {
            index += 8;
            int end = json.indexOf(",", index);
            String appId = json.substring(index, end).trim();
            appIds.add(appId);
        }

        return appIds.toArray(new String[0]);
    }

    public void addSteamGames() throws Exception {
        String apiKey = JOptionPane.showInputDialog("enter steam api key");
        String steamId = JOptionPane.showInputDialog("enter steam id");
        String [] appids = getSteamGames(apiKey,steamId);
        int i;
        for (i =0;i < appids.length;i++) {
            String jsun = new String(new java.net.URL(
                    "https://store.steampowered.com/api/appdetails?appids=" + appids[i]
            ).openStream().readAllBytes());
            String name = jsun.split("\"name\":\"")[1].split("\"")[0];
            if (!savedJson(appids[i])) {
                String json = "{\n" + " \"name\": \"" + name + "\",\n" + " \"path\": \"" + appids[i] + "\"\n" + "}" + "\n";
                Files.writeString(Path.of("data.json"), json, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                downloadCover(appids[i]);
            }
        }
        JOptionPane.showMessageDialog(null, "SAVED: " + i + " games");
    }



    public void allLegendaryGames() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("legendary", "list");
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String allLine = reader.lines().collect(Collectors.joining("\n"));
        process.waitFor();
        if (allLine != null && allLine.contains("ERROR") && allLine.contains("Login")) {
            pb = new ProcessBuilder("legendary","auth");
            pb.redirectErrorStream(true);
            process = pb.start();
            OutputStream stdin = process.getOutputStream();
            String auth = JOptionPane.showInputDialog("enter auth");
            stdin.write((auth + "\n").getBytes());
            stdin.flush();
            stdin.close();
            process.waitFor();
        }
        else if (allLine != null) {
            System.out.println("1");
            process = pb.start();
            reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            System.out.println("2");
            String line = reader.readLine();
            System.out.println("3");
            while (line != null){
                System.out.println("4");
                if (line.contains("*")){
                    System.out.println("contains *");
                    String [] filter = line.replace("*","").replace(" ","").split("\\(");
                    String name = filter[0];
                    filter = filter[1].split(":");
                    filter = filter[1].split("\\|");
                    String path = filter[0].replace(" ","");
                    if (!savedJson(name)) {
                        String json = "{\n" + " \"name\": \"" + name + "\",\n" + " \"path\": \"" + "aUniquePathForLegendaryGames" + path + "\"\n" + "}" + "\n";
                        Files.writeString(Path.of("data.json"), json, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    }
                }
                else
                    System.out.println("!contains *");
                line = reader.readLine();
            }
        }
    }

}