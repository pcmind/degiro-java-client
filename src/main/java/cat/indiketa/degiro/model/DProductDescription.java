package cat.indiketa.degiro.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 *
 * @author indiketa
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DProductDescription {

    private String feedQuality;
    private long orderBookDepth;
    private String vwdIdentifierType;
    private String vwdId;
    private boolean qualitySwitchable;
    private boolean qualitySwitchFree;
    private long vwdModuleId;
    private String id;
    private String name;
    @EqualsAndHashCode.Include
    private String isin;
    private double contractSize;
    private String exchangeId;
    private String symbol;
    private DProductType productTypeId;
    private boolean tradable;
    private List<DOrderTime> orderTimeTypes = null;
    private boolean gtcAllowed;
    private List<DOrderType> buyOrderTypes = null;
    private List<DOrderType> sellOrderTypes = null;
    private boolean marketAllowed;
    private boolean limitHitOrderAllowed;
    private boolean stoplossAllowed;
    private boolean stopLimitOrderAllowed;
    private boolean joinOrderAllowed;
    private boolean trailingStopOrderAllowed;
    private boolean combinedOrderAllowed;
    private boolean sellAmountAllowed;
    private boolean isFund;
    private double closePrice;
    private Date closePriceDate;
    private String category;
    private String currency;

}
