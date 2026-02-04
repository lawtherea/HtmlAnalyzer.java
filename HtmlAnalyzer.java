import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HtmlAnalyzer {
    public static void main(String[] args) {
        if (args.length != 1) return;

        String urlStr = args[0];

        try {
            URL url = new URL(urlStr);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );

            int validLines = 0;
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                validLines++;
            }

            br.close();
            System.out.println(validLines);

        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }
}
