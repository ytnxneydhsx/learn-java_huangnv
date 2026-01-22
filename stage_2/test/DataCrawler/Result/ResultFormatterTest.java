package DataCrawler.Result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultFormatterTest {
    private static final String FINAL_ONLY_JSON =
            "{"
                    + "\"Heats\":["
                    + "{"
                    + "\"PhaseName\":\"Finals\","
                    + "\"Results\":["
                    + "{"
                    + "\"Rank\":1,"
                    + "\"TotalPoints\":\"248.95\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"51.60\",\"TotalPoints\":\"51.60\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"52.00\",\"TotalPoints\":\"103.60\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"51.75\",\"TotalPoints\":\"155.35\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"46.80\",\"TotalPoints\":\"202.15\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"46.80\",\"TotalPoints\":\"248.95\"}"
                    + "],"
                    + "\"FullName\":\"MULLER Jette\""
                    + "},"
                    + "{"
                    + "\"Rank\":2,"
                    + "\"TotalPoints\":\"240.40\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"46.00\",\"TotalPoints\":\"46.00\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"42.90\",\"TotalPoints\":\"88.90\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"50.70\",\"TotalPoints\":\"139.60\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"54.00\",\"TotalPoints\":\"193.60\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"46.80\",\"TotalPoints\":\"240.40\"}"
                    + "],"
                    + "\"FullName\":\"ROLLINSON Amy\""
                    + "}"
                    + "]"
                    + "}"
                    + "]"
                    + "}";

    private static final String DETAIL_JSON =
            "{"
                    + "\"Heats\":["
                    + "{"
                    + "\"PhaseName\":\"Preliminaries\","
                    + "\"Results\":["
                    + "{"
                    + "\"PersonId\":\"84abb6f6-fdf2-48a2-8732-83f2a642a347\","
                    + "\"FullName\":\"MULLER Jette\","
                    + "\"Rank\":1,"
                    + "\"TotalPoints\":\"234.40\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"54.00\",\"TotalPoints\":\"54.00\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"53.30\",\"TotalPoints\":\"107.30\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"36.80\",\"TotalPoints\":\"144.10\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"39.60\",\"TotalPoints\":\"183.70\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"50.70\",\"TotalPoints\":\"234.40\"}"
                    + "]"
                    + "},"
                    + "{"
                    + "\"PersonId\":\"379ad49d-fd57-439d-a87c-eae270a4e683\","
                    + "\"FullName\":\"WILSON Aimee\","
                    + "\"Rank\":2,"
                    + "\"TotalPoints\":\"226.30\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"50.40\",\"TotalPoints\":\"50.40\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"41.40\",\"TotalPoints\":\"91.80\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"39.60\",\"TotalPoints\":\"131.40\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"44.20\",\"TotalPoints\":\"175.60\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"50.70\",\"TotalPoints\":\"226.30\"}"
                    + "]"
                    + "}"
                    + "]"
                    + "},"
                    + "{"
                    + "\"PhaseName\":\"Finals\","
                    + "\"Results\":["
                    + "{"
                    + "\"PersonId\":\"84abb6f6-fdf2-48a2-8732-83f2a642a347\","
                    + "\"FullName\":\"MULLER Jette\","
                    + "\"Rank\":1,"
                    + "\"TotalPoints\":\"248.95\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"51.60\",\"TotalPoints\":\"51.60\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"52.00\",\"TotalPoints\":\"103.60\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"51.75\",\"TotalPoints\":\"155.35\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"46.80\",\"TotalPoints\":\"202.15\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"46.80\",\"TotalPoints\":\"248.95\"}"
                    + "]"
                    + "},"
                    + "{"
                    + "\"PersonId\":\"22699d3a-dc39-457e-8714-15cc91d7330d\","
                    + "\"FullName\":\"ROLLINSON Amy\","
                    + "\"Rank\":2,"
                    + "\"TotalPoints\":\"240.40\","
                    + "\"Dives\":["
                    + "{\"DiveOrder\":1,\"DivePoints\":\"46.00\",\"TotalPoints\":\"46.00\"},"
                    + "{\"DiveOrder\":2,\"DivePoints\":\"42.90\",\"TotalPoints\":\"88.90\"},"
                    + "{\"DiveOrder\":3,\"DivePoints\":\"50.70\",\"TotalPoints\":\"139.60\"},"
                    + "{\"DiveOrder\":4,\"DivePoints\":\"54.00\",\"TotalPoints\":\"193.60\"},"
                    + "{\"DiveOrder\":5,\"DivePoints\":\"46.80\",\"TotalPoints\":\"240.40\"}"
                    + "]"
                    + "}"
                    + "]"
                    + "}"
                    + "]"
                    + "}";

    @Test
    public void formatFinalResultsOrdersAndPairs() {
        ResultFormatter formatter = new ResultFormatter();
        String output = formatter.formatFinalResults(FINAL_ONLY_JSON);
        String expected =
                "Full Name:MULLER Jette\n" +
                "Rank:1\n" +
                "Score:51.60 + 52.00 + 51.75 + 46.80 + 46.80 = 248.95\n" +
                "-----\n" +
                "Full Name:ROLLINSON Amy\n" +
                "Rank:2\n" +
                "Score:46.00 + 42.90 + 50.70 + 54.00 + 46.80 = 240.40\n" +
                "-----\n";
        assertEquals(expected, output);
    }

    @Test
    public void formatDetailedResultsMissingPhaseUsesStar() {
        ResultFormatter formatter = new ResultFormatter();
        String output = formatter.formatDetailedResults(DETAIL_JSON);
        String expected =
                "Full Name:MULLER Jette\n" +
                "Rank:1 | * | 1\n" +
                "Preliminary Score:54.00 + 53.30 + 36.80 + 39.60 + 50.70 = 234.40\n" +
                "Semifinal Score:*\n" +
                "Final Score:51.60 + 52.00 + 51.75 + 46.80 + 46.80 = 248.95\n" +
                "-----\n" +
                "Full Name:WILSON Aimee\n" +
                "Rank:2 | * | *\n" +
                "Preliminary Score:50.40 + 41.40 + 39.60 + 44.20 + 50.70 = 226.30\n" +
                "Semifinal Score:*\n" +
                "Final Score:*\n" +
                "-----\n" +
                "Full Name:ROLLINSON Amy\n" +
                "Rank:* | * | 2\n" +
                "Preliminary Score:*\n" +
                "Semifinal Score:*\n" +
                "Final Score:46.00 + 42.90 + 50.70 + 54.00 + 46.80 = 240.40\n" +
                "-----\n";
        assertEquals(expected, output);
    }
}
