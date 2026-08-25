package pt.isec.gps2526_g42.surprise_me.application;

public class GiftCriteria {
    private double minBudget;
    private double maxBudget;
    private String occasion;
    private String giftType;
    private String thingsToAvoid;
    private boolean sustainable;
    private boolean useful;
    private String additionalIdeas;

    // Getters and setters
    public double getMinBudget() { return minBudget; }

    public void setMinBudget(double minBudget) { this.minBudget = minBudget; }

    public double getMaxBudget() { return maxBudget; }

    public void setMaxBudget(double maxBudget) { this.maxBudget = maxBudget; }

    public String getOccasion() { return occasion; }

    public void setOccasion(String occasion) { this.occasion = occasion; }

    public String getGiftType() { return giftType; }

    public void setGiftType(String giftType) { this.giftType = giftType; }

    public String getThingsToAvoid(){ return thingsToAvoid; }

    public void setThingsToAvoid(String thingsToAvoid){ this.thingsToAvoid = thingsToAvoid; }

    public String getAdditionalIdeas() { return additionalIdeas; }

    public void setAdditionalIdeas(String additionalIdeas) { this.additionalIdeas = additionalIdeas; }

    public boolean isSustainable() { return sustainable; }

    public void setSustainable(boolean sustainable) { this.sustainable = sustainable; }

    public boolean isUseful() { return useful; }

    public void setUseful(boolean useful) { this.useful = useful; }
}
