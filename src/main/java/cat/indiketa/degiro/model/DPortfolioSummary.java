package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author indiketa
 */
public class DPortfolioSummary {

    private BigDecimal portVal;
    private BigDecimal cash;
    private BigDecimal total;
    private BigDecimal pl;
    private BigDecimal plToday;
    private BigDecimal freeSpace;
    private BigDecimal reportFreeRuimte;
    private BigDecimal reportMargin;
    private Date reportCreationTime;
    private BigDecimal reportPortfValue;
    private BigDecimal reportCashBal;
    private BigDecimal reportNetliq;
    private BigDecimal reportOverallMargin;
    private BigDecimal reportTotalLongVal;
    private BigDecimal reportDeficit;

    public BigDecimal getPortVal() {
        return portVal;
    }

    public void setPortVal(BigDecimal portVal) {
        this.portVal = portVal;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPl() {
        return pl;
    }

    public void setPl(BigDecimal pl) {
        this.pl = pl;
    }

    public BigDecimal getPlToday() {
        return plToday;
    }

    public void setPlToday(BigDecimal plToday) {
        this.plToday = plToday;
    }

    public BigDecimal getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(BigDecimal freeSpace) {
        this.freeSpace = freeSpace;
    }

    public BigDecimal getReportFreeRuimte() {
        return reportFreeRuimte;
    }

    public void setReportFreeRuimte(BigDecimal reportFreeRuimte) {
        this.reportFreeRuimte = reportFreeRuimte;
    }

    public BigDecimal getReportMargin() {
        return reportMargin;
    }

    public void setReportMargin(BigDecimal reportMargin) {
        this.reportMargin = reportMargin;
    }

    public Date getReportCreationTime() {
        return reportCreationTime;
    }

    public void setReportCreationTime(Date reportCreationTime) {
        this.reportCreationTime = reportCreationTime;
    }

    public BigDecimal getReportPortfValue() {
        return reportPortfValue;
    }

    public void setReportPortfValue(BigDecimal reportPortfValue) {
        this.reportPortfValue = reportPortfValue;
    }

    public BigDecimal getReportCashBal() {
        return reportCashBal;
    }

    public void setReportCashBal(BigDecimal reportCashBal) {
        this.reportCashBal = reportCashBal;
    }

    public BigDecimal getReportNetliq() {
        return reportNetliq;
    }

    public void setReportNetliq(BigDecimal reportNetliq) {
        this.reportNetliq = reportNetliq;
    }

    public BigDecimal getReportOverallMargin() {
        return reportOverallMargin;
    }

    public void setReportOverallMargin(BigDecimal reportOverallMargin) {
        this.reportOverallMargin = reportOverallMargin;
    }

    public BigDecimal getReportTotalLongVal() {
        return reportTotalLongVal;
    }

    public void setReportTotalLongVal(BigDecimal reportTotalLongVal) {
        this.reportTotalLongVal = reportTotalLongVal;
    }

    public BigDecimal getReportDeficit() {
        return reportDeficit;
    }

    public void setReportDeficit(BigDecimal reportDeficit) {
        this.reportDeficit = reportDeficit;
    }

    
    
}
