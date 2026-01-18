package DataCrawler.result;

import java.io.IOException;

public class ResultService {

    private final ResultCrawler crawler;
    private final ResultFormatter formatter;

    public ResultService() {
        this(new ResultCrawler(), new ResultFormatter());
    }

    public ResultService(ResultCrawler crawler, ResultFormatter formatter) {
        this.crawler = crawler;
        this.formatter = formatter;
    }

    // Format final results by discipline name.
    public String formatFinalResults(String disciplineName) throws IOException {
        String eventJson = crawler.fetchEventJsonByDisciplineName(disciplineName);
        if (eventJson == null) {
            return "";
        }
        return formatter.formatFinalResults(eventJson);
    }

    // Format detailed results by discipline name.
    public String formatDetailedResults(String disciplineName) throws IOException {
        String eventJson = crawler.fetchEventJsonByDisciplineName(disciplineName);
        if (eventJson == null) {
            return "";
        }
        return formatter.formatDetailedResults(eventJson);
    }
}
