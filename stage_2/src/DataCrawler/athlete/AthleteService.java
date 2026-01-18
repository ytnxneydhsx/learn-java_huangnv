package DataCrawler.athlete;

import java.io.IOException;

public class AthleteService {

    private final AthleteCrawler crawler;
    private final AthleteFormatter formatter;

    public AthleteService() {
        this(new AthleteCrawler(), new AthleteFormatter());
    }

    public AthleteService(AthleteCrawler crawler, AthleteFormatter formatter) {
        this.crawler = crawler;
        this.formatter = formatter;
    }

    // Format all players for the "players" command.
    public String formatAllPlayers() throws IOException {
        String json = crawler.fetchRawJson();
        if (json == null) {
            return "";
        }
        return formatter.formatAllPlayers(json);
    }
}
