package pt.isec.gps2526_g42.surprise_me.ui.suggestions.utils;

import pt.isec.gps2526_g42.surprise_me.application.GiftCriteria;

import java.util.List;
import java.util.Set;

public final class GiftCriteriaUtil {
    private GiftCriteriaUtil() {
    }

    public static GiftCriteria withRejections(GiftCriteria base, Set<String> currentTitles, List<String> rejected) {
        GiftCriteria copy = new GiftCriteria();
        if (base != null) {
            copy.setMinBudget(base.getMinBudget());
            copy.setMaxBudget(base.getMaxBudget());
            copy.setOccasion(base.getOccasion());
            copy.setGiftType(base.getGiftType());
            copy.setSustainable(base.isSustainable());
            copy.setUseful(base.isUseful());
            copy.setAdditionalIdeas(base.getAdditionalIdeas());
            copy.setThingsToAvoid(base.getThingsToAvoid());
        }

        StringBuilder avoid = new StringBuilder();
        if (copy.getThingsToAvoid() != null && !copy.getThingsToAvoid().trim().isEmpty()) {
            avoid.append(copy.getThingsToAvoid().trim()).append(". ");
        }
        if ((rejected != null && !rejected.isEmpty()) || (currentTitles != null && !currentTitles.isEmpty())) {
            avoid.append("Never suggest these: ");
            boolean first = true;
            if (rejected != null) {
                for (String s : rejected) {
                    if (!first) avoid.append(", ");
                    avoid.append(s);
                    first = false;
                }
            }
            if (currentTitles != null) {
                for (String s : currentTitles) {
                    if (!first) avoid.append(", ");
                    avoid.append(s);
                    first = false;
                }
            }
        }
        copy.setThingsToAvoid(avoid.toString());
        return copy;
    }
}
