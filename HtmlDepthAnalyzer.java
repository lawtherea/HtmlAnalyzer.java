
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public final class HtmlDepthAnalyzer {

    public enum Status {
        TEXT, MALFORMED, URL_ERROR, NONE
    }

    public static final class Outcome {

        public final Status status;
        public final String text;

        private Outcome(Status status, String text) {
            this.status = status;
            this.text = text;
        }

        public static Outcome text(String t) {
            return new Outcome(Status.TEXT, t);
        }

        public static Outcome malformed() {
            return new Outcome(Status.MALFORMED, null);
        }

        public static Outcome urlError() {
            return new Outcome(Status.URL_ERROR, null);
        }

        public static Outcome none() {
            return new Outcome(Status.NONE, null);
        }
    }

    public Outcome findDeepestText(BufferedReader br) throws IOException {
        Deque<String> stack = new ArrayDeque<>();
        int maxDepth = -1;
        String bestText = null;

        String rawLine;
        while ((rawLine = br.readLine()) != null) {
            // ignorar espaços iniciais e linhas em branco
            String noIndent = rawLine.stripLeading();
            String content = noIndent.trim();

            if (content.isEmpty()) {
                continue;
            }

            if (isTagLine(content)) {
                boolean closing = isClosingTag(content);
                String tagName = extractTagName(content, closing);
                if (tagName == null) {
                    return Outcome.malformed();
                }

                if (closing) {
                    if (stack.isEmpty() || !stack.peek().equals(tagName)) {
                        return Outcome.malformed();
                    }
                    stack.pop();
                } else {
                    stack.push(tagName);
                }
            } else {
                int depth = stack.size();
                if (depth > maxDepth) { // empate mantém o primeiro
                    maxDepth = depth;
                    bestText = content; // sem indentação; preserva o texto da linha
                }
            }
        }

        if (!stack.isEmpty()) {
            return Outcome.malformed();
        }

        return (bestText == null) ? Outcome.none() : Outcome.text(bestText);
    }

    private boolean isTagLine(String s) {
        return s.startsWith("<") && s.endsWith(">");
    }

    private boolean isClosingTag(String s) {
        return s.startsWith("</");
    }

    private String extractTagName(String s, boolean closing) {
        // remove '<' e '>'
        String inner = s.substring(1, s.length() - 1).trim();

        if (inner.isEmpty()) {
            return null;
        }

        if (closing) {
            if (!inner.startsWith("/")) {
                return null;
            }
            inner = inner.substring(1).trim();
            if (inner.isEmpty()) {
                return null;
            }
        }

        // validar caracteres do nome da tag
        for (int i = 0; i < inner.length(); i++) {
            char c = inner.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return null;
            }
        }

        return inner.toLowerCase();
    }
}
