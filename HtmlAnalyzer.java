
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public final class HtmlAnalyzer {

    private static final String MALFORMED_HTML = "malformed HTML";
    private static final String URL_CONNECTION_ERROR = "URL connection error";

    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            return;
        }

        HtmlDepthAnalyzer.Outcome outcome = analyze(args[0]);

        switch (outcome.status) {
            case TEXT ->
                System.out.println(outcome.text);
            case MALFORMED ->
                System.out.println(MALFORMED_HTML);
            case URL_ERROR ->
                System.out.println(URL_CONNECTION_ERROR);
            case NONE -> {
            }
        }
    }

    private static HtmlDepthAnalyzer.Outcome analyze(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                return new HtmlDepthAnalyzer().findDeepestText(br);
            }
        } catch (IOException | RuntimeException e) {
            return HtmlDepthAnalyzer.Outcome.urlError();
        }
    }
}
