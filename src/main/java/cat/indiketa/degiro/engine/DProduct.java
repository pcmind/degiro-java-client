package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.model.DPortfolioProducts.DPortfolioProduct;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DProductDescription;
import com.google.common.collect.EvictingQueue;

/**
 *
 * @author indiketa
 */
public class DProduct {

    private final DEngine engine;
    private DPortfolioProduct productStatus;
    private DProductDescription description;
    private DPrice lastPrice;
    private final EvictingQueue<DPrice> priceHistory;
    private boolean active;

    public DProduct(DEngine engine) {
        this.engine = engine;
        priceHistory = EvictingQueue.create(engine.getConfig().getProductPriceHistoryCount());
    }

    public DProductDescription getDescription() {
        return description;
    }

    public DPrice getLastPrice() {
        return lastPrice;
    }

    public EvictingQueue<DPrice> getPriceHistory() {
        return priceHistory;
    }

    public void setLastPrice(DPrice lastPrice) {
        if (this.lastPrice == null || (lastPrice.getLastTime().getTime() - this.lastPrice.getLastTime().getTime() <= engine.getConfig().getProductPriceHistoryMinInterval() * 1000)) {
            priceHistory.add(lastPrice);
        }
        this.lastPrice = lastPrice;
    }

    public DPortfolioProduct getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(DPortfolioProduct productStatus) {
        this.productStatus = productStatus;
    }

    public void setDescription(DProductDescription description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
