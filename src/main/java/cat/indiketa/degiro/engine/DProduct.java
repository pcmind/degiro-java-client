package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.engine.event.DPriceChanged;
import cat.indiketa.degiro.engine.event.DProductAdded;
import cat.indiketa.degiro.engine.event.DProductChanged;
import cat.indiketa.degiro.engine.event.DProductDescribed;
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
    private boolean priceUpdateEnabled;

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

    void setLastPrice(DPrice lastPrice) {
        if (this.lastPrice == null || (lastPrice.getLastTime().getTime() - this.lastPrice.getLastTime().getTime() <= engine.getConfig().getProductPriceHistoryMinInterval() * 1000)) {
            priceHistory.add(lastPrice);
        }
        this.lastPrice = lastPrice;
        engine.getEventBus().post(new DPriceChanged(lastPrice, productStatus.getId()));
    }

    public DPortfolioProduct getProductStatus() {
        return productStatus;
    }

    void setProductStatus(DPortfolioProduct productStatus) {
        boolean isNew = this.productStatus == null;
        boolean changed = !isNew && productStatus.hashCode() != this.productStatus.hashCode();
        this.productStatus = productStatus;
        
        if (isNew) {
            engine.getEventBus().post(new DProductAdded(productStatus));
        }

        if (changed) {
            engine.getEventBus().post(new DProductChanged(productStatus, productStatus.getId()));
        }
    }

    void setDescription(DProductDescription description) {
        this.description = description;
        engine.getEventBus().post(new DProductDescribed(description, productStatus.getId()));
    }

    public boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPriceUpdateEnabled() {
        return priceUpdateEnabled;
    }

    void setPriceUpdateEnabled(boolean priceUpdateEnabled) {
        this.priceUpdateEnabled = priceUpdateEnabled;
    }

}
