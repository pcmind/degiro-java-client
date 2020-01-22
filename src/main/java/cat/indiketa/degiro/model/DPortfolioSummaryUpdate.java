package cat.indiketa.degiro.model;

import lombok.Data;

@Data
public class DPortfolioSummaryUpdate {
    private long lastUpdated;
    private boolean isAdded;
    private DPortfolioSummary portfolioSummary;

    public boolean hasChanges() {
        return portfolioSummary != null;
    }
}
