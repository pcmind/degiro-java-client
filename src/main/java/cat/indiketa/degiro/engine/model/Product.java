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
package cat.indiketa.degiro.engine.model;

import cat.indiketa.degiro.engine.DEngine;
import cat.indiketa.degiro.model.DPortfolioProducts.DPortfolioProduct;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.DProductDescription;
import cat.indiketa.degiro.model.DProductType;
import com.google.common.base.Strings;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author ecatala
 */
public class Product {

    private final DEngine engine;
    protected long id; //
    protected String name; //
    protected long qty; //
    protected BigDecimal price; //
    protected BigDecimal value; //
    protected long lastUpdate; //
    protected long contractSize; //
    protected String currency; //
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

    public Product(DEngine engine) {
        this.engine = engine;
    }

    public void adopt(DPrice prod) {
        int previousHash = hashCode();
        if (prod != null) {
            bid = new BigDecimal(prod.getBid());
            ask = new BigDecimal(prod.getAsk());
            last = new BigDecimal(prod.getLast());
            priceTime = prod.getLastTime().getTime();
        }

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

    public void adopt(DPortfolioProduct prod) {
        int previousHash = hashCode();
        if (prod != null) {
            id = prod.getId();
            if (Strings.isNullOrEmpty(name)) {
                name = prod.getProduct();
            }
            qty = prod.getSize();
            contractSize = prod.getContractSize();
            currency = prod.getCurrency();

            if (prod.getPrice() != null && prod.getChange() != null) {
                int scale = prod.getPrice().scale();
                price = price.subtract(price.multiply(prod.getChange())).setScale(scale, RoundingMode.HALF_UP);
                value = price.multiply(new BigDecimal(qty)).multiply(new BigDecimal(prod.getContractSize()));
            }

            result = prod.getPlBase().add(value);
            todayResult = prod.getTodayPlBase().add(value);
        }

        if (previousHash != hashCode()) {
            publish();
        }
    }

    private void publish() {

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

}
