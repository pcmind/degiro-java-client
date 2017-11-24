package cat.indiketa.degiro;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DCashFunds.DCashFund;
import cat.indiketa.degiro.model.DPortfolio;
import cat.indiketa.degiro.model.DPortfolio.DProductRow;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import cat.indiketa.degiro.model.raw.DRawPortfolio.Value_;
import com.google.common.base.CaseFormat;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DUtils {

    public static DPortfolio convert(DRawPortfolio rawPortfolio) {
        DPortfolio portfolio = new DPortfolio();
        List<DProductRow> active = new LinkedList<>();
        List<DProductRow> inactive = new LinkedList<>();

        for (Value value : rawPortfolio.getPortfolio().getValue()) {
            DProductRow productRow = convertProduct(value);

            if (productRow.getSize() == 0) {
                inactive.add(productRow);
            } else {
                active.add(productRow);
            }
        }

        portfolio.setActive(active);
        portfolio.setInactive(inactive);

        return portfolio;

    }

    public static DProductRow convertProduct(Value row) {
        DProductRow productRow = new DProductRow();

        for (Value_ value : row.getValue()) {

            try {

                switch (value.getName()) {
                    case "id":
                    case "size":
                    case "change":
                    case "contractSize":
                        long longValue = (long) (double) value.getValue();
                        DProductRow.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), long.class).invoke(productRow, longValue);
                        break;
                    case "product":
                    case "currency":
                    case "exchangeBriefCode":
                    case "productCategory":
                        String stringValue = (String) value.getValue();
                        DProductRow.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), String.class).invoke(productRow, stringValue);
                        break;
                    case "lastUpdate":
                        System.err.println(value.getValue());
                        break;
                    case "closedToday":
                    case "tradable":
                        Boolean booleanValue = (Boolean) value.getValue();
                        DProductRow.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), boolean.class).invoke(productRow, booleanValue);
                        break;
                    case "price":
                    case "value":
                    case "closePrice":
                        BigDecimal bdValue = new BigDecimal((double) value.getValue());
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DProductRow.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), BigDecimal.class).invoke(productRow, bdValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of portfolio", e);
            }
        }

        return productRow;
    }

    public static DCashFunds convert(DRawCashFunds rawCashFunds) {
        DCashFunds cashFunds = new DCashFunds();
        List<DCashFund> list = new LinkedList<>();

        for (Value value : rawCashFunds.getCashFunds().getValue()) {
            DCashFund cashFund = convertCash(value);
            list.add(cashFund);
        }

        cashFunds.setCashFunds(list);

        return cashFunds;

    }

    public static DCashFund convertCash(Value row) {

        DCashFund cashFund = new DCashFund();

        for (Value_ value : row.getValue()) {

            try {

                switch (value.getName()) {
                    case "id":
                        int intValue = (int) (double) value.getValue();
                        DCashFund.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), int.class).invoke(cashFund, intValue);
                        break;
                    case "currencyCode":
                        String stringValue = (String) value.getValue();
                        DCashFund.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), String.class).invoke(cashFund, stringValue);
                        break;
                    case "lastUpdate":
                        System.err.println(value.getValue());
                        break;
                    case "value":
                        BigDecimal bdValue = new BigDecimal((double) value.getValue());
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DCashFund.class.getMethod("set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName()), BigDecimal.class).invoke(cashFund, bdValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of cash fund", e);
            }

        }
        return cashFund;
    }

}
