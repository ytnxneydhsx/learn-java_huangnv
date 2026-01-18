package DataCrawler.athlete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AthleteCrawler {

    public static final String ATHLETE_URL =
            "https://api.worldaquatics.com/fina/competitions/3337/athletes?gender=&countryId=";
    public static final String DEFAULT_OUTPUT_PATH = "stage_2/data/athletes.json";

    // Public API: fetch JSON and persist files.
    // Fetch the athlete list JSON.
    public String fetchRawJson() throws IOException {
        return httpGet(ATHLETE_URL);
    }

    // Save raw JSON to disk using UTF-8.
    public void saveJsonToFile(String json, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, json.getBytes(StandardCharsets.UTF_8));
    }

    // Manual test entry: fetch and save athlete JSON.
    public static void main(String[] args) {
        AthleteCrawler crawler = new AthleteCrawler();
        try {
            String json = crawler.fetchRawJson();
            crawler.saveJsonToFile(json, DEFAULT_OUTPUT_PATH);
            System.out.println("Saved: " + DEFAULT_OUTPUT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String httpGet(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int code = conn.getResponseCode();
        InputStream in = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (in == null) {
            throw new IOException("HTTP " + code + " empty response");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + " " + sb);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
}
