/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package cat.eduard.degiro;

import cat.eduard.degiro.log.DLog;
import cat.eduard.degiro.model.DPortfolio;
import cat.eduard.degiro.model.DPortfolio.DProductRow;
import cat.eduard.degiro.model.raw.DRawPortfolio;
import cat.eduard.degiro.model.raw.DRawPortfolio.Value;
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
            DProductRow productRow = convert(value);

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

    public static DProductRow convert(Value row) {
        DProductRow productRow = new DProductRow();

        for (DRawPortfolio.Value_ value : row.getValue()) {

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
                        if (bdValue.scale() >4) {
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

}
