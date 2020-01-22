package cat.indiketa.degiro.model;

import lombok.Data;

@Data
public class DUpdates {
    DOrders orders;
    DPortfolioProducts portfolio;
    DPortfolioSummaryUpdate portfolioSummary;
}
