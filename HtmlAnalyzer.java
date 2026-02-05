
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }

        String urlStr = args[0];

        try {
            URL url = new URL(urlStr);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );

            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (isOpeningTag(line)) {
                    System.out.println("OPEN " + extractTagName(line));
                } else if (isClosingTag(line)) {
                    System.out.println("CLOSE " + extractTagName(line));
                } else {
                    System.out.println("TEXT " + line);
                }

            }

            br.close();

        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }

    private static boolean isTagLine(String line) {
        return line.startsWith("<") && line.endsWith(">");
    }

    private static boolean isClosingTag(String line) {
        return isTagLine(line) && line.startsWith("</");
    }

    private static boolean isOpeningTag(String line) {
        return isTagLine(line) && !isClosingTag(line);
    }

    private static String extractTagName(String line) {
        int start = line.startsWith("</") ? 2 : 1;
        int end = line.length() - 1;
        return line.substring(start, end).trim();
    }

}
