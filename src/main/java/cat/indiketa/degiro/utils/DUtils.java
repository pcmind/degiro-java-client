package cat.indiketa.degiro.utils;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DAlert;
import cat.indiketa.degiro.model.DCashFunds;
import cat.indiketa.degiro.model.DCashFunds.DCashFund;
import cat.indiketa.degiro.model.DLastTransactions;
import cat.indiketa.degiro.model.DLastTransactions.DTransaction;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DPortfolioProduct;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.model.DUpdate;
import cat.indiketa.degiro.model.DUpdates;
import cat.indiketa.degiro.model.raw.DFieldValue;
import cat.indiketa.degiro.model.raw.DRawAlerts;
import cat.indiketa.degiro.model.raw.DRawCashFunds;
import cat.indiketa.degiro.model.raw.DRawOrders;
import cat.indiketa.degiro.model.raw.DRawPortfolio;
import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import cat.indiketa.degiro.model.raw.DRawPortfolioSummary;
import cat.indiketa.degiro.model.raw.DRawTransactions;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author indiketa
 */
public class DUtils {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static DUpdates.DLastUpdate<List<DUpdate<DPortfolioProduct, String>>> convert(DRawPortfolio rawPortfolio) {
        final List<DUpdate<DPortfolioProduct, String>> updates = new ArrayList<>();
        final DRawPortfolio.Portfolio portfolio1 = rawPortfolio.getPortfolio();
        for (Value value : portfolio1.getValue()) {
            if (value.getIsRemoved()) {
                updates.add(DUpdate.ofDelete(value.getId()));
            } else {
                if (value.getIsAdded()) {
                    updates.add(DUpdate.ofCreate(value.getId(), () -> convertProduct(value)));
                } else {
                    updates.add(DUpdate.ofUpdate(value.getId(), r -> applyChangesToPortfolioProduct(value, r)));
                }
            }
        }
        return DUpdates.DLastUpdate.of(portfolio1.getLastUpdated(), updates);

    }

    public static DUpdates.DLastUpdate<DUpdate<DPortfolioSummary, String>> convertPortfolioSummary(DRawPortfolioSummary.TotalPortfolio row) {
        if (row.getValue() == null || row.getValue().isEmpty()) {
            return DUpdates.DLastUpdate.of(row.getLastUpdated(), null);
        }
        if (row.getIsAdded()) {
            return DUpdates.DLastUpdate.of(row.getLastUpdated(), DUpdate.ofCreate("totalPortfolio", () -> {
                DPortfolioSummary portfolioSummary = new DPortfolioSummary();
                applyChangesToPortfolioSummary(row, portfolioSummary);
                return portfolioSummary;
            }));
        }else {
            return DUpdates.DLastUpdate.of(row.getLastUpdated(), DUpdate.ofUpdate("totalPortfolio", portfolioSummary -> {
                applyChangesToPortfolioSummary(row, portfolioSummary);
            }));
        }
    }

    private static void applyChangesToPortfolioSummary(DRawPortfolioSummary.TotalPortfolio row, DPortfolioSummary portfolioSummary) {

        for (DFieldValue value : row.getValue()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "cashFundCompensationCurrency":
                        final String value1 = (String) value.getValue();
                        DPortfolioSummary.class.getMethod(methodName, String.class).invoke(portfolioSummary, value1);
                        break;
                    case "freeSpaceNew":
                        final HashMap<String, BigDecimal> freeSpaceNew = new HashMap<>();
                        for (Map.Entry<String, Double> stringDoubleEntry : ((Map<String, Double>) value.getValue())
                                .entrySet()) {
                            freeSpaceNew.put(stringDoubleEntry.getKey(), getBigDecimal(stringDoubleEntry.getValue()));
                        }
                        portfolioSummary.setFreeSpaceNew(freeSpaceNew);
                        break;
                    case "cash":
                    case "cashFundCompensation":
                    case "cashFundCompensationWithdrawn":
                    case "cashFundCompensationPending":
                    case "todayNonProductFees":
                    case "totalNonProductFees":
                        BigDecimal bdValue = getBigDecimal(value.getValue());
                        DPortfolioSummary.class.getMethod(methodName, BigDecimal.class).invoke(portfolioSummary, bdValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.DEGIRO.error("Error while setting value of portfolioSummary." + value.getName(), e);
            }
        }
    }

    private static BigDecimal getBigDecimal(Object value1) {
        BigDecimal bdValue = new BigDecimal((double) value1);
        if (bdValue.scale() > 4) {
            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
        }
        return bdValue;
    }

    public static DPortfolioProduct convertProduct(Value row) {
        DPortfolioProduct productRow = new DPortfolioProduct();

        applyChangesToPortfolioProduct(row, productRow);

        return productRow;
    }

    private static void applyChangesToPortfolioProduct(Value row, DPortfolioProduct productRow) {
        for (DFieldValue value : row.getValue()) {

            try {
                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "size":
                    case "averageFxRate":
                        long longValue = Double.valueOf(value.getValue().toString()).longValue();
                        DPortfolioProduct.class.getMethod(methodName, long.class).invoke(productRow, longValue);
                        break;
                    case "id":
                    case "positionType":
                    case "accruedInteres":
                        String stringValue = (String) value.getValue();
                        DPortfolioProduct.class.getMethod(methodName, String.class).invoke(productRow, stringValue);
                        break;
                    case "lastUpdate":
                        break;
                    case "price":
                    case "value":
                    case "breakEvenPrice":
                    case "portfolioValueCorrection":
                    case "realizedProductPl":
                    case "realizedFxPl":
                    case "todayRealizedProductPl":
                    case "todayRealizedFxPl":
                        BigDecimal bdValue = new BigDecimal((double) value.getValue());
                        if (!value.getName().equals("change") && bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DPortfolioProduct.class.getMethod(methodName, BigDecimal.class).invoke(productRow, bdValue);
                        break;
                    case "todayPlBase":
                    case "plBase":
                        Map<String, ?> values = (Map<String, ?>) value.getValue();
                        final HashMap<String, BigDecimal> map = new HashMap<>();
                        values.forEach((k, v) -> map.put(k, new BigDecimal(v.toString())));
                        DPortfolioProduct.class.getMethod(methodName, Map.class).invoke(productRow, values);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.DEGIRO.error("Error while setting value of portfolio", e);
            }
        }
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

        for (DFieldValue value : row.getValue()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "id":
                        int intValue = (int) (double) value.getValue();
                        DCashFund.class.getMethod(methodName, int.class).invoke(cashFund, intValue);
                        break;
                    case "currencyCode":
                        String stringValue = (String) value.getValue();
                        DCashFund.class.getMethod(methodName, String.class).invoke(cashFund, stringValue);
                        break;
                    case "value":
                        BigDecimal bdValue = getBigDecimal(value.getValue());
                        DCashFund.class.getMethod(methodName, BigDecimal.class).invoke(cashFund, bdValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.DEGIRO.error("Error while setting value of cash fund", e);
            }

        }
        return cashFund;
    }

    public static DUpdates.DLastUpdate<List<DUpdate<DOrder, String>>> convert(DRawOrders rawOrders) {

        final List<DUpdate<DOrder, String>> updates = new ArrayList<>();

        for (DRawOrders.Value value : rawOrders.getOrders().getValue()) {
            if (value.getIsRemoved()) {
                updates.add(DUpdate.ofDelete(value.getId()));
            } else {
                DOrder order = convertOrder(value);
                if (value.getIsAdded()) {
                    updates.add(DUpdate.ofCreate(value.getId(), () -> convertOrder(value)));
                } else {
                    updates.add(DUpdate.ofUpdate(value.getId(), previ -> applyChangesToOrder(value, previ)));
                }
            }
        }
        return DUpdates.DLastUpdate.of(rawOrders.orders.lastUpdated, updates);

    }

    public static DOrder convertOrder(DRawOrders.Value row) {

        DOrder order = new DOrder();
        applyChangesToOrder(row, order);
        return order;
    }

    private static void applyChangesToOrder(DRawOrders.Value row, DOrder order) {
        for (DFieldValue value : row.getValue()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "contractType":
                    case "contractSize":
                        int intValue = (int) (double) value.getValue();
                        DOrder.class.getMethod(methodName, int.class).invoke(order, intValue);
                        break;
                    case "productId":
                    case "size":
                    case "quantity":
                        long longValue = (long) (double) value.getValue();
                        DOrder.class.getMethod(methodName, long.class).invoke(order, longValue);
                        break;
                    case "id":
                    case "product":
                    case "currency":
                        String stringValue = (String) value.getValue();
                        DOrder.class.getMethod(methodName, String.class).invoke(order, stringValue);
                        break;
                    case "buysell":
                        String stringValue2 = (String) value.getValue();
                        order.setBuySell(DOrderAction.getOrderByValue(stringValue2));
                        break;

                    case "date":
                        Calendar calendar = processDate((String) value.getValue());
                        DOrder.class.getMethod(methodName, Calendar.class).invoke(order, calendar);
                        break;
                    case "orderTypeId":
                        order.setOrderType(DOrderType.getOrderByValue((int) (double) value.getValue()));
                        break;
                    case "orderTimeTypeId":
                        order.setOrderTime(DOrderTime.getOrderByValue((int) (double) value.getValue()));
                        break;
                    case "price":
                    case "stopPrice":
                    case "totalOrderValue":
                        BigDecimal bdValue = getBigDecimal(value.getValue());
                        DOrder.class.getMethod(methodName, BigDecimal.class).invoke(order, bdValue);
                        break;

                    case "isModifiable":
                    case "isDeletable":
                        Boolean booleanValue = (Boolean) value.getValue();
                        DOrder.class.getMethod(methodName, boolean.class).invoke(order, booleanValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.DEGIRO.error("Error while setting value of order", e);
            }

        }
    }

    private static Calendar processDate(String date) {
        Calendar parsed = null;

        date = Strings.nullToEmpty(date);

        if (date.contains(":")) {
            parsed = Calendar.getInstance();
            parsed.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date.split(":")[0]));
            parsed.set(Calendar.MINUTE, Integer.parseInt(date.split(":")[1]));
            parsed.set(Calendar.SECOND, 0);
            parsed.set(Calendar.MILLISECOND, 0);
        } else if (date.contains("/")) {
            parsed = Calendar.getInstance();
            int month = Integer.parseInt(date.split("/")[1]) - 1;

            if (parsed.get(Calendar.MONTH) < month) {
                parsed.add(Calendar.YEAR, -1);
            }

            parsed.set(Calendar.MONTH, month);
            parsed.set(Calendar.DATE, Integer.parseInt(date.split("/")[0]));
            parsed.set(Calendar.HOUR_OF_DAY, 0);
            parsed.set(Calendar.MINUTE, 0);
            parsed.set(Calendar.SECOND, 0);
            parsed.set(Calendar.MILLISECOND, 0);

        } else {
        }
        return parsed;
    }

    public static DLastTransactions convert(DRawTransactions rawOrders) {
        DLastTransactions transactions = new DLastTransactions();
        List<DTransaction> list = new LinkedList<>();

        for (Value value : rawOrders.getTransactions().getValue()) {
            DTransaction order = convertTransaction(value);
            list.add(order);
        }

        transactions.setTransactions(list);

        return transactions;

    }

    public static DTransaction convertTransaction(Value row) {

        DTransaction transaction = new DTransaction();

        for (DFieldValue value : row.getValue()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "contractType":
                    case "contractSize":
                        int intValue = (int) (double) value.getValue();
                        DTransaction.class.getMethod(methodName, int.class).invoke(transaction, intValue);
                        break;
                    case "productId":
                    case "size":
                    case "quantity":
                    case "id":
                        long longValue = (long) (double) value.getValue();
                        DTransaction.class.getMethod(methodName, long.class).invoke(transaction, longValue);
                        break;
                    case "product":
                    case "currency":
                        String stringValue = (String) value.getValue();
                        DTransaction.class.getMethod(methodName, String.class).invoke(transaction, stringValue);
                        break;
                    case "buysell":
                        String stringValue2 = (String) value.getValue();
                        transaction.setBuysell(DOrderAction.getOrderByValue(stringValue2));
                        break;

                    case "date":
                        Calendar calendar = processDate((String) value.getValue());
                        DTransaction.class.getMethod(methodName, Calendar.class).invoke(transaction, calendar);
                        break;
                    case "orderType":
                        transaction.setOrderType(DOrderType.getOrderByValue((int) (double) value.getValue()));
                        break;
                    case "orderTime":
                        transaction.setOrderTime(DOrderTime.getOrderByValue((int) (double) value.getValue()));
                        break;
                    case "price":
                    case "stopPrice":
                    case "totalOrderValue":
                        BigDecimal bdValue = getBigDecimal(value.getValue());
                        DTransaction.class.getMethod(methodName, BigDecimal.class).invoke(transaction, bdValue);
                        break;

                    case "isModifiable":
                    case "isDeletable":
                        Boolean booleanValue = (Boolean) value.getValue();
                        DTransaction.class.getMethod(methodName, boolean.class).invoke(transaction, booleanValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.DEGIRO.error("Error while setting value of order", e);
            }

        }
        return transaction;
    }

    public static DUpdates.DLastUpdate<List<DUpdate<DAlert, String>>> convert(DRawAlerts dRawAlerts) {
        final List<DUpdate<DAlert, String>> updates = new ArrayList<>();
        final DRawAlerts.Alerts alerts = dRawAlerts.getAlerts();
        for (Value value : alerts.getValue()) {
            if (value.getIsRemoved()) {
                updates.add(DUpdate.ofDelete(value.getId()));
            } else {
                if (value.getIsAdded()) {
                    updates.add(DUpdate.ofCreate(value.getId(), () -> convertAlert(value)));
                } else {
                    updates.add(DUpdate.ofUpdate(value.getId(), r -> applyChangesToAlert(value, r)));
                }
            }
        }
        return DUpdates.DLastUpdate.of(alerts.getLastUpdated(), updates);
    }

    private static void applyChangesToAlert(Value row, DAlert r) {
        for (DFieldValue value : row.getValue()) {
            switch (value.getName()) {
                case "id":
                    r.setId((long) (double) value.getValue());
                    return;
                case "text":
                    r.setText(((String) value.getValue()));
            }
        }
    }

    private static DAlert convertAlert(Value value) {
        final DAlert r = new DAlert();
        applyChangesToAlert(value, r);
        return r;
    }

    public static class ProductTypeAdapter extends TypeAdapter<DProductType> {

        @Override
        public DProductType read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            int value = reader.nextInt();

            return DProductType.getProductTypeByValue(value);

        }

        @Override
        public void write(JsonWriter writer, DProductType value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getTypeCode());
        }
    }

    public static class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal> {

        @Override
        public BigDecimal read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            BigDecimal bd = null;

            if (!Strings.isNullOrEmpty(value)) {
                bd = new BigDecimal(value);
            }

            return bd;

        }

        @Override
        public void write(JsonWriter writer, BigDecimal value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.toPlainString());
        }
    }
    public static class OrderTimeListTypeAdapter extends TypeAdapter<List<DOrderTime>> {

        @Override
        public List<DOrderTime> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            List<DOrderTime> list = new ArrayList<>();
            in.beginArray();
            while (in.hasNext()) {
                final String s = in.nextString();
                list.add(DOrderTime.getOrderByValue(s));
            }
            in.endArray();
            return list;
        }

        @Override
        public void write(JsonWriter out, List<DOrderTime> list) throws IOException {
            if (list == null) {
                out.nullValue();
                return;
            }

            out.beginArray();
            for (DOrderTime dOrderTime : list) {
                out.value(dOrderTime.getStrValue());
            }
            out.endArray();
        }
    }

    public static class OrderTimeTypeAdapter extends TypeAdapter<DOrderTime> {

        @Override
        public DOrderTime read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            //only list are coming from string other values are coming by name
            int value = reader.nextInt();

            return DOrderTime.getOrderByValue(value);

        }

        @Override
        public void write(JsonWriter writer, DOrderTime value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }
    public static class OrderTypeListTypeAdapter extends TypeAdapter<List<DOrderType>> {

        @Override
        public void write(JsonWriter out, List<DOrderType> list) throws IOException {
            if (list == null) {
                out.nullValue();
                return;
            }

            out.beginArray();
            for (DOrderType dOrderType : list) {
                out.value(dOrderType.getStrValue());
            }
            out.endArray();
        }

        @Override
        public List<DOrderType> read(JsonReader in) throws IOException {
            final JsonToken p = in.peek();
            if (p == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            List<DOrderType> list = new ArrayList<DOrderType>();
            in.beginArray();
            while (in.hasNext()) {
                final String s = in.nextString();
                list.add(DOrderType.getOrderByValue(s));
            }
            in.endArray();
            return list;
        }
    }
    public static class OrderTypeTypeAdapter extends TypeAdapter<DOrderType> {

        @Override
        public DOrderType read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            /*
            type when read from remote is always a number. when present in an array like DOrderType[] it is always by name
            but a distinct serializer will be used
            */
            int value = reader.nextInt();

            return DOrderType.getOrderByValue(value);

        }

        @Override
        public void write(JsonWriter writer, DOrderType value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class OrderActionTypeAdapter extends TypeAdapter<DOrderAction> {

        @Override
        public DOrderAction read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            return DOrderAction.getOrderByValue(value);

        }

        @Override
        public void write(JsonWriter writer, DOrderAction value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter writer, LocalDate value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value.toString());
        }

        @Override
        public LocalDate read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            LocalDate d = null;
            try {
                d = LocalDate.parse(value);
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("Date not parseable: " + value, e);
            }
            return d;
        }
    }
    public static class OffsetDateTimeTypeAdapter extends TypeAdapter<OffsetDateTime> {

        @Override
        public void write(JsonWriter writer, OffsetDateTime value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value.toString());
        }

        @Override
        public OffsetDateTime read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            OffsetDateTime d = null;
            try {
                d = OffsetDateTime.parse(value);
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("Date not parseable: " + value, e);
            }
            return d;
        }
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("\\d+");
    }

    public static class CalendarTypeAdapter extends TypeAdapter<Calendar> {
        @Override
        public void write(JsonWriter writer, Calendar value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(DATE_TIME_FORMAT.format(value));
        }

        @Override
        public Calendar read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            Calendar d = null;
            try {
                final Date parse = DATE_TIME_FORMAT.parse(value);
                final Calendar instance = Calendar.getInstance();
                instance.setTime(parse);
                return instance;
            } catch (ParseException e) {
                DLog.DEGIRO.warn("DateTime not parseable: " + value);
            }
            return d;
        }
    }
}
