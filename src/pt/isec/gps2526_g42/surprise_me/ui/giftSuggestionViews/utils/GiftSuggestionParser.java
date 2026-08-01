package pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class GiftSuggestionParser {

    private GiftSuggestionParser() {
    }

    public static List<GiftSuggestion> parseAll(String text) {
        List<GiftSuggestion> out = tryParseJson(text);
        if (!out.isEmpty()) {
            return out;
        }

        out = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return out;
        }

        String[] lines = text.split("\n");
        StringBuilder current = new StringBuilder();
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.matches("^\\d+\\.\\s*.*")) {
                if (!current.isEmpty()) {
                    out.add(parseLine(current.toString()));
                    current = new StringBuilder();
                }
                current.append(line);
            } else if (!current.isEmpty()) {
                current.append(" ").append(line);
            }
        }
        if (!current.isEmpty()) {
            out.add(parseLine(current.toString()));
        }

        return out;
    }

    public static GiftSuggestion parseFirst(String text) {
        List<GiftSuggestion> list = parseAll(text);
        return list.isEmpty() ? new GiftSuggestion("New Suggestion", "", "") : list.get(0);
    }

    private static List<GiftSuggestion> tryParseJson(String text) {
        List<GiftSuggestion> list = new ArrayList<>();
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode root = m.readTree(text);
            JsonNode arr = root.path("suggestions");
            if (!arr.isArray() || arr.isEmpty()) {
                return list;
            }
            for (JsonNode n : arr) {
                String title = n.path("title").asText();
                String shortD = n.has("shortDescription") ? n.path("shortDescription").asText() : null;
                String longD = n.has("longDescription") ? n.path("longDescription").asText() : n.path("description").asText();
                String price = n.path("estimatedPrice").asText();
                if (shortD != null) {
                    list.add(new GiftSuggestion(title, shortD, longD, price, true));
                } else {
                    list.add(new GiftSuggestion(title, longD, price));
                }
            }
        } catch (Exception ex) {
            // ignored
        }
        return list;
    }

    private static GiftSuggestion parseLine(String numbered) {
        String line = numbered.replaceFirst("^\\d+\\.\\s*", "");
        String title;
        String description;
        String price = "";

        if (line.contains("**")) {
            int s = line.indexOf("**");
            int e = line.indexOf("**", s + 2);
            if (e > s) {
                title = line.substring(s + 2, e).trim();
                line = line.substring(e + 2).trim();
                if (line.startsWith("-")) line = line.substring(1).trim();
            } else {
                return parseWithDash(line);
            }
        } else {
            return parseWithDash(line);
        }

        if (line.contains("(") && line.contains(")")) {
            int ps = line.lastIndexOf("(");
            int pe = line.lastIndexOf(")");
            if (pe > ps) {
                price = line.substring(ps + 1, pe).trim();
                description = line.substring(0, ps).trim();
            } else {
                description = line.trim();
            }
        } else {
            description = line.trim();
        }

        String shortD = null;
        String longD = null;
        String lower = description.toLowerCase();
        int idxShort = lower.indexOf("short:");
        int idxLong = lower.indexOf("long:");
        if (idxShort >= 0 && idxLong > idxShort) {
            shortD = description.substring(idxShort + 6, idxLong).replace("|", "").trim();
            longD = description.substring(idxLong + 5).trim();
        }

        if (shortD != null) {
            return new GiftSuggestion(title, shortD, longD, price, true);
        }
        return new GiftSuggestion(title, description, price);
    }

    private static GiftSuggestion parseWithDash(String line) {
        String title;
        String description = "";
        String price = "";

        int dash = line.indexOf(" - ");
        if (dash > 0) {
            title = line.substring(0, dash).trim();
            String remaining = line.substring(dash + 3).trim();
            if (remaining.contains("(") && remaining.contains(")")) {
                int ps = remaining.lastIndexOf("(");
                int pe = remaining.lastIndexOf(")");
                if (pe > ps) {
                    price = remaining.substring(ps + 1, pe).trim();
                    description = remaining.substring(0, ps).trim();
                } else {
                    description = remaining;
                }
            } else {
                description = remaining;
            }
        } else {
            if (line.length() > 30) {
                int breakPoint = -1;
                for (int i = 0; i < Math.min(30, line.length()); i++) {
                    char ch = line.charAt(i);
                    if (ch == ',' || ch == ';') {
                        breakPoint = i;
                        break;
                    }
                }
                if (breakPoint > 0) {
                    title = line.substring(0, breakPoint).trim();
                    description = line.substring(breakPoint + 1).trim();
                } else {
                    String[] words = line.split("\\s+");
                    if (words.length > 3) {
                        title = String.join(" ", words[0], words[1], words[2]);
                        description = String.join(" ", java.util.Arrays.copyOfRange(words, 3, words.length));
                    } else {
                        title = line.trim();
                    }
                }
            } else {
                title = line.trim();
            }
        }

        String shortD;
        String longD;
        String lower = description.toLowerCase();
        int idxShort = lower.indexOf("short:");
        int idxLong = lower.indexOf("long:");
        if (idxShort >= 0 && idxLong > idxShort) {
            shortD = description.substring(idxShort + 6, idxLong).replace("|", "").trim();
            longD = description.substring(idxLong + 5).trim();
            return new GiftSuggestion(title, shortD, longD, price, true);
        }

        return new GiftSuggestion(title, description, price);
    }
}
