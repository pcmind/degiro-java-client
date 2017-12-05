package cat.indiketa.degiro.model;

import java.util.Date;

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

}
