package cat.indiketa.degiro.model;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author indiketa
 */
public class DPrice {

    private long issueId;
    private Double bid;
    private Double ask;
    private Double last;
    private Date lastTime;
    private String vwdProductName;

    public long getIssueId() {
        return issueId;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public Double getLast() {
        return last;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getVwdProductName() {
        return vwdProductName;
    }

    public void setVwdProductName(String vwdProductName) {
        this.vwdProductName = vwdProductName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.bid);
        hash = 59 * hash + Objects.hashCode(this.ask);
        hash = 59 * hash + Objects.hashCode(this.last);
        hash = 59 * hash + Objects.hashCode(this.lastTime);
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
        final DPrice other = (DPrice) obj;
        if (!Objects.equals(this.bid, other.bid)) {
            return false;
        }
        if (!Objects.equals(this.ask, other.ask)) {
            return false;
        }
        if (!Objects.equals(this.last, other.last)) {
            return false;
        }
        if (!Objects.equals(this.lastTime, other.lastTime)) {
            return false;
        }
        return true;
    }

}
