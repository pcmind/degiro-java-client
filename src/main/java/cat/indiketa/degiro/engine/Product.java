/*
 * Copyright 2018 ecatala.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cat.indiketa.degiro.engine;

import cat.indiketa.degiro.engine.event.DProductChanged;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.DPortfolioProducts.DPortfolioProduct;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DProductDescription;
import cat.indiketa.degiro.model.DProductType;
import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author ecatala
 */
public class Product {

    private final transient DEngine engine;

    protected long id; //
    protected String name; //
    protected long qty; //
    protected BigDecimal price; //
    protected BigDecimal value; //
    protected BigDecimal change; //
    protected long lastUpdate; //
    protected long contractSize = 1; //
    protected String currency; //
    protected BigDecimal todayBase; //
    protected BigDecimal totalBase; //
    protected BigDecimal result; //
    protected BigDecimal todayResult; //
    private String vwdId;
    private DProductType type;
    private BigDecimal closePrice;
    private long closePriceDate;
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal last;
    private long priceTime;

    /////// STRATEGY
    private List<Strategy> strategies;

    public Product(DEngine engine) {
        this.engine = engine;
    }

    public void adopt(DPrice prod) {
        int previousHash = hashCode();

//        DLog.ENGINE.debug("adopting price " + new Gson().toJson(prod) + " on " + name + " hash=" + previousHash);
        if (prod != null) {
            if (prod.getBid() != null) {
                bid = new BigDecimal(prod.getBid());
            }
            if (prod.getAsk() != null) {
                ask = new BigDecimal(prod.getAsk());
            }
            if (prod.getLast() != null) {
                last = new BigDecimal(prod.getLast());
                value = last.multiply(new BigDecimal(qty)).multiply(new BigDecimal(contractSize));
                result = totalBase.add(value);
                todayResult = todayBase.add(value);
            }
            if (prod.getLastTime() != null) {
                priceTime = prod.getLastTime().getTime();
            }
        }
//        DLog.ENGINE.debug("hash=" + hashCode());

        if (previousHash != hashCode()) {
            publish();
        }
    }

    public void adopt(DProductDescription prod) {
        int previousHash = hashCode();
        if (prod != null) {

            name = prod.getName();
            vwdId = prod.getVwdId();
            type = prod.getProductTypeId();
            closePrice = new BigDecimal(prod.getClosePrice());
            closePriceDate = prod.getClosePriceDate().getTime();
        }

        if (previousHash != hashCode()) {
            publish();
        }
    }

    public void adopt(DPortfolioProduct prod) throws DeGiroException {

        if (prod == null) {
            return;
        }

        int previousHash = hashCode();
        id = prod.getId();
        if (Strings.isNullOrEmpty(name)) {
            name = prod.getProduct();
        }
        qty = prod.getSize();

        if (!Strings.isNullOrEmpty(vwdId)) {
            if (qty == 0) {
                engine.getDegiro().unsubscribeToPrice(vwdId);
            } else {
                engine.getDegiro().subscribeToPrice(vwdId);
            }
        }

        contractSize = prod.getContractSize();
        currency = prod.getCurrency();
        change = prod.getChange();

        if (prod.getPrice() != null && change != null) {
            int scale = prod.getPrice().scale();
            price = prod.getPrice().subtract(prod.getPrice().multiply(change)).setScale(scale, RoundingMode.HALF_UP);
            value = price.multiply(new BigDecimal(qty)).multiply(new BigDecimal(prod.getContractSize()));
        }
        if (lastUpdate == 0) {
            last = price;
            if (prod.getLastUpdate() != null) {
                priceTime = prod.getLastUpdate().getTime();
            }
        }
        if (prod.getLastUpdate() != null) {
            lastUpdate = prod.getLastUpdate().getTime();

        }

        todayBase = prod.getTodayPlBase();
        totalBase = prod.getPlBase();

        result = prod.getPlBase().add(value);
        todayResult = prod.getTodayPlBase().add(value);

        if (previousHash != hashCode()) {
            publish();
        }

    }

    private void recalc() {

    }

    private void publish() {

        engine.getEventBus().post(new DProductChanged(this));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getContractSize() {
        return contractSize;
    }

    public void setContractSize(long contractSize) {
        this.contractSize = contractSize;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public BigDecimal getTodayResult() {
        return todayResult;
    }

    public void setTodayResult(BigDecimal todayResult) {
        this.todayResult = todayResult;
    }

    public String getVwdId() {
        return vwdId;
    }

    public void setVwdId(String vwdId) {
        this.vwdId = vwdId;
    }

    public DProductType getType() {
        return type;
    }

    public void setType(DProductType type) {
        this.type = type;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public long getClosePriceDate() {
        return closePriceDate;
    }

    public void setClosePriceDate(long closePriceDate) {
        this.closePriceDate = closePriceDate;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public long getPriceTime() {
        return priceTime;
    }

    public void setPriceTime(long priceTime) {
        this.priceTime = priceTime;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 67 * hash + (int) (this.qty ^ (this.qty >>> 32));
        hash = 67 * hash + Objects.hashCode(this.price);
        hash = 67 * hash + Objects.hashCode(this.value);
        hash = 67 * hash + (int) (this.lastUpdate ^ (this.lastUpdate >>> 32));
        hash = 67 * hash + Objects.hashCode(this.result);
        hash = 67 * hash + Objects.hashCode(this.todayResult);
        hash = 67 * hash + Objects.hashCode(this.vwdId);
        hash = 67 * hash + Objects.hashCode(this.bid);
        hash = 67 * hash + Objects.hashCode(this.ask);
        hash = 67 * hash + Objects.hashCode(this.change);
        hash = 67 * hash + Objects.hashCode(this.last);
        hash = 67 * hash + (int) (this.priceTime ^ (this.priceTime >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.qty != other.qty) {
            return false;
        }
        if (this.lastUpdate != other.lastUpdate) {
            return false;
        }
        if (this.priceTime != other.priceTime) {
            return false;
        }
        if (!Objects.equals(this.vwdId, other.vwdId)) {
            return false;
        }
        if (!Objects.equals(this.price, other.price)) {
            return false;
        }
        if (!Objects.equals(this.change, other.change)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.result, other.result)) {
            return false;
        }
        if (!Objects.equals(this.todayResult, other.todayResult)) {
            return false;
        }
        if (!Objects.equals(this.bid, other.bid)) {
            return false;
        }
        if (!Objects.equals(this.ask, other.ask)) {
            return false;
        }
        if (!Objects.equals(this.last, other.last)) {
            return false;
        }
        return true;
    }

    public List<Strategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<Strategy> strategies) {
        this.strategies = strategies;
    }

}
