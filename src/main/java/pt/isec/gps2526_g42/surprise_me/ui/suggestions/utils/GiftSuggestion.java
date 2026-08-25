package pt.isec.gps2526_g42.surprise_me.ui.suggestions.utils;

public final class GiftSuggestion {
    private final String title;
    private final String shortDescription;
    private final String longDescription;
    private final String price;

    public GiftSuggestion(String title, String description, String price) {
        this.title = title == null ? "" : title.trim();
        String desc = description == null ? "" : description.trim();
        this.longDescription = desc;
        this.shortDescription = buildShort(desc);
        this.price = price == null ? "" : price.trim();
    }

    public GiftSuggestion(String title, String shortDescription, String longDescription, String price, boolean dual) {
        this.title = title == null ? "" : title.trim();
        this.shortDescription = shortDescription == null ? "" : shortDescription.trim();
        this.longDescription = longDescription == null ? "" : longDescription.trim();
        this.price = price == null ? "" : price.trim();
    }

    public String title() {
        return title;
    }

    public String description() {
        return longDescription;
    }

    public String shortDescription() {
        return shortDescription;
    }

    public String price() {
        return price;
    }

    public boolean isValidTitle() {
        if (title.isBlank()) {
            return false;
        }
        if (title.length() > 50) {
            return false;
        }
        String[] words = title.split("\\s+");
        if (words.length > 3) {
            return false;
        }
        String lt = title.toLowerCase();
        return !(lt.contains(" for ") || lt.contains(" with ") || lt.contains(" that ") || lt.contains(" and "));
    }

    private static String buildShort(String text) {
        if (text == null) {
            return "";
        }
        String t = text.trim();
        final int limit = 240;
        if (t.length() <= limit) {
            return t;
        }
        int cut = t.lastIndexOf(' ', limit);
        if (cut < 0) {
            cut = limit;
        }
        // No trailing "..." here. The card will add ellipsis only if visual overflow happens.
        return t.substring(0, Math.max(0, cut)).trim();
    }
}
