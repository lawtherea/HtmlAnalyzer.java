
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        }

        String urlStr = args[0];

        try {
            URL url = new URL(urlStr);

            Deque<String> stack = new ArrayDeque<>();
            int maxDepth = -1;
            String bestText = null;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openStream())
            )) {
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.strip();
                    if (line.isEmpty()) {
                        continue;
                    }

                    if (isOpeningTag(line)) {
                        String tag = extractTagName(line);
                        stack.push(tag);

                    } else if (isClosingTag(line)) {
                        String tag = extractTagName(line);

                        if (stack.isEmpty() || !stack.peek().equals(tag)) {
                            System.out.println("malformed HTML");
                            return;
                        }

                        stack.pop();

                    } else {
                        int depth = stack.size();
                        if (depth > maxDepth) {
                            maxDepth = depth;
                            bestText = line;
                        }
                    }
                }
            }

            if (!stack.isEmpty()) {
                System.out.println("malformed HTML");
                return;
            }

            if (bestText != null) {
                System.out.println(bestText);
            }

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
