package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.SessionExpiredException;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.raw.DRawVwdPrice;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Doubles;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Non thread safe price event batch decoder.
 */
public class DVwdPriceDecoder {
    private static final DateTimeFormatter HM_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();
    private static final Set<String> SUPPORTED_FIELDS = ImmutableSet.of(
            "BidPrice",
            "AskPrice",
            "LastPrice",
            "OpenPrice",
            "HighPrice",
            "LowPrice",
            "PreviousClosePrice",

            "BidVolume",
            "AskVolume",
            "CumulativeVolume",

            // format: 21:59:58
            "LastTime",
            // Format: 2020-01-24
            "LastDate",
            //"CombinedLastDateTime",

            "FullName"
    );

    private static final Map<String, BiConsumer<String, DPrice>> MAP_FIELDS = ImmutableMap.<String, BiConsumer<String, DPrice>>builder()
            .put("BidPrice", (value, price) -> price.setBid(Doubles.tryParse(value)))
            .put("AskPrice", (value, price) -> price.setAsk(Doubles.tryParse(value)))
            .put("LastPrice", (value, price) -> price.setLast(Doubles.tryParse(value)))
            .put("OpenPrice", (value, price) -> price.setOpen(Doubles.tryParse(value)))
            .put("HighPrice", (value, price) -> price.setHigh(Doubles.tryParse(value)))
            .put("LowPrice", (value, price) -> price.setLow(Doubles.tryParse(value)))
            .put("CombinedLastDateTime", (value, price) -> price.setCombinedLastDateTime(value))
            .put("PreviousClosePrice", (value, price) -> price.setPreviousClose(Doubles.tryParse(value)))
            .put("BidVolume", (value, price) -> price.setBidVolume(Doubles.tryParse(value)))
            .put("AskVolume", (value, price) -> price.setAskVolume(Doubles.tryParse(value)))
            .put("CumulativeVolume", (value, price) -> price.setCumulativeVolume(Doubles.tryParse(value)))
            .put("LastTime", (value, price) -> price.setLastTime(value))
            .put("LastDate", (value, price) -> price.setLastDate(value))
            .put("FullName", (value, price) -> price.setFullName(value))
            .build();


    /**
     * Keep relation field subscription id (a number) and long string field name (issuer + field name)
     */
    private final Map<String, Field> fieldSubscriptionState = new HashMap<>();
    private final Map<String, DPrice> issuersState = new LinkedHashMap<>();

    public Set<String> getSupportedFields() {
        return SUPPORTED_FIELDS;
    }

    public void resetState() {
        fieldSubscriptionState.clear();
        issuersState.clear();
    }

    public List<DPrice> decode(List<DRawVwdPrice> data) throws SessionExpiredException {
        //entries will always come in order so subscription ids will always come before any value
        //if session is renewed and new subscription will occurs and ids will be replaced by new ones
        Set<String> notifiedIssuers = new LinkedHashSet<>();
        for (DRawVwdPrice dRawVwdPrice : data) {
            final String m = dRawVwdPrice.getM();
            if ("sr".equals(m)) {
                //[{"m":"sr"}]
                fieldSubscriptionState.clear(); //we don't need the state any more
                throw new SessionExpiredException("Renew session id is required");
            }
            if ("a_req".equals(m)) {
                //{"m":"a_req","v":["SPCE.BATS,E.BidPrice",30092]}
                if (dRawVwdPrice.getV().size() == 2) {
                    String firstVal = dRawVwdPrice.getV().get(0);
                    final String fieldSubscriptionId = dRawVwdPrice.getV().get(1);
                    final int lastDotIndex = firstVal.lastIndexOf(".");
                    fieldSubscriptionState.put(fieldSubscriptionId, new Field(firstVal.substring(0, lastDotIndex), firstVal.substring(lastDotIndex + 1)));
                }
            } else if (("un".equals(m) || "us".equals(m)) && dRawVwdPrice.getV().size() == 2) { //m is "un" or "us" at this point
                //{"m":"un","v":[36551,17.440000]}
                //{"m":"us","v":[30097,"2020-03-24"]}
                //{"m":"us","v":[30096,"18:53:07"]}
                final String fieldSubscriptionId = dRawVwdPrice.getV().get(0);
                final String value = dRawVwdPrice.getV().get(1);
                final Field field = fieldSubscriptionState.get(fieldSubscriptionId);

                if (field != null) {
                    final BiConsumer<String, DPrice> fieldMap = MAP_FIELDS.get(field.getField());
                    if (fieldMap != null) {
                        //always update last instance to keep field that are not reported because they did not change
                        //like date (not time)
                        fieldMap.accept(value, issuersState.computeIfAbsent(field.issuer, this::priceFactory));
                    }
                    notifiedIssuers.add(field.issuer);
                }
            }else { //[{"m":"h"}]
                //idle
            }
        }
        //this creates a clone of the object and set dateTime
        return notifiedIssuers.stream().map(issuersState::get)
                .map(this::cloneAndFixDate)
                .collect(Collectors.toList());
    }

    private DPrice cloneAndFixDate(DPrice e) {
        String combined = e.getCombinedLastDateTime();
        LocalDateTime dateTime = null;
        if(combined == null) {
            if (e.getLastDate() == null && e.getLastTime() != null) {
                final LocalTime parse1 = LocalTime.parse(e.getLastTime());
                if (parse1.isAfter(LocalTime.now())) {
                    dateTime = LocalDateTime.of(LocalDate.now().minus(1, ChronoUnit.DAYS), parse1);
                } else {
                    dateTime = LocalDateTime.of(LocalDate.now(), parse1);
                }
            }
        }
        if(combined == null && e.getLastDate() != null && e.getLastTime() != null) {
            combined = e.getLastDate() +" " + e.getLastTime();
        }
        if(dateTime == null) {
            dateTime = decodeDateTime(e, combined);
        }

        return e.withCombinedLastDateTime(combined).withLastDateTime(dateTime);
    }

    private LocalDateTime decodeDateTime(DPrice dPrice, String combined) {
        try {
            return LocalDateTime.parse(combined, HM_FORMAT);
        }catch (DateTimeParseException e) {
            DLog.DEGIRO.warn("Exception parsing date time: " + combined + " from issue " + dPrice.getIssueId());
            return null;
        }
    }

    private DPrice priceFactory(String k) {
        final DPrice dPrice = new DPrice();
        dPrice.setIssueId(k);
        return dPrice;
    }

    @Value
    static class Field {
        String issuer;
        String field;
    }
}
