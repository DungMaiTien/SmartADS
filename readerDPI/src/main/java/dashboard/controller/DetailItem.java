package dashboard.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DetailItem {
    private final SimpleStringProperty host;
    private final SimpleIntegerProperty targetDomain;
    private final SimpleStringProperty Web_category;
    private final SimpleIntegerProperty adId;
    private final SimpleStringProperty adContent;
    private final SimpleIntegerProperty targetAgeMin;
    private final SimpleIntegerProperty targetAgeMax;
    private final SimpleStringProperty targetGender;
    private final SimpleIntegerProperty targetRadiusKm;

    public DetailItem(String host, int targetDomain, String Web_category, int adId, String adContent, int targetAgeMin, int targetAgeMax, String targetGender, int targetRadiusKm) {
        this.host = new SimpleStringProperty(host);
        this.targetDomain = new SimpleIntegerProperty(targetDomain);
        this.Web_category = new SimpleStringProperty(Web_category);
        this.adId = new SimpleIntegerProperty(adId);
        this.adContent = new SimpleStringProperty(adContent);
        this.targetAgeMin = new SimpleIntegerProperty(targetAgeMin);
        this.targetAgeMax = new SimpleIntegerProperty(targetAgeMax);
        this.targetGender = new SimpleStringProperty(targetGender);
        this.targetRadiusKm = new SimpleIntegerProperty(targetRadiusKm);
    }

    public String getHost() {
        return host.get();
    }

    public SimpleStringProperty hostProperty() {
        return host;
    }

    public int getTargetDomain() {
        return targetDomain.get();
    }

    public SimpleIntegerProperty targetDomainProperty() {
        return targetDomain;
    }

    public String getWeb_category() {
        return Web_category.get();
    }

    public SimpleStringProperty Web_categoryProperty() {
        return Web_category;
    }

    public int getAdId() {
        return adId.get();
    }

    public SimpleIntegerProperty adIdProperty() {
        return adId;
    }

    public String getAdContent() {
        return adContent.get();
    }

    public SimpleStringProperty adContentProperty() {
        return adContent;
    }

    public int getTargetAgeMin() {
        return targetAgeMin.get();
    }

    public SimpleIntegerProperty targetAgeMinProperty() {
        return targetAgeMin;
    }

    public int getTargetAgeMax() {
        return targetAgeMax.get();
    }

    public SimpleIntegerProperty targetAgeMaxProperty() {
        return targetAgeMax;
    }

    public String getTargetGender() {
        return targetGender.get();
    }

    public SimpleStringProperty targetGenderProperty() {
        return targetGender;
    }

    public int getTargetRadiusKm() {
        return targetRadiusKm.get();
    }

    public SimpleIntegerProperty targetRadiusKmProperty() {
        return targetRadiusKm;
    }
}
