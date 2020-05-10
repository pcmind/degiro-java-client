package cat.indiketa.degiro.json;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.DAlert;
import cat.indiketa.degiro.model.DCashFund;
import cat.indiketa.degiro.model.DCopyable;
import cat.indiketa.degiro.model.DHistoricalOrder;
import cat.indiketa.degiro.model.DLastTransaction;
import cat.indiketa.degiro.model.DOrder;
import cat.indiketa.degiro.model.DPortfolioProduct;
import cat.indiketa.degiro.model.DPortfolioSummary;
import cat.indiketa.degiro.model.updates.DLastUpdate;
import cat.indiketa.degiro.model.updates.DUpdate;
import cat.indiketa.degiro.model.updates.DUpdateSection;
import cat.indiketa.degiro.model.updates.DUpdateToken;
import cat.indiketa.degiro.model.updates.DUpdates;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DUpdatesDeserializer implements JsonDeserializer<DUpdates> {
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String VALUE = "value";
    public static final String IS_ADDED = "isAdded";
    public static final String IS_REMOVED = "isRemoved";
    public static final String ID = "id";

    @Override
    public DUpdates deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }
        final JsonObject updates = json.getAsJsonObject();
        /*
        TODO Implement proper model decoding and token parsing
        we do not yet know the schema of all section so not decoding will happen but request/response will have this data.
        this is done to collect data continously whyle using this client and by collecting log one can implement proper model
        decoding. Until then tokens will be collected in a single collection to avoid always requesting data from zero for the
        unknown models
        */
        final List<DUpdateToken> tokens = new ArrayList<>();
        for (Map.Entry<String, JsonElement> keySer : updates.entrySet()) {
            final DUpdateSection updateSection = DUpdateSection.valueOf(keySer.getKey());
            tokens.add(DUpdateToken.of(updateSection, keySer.getValue().getAsJsonObject().get(LAST_UPDATED).getAsLong()));
        }

        DLastUpdate<List<DUpdate<DOrder>>> decodeOrders = decodeUpdate(
                updates,
                DUpdateSection.orders,
                d -> decodeTable(d, DOrder.class, DOrder::new, context)
        );
        DLastUpdate<DUpdate<DPortfolioSummary>> totalPortfolio = decodeUpdate(
                updates,
                DUpdateSection.totalPortfolio,
                d -> decodeSingle(d, DPortfolioSummary.class, DPortfolioSummary::new, context)
        );
        DLastUpdate<List<DUpdate<DHistoricalOrder>>> historicalOrders = decodeUpdate(
                updates,
                DUpdateSection.historicalOrders,
                d -> decodeTable(d, DHistoricalOrder.class, DHistoricalOrder::new, context)
        );
        DLastUpdate<List<DUpdate<DPortfolioProduct>>> portfolio = decodeUpdate(
                updates,
                DUpdateSection.portfolio,
                d -> decodeTable(d, DPortfolioProduct.class, DPortfolioProduct::new, context)
        );
        DLastUpdate<List<DUpdate<DAlert>>> alerts = decodeUpdate(
                updates,
                DUpdateSection.alerts,
                d -> decodeTable(d, DAlert.class, DAlert::new, context)
        );
        DLastUpdate<List<DUpdate<DCashFund>>> cashFunds = decodeUpdate(
                updates,
                DUpdateSection.cashFunds,
                d -> decodeTable(d, DCashFund.class, DCashFund::new, context)
        );
        DLastUpdate<List<DUpdate<DLastTransaction>>> transactions = decodeUpdate(
                updates,
                DUpdateSection.transactions,
                d -> decodeTable(d, DLastTransaction.class, DLastTransaction::new, context)
        );
        return new DUpdates(
                tokens,
                decodeOrders,
                portfolio,
                historicalOrders,
                totalPortfolio,
                alerts,
                cashFunds,
                transactions
        );
    }

    private <T> DLastUpdate<T> decodeUpdate(
            JsonObject updates,
            DUpdateSection type,
            Function<JsonObject, T> sectionDecoder
    ) {
        final JsonElement jsonElement = updates.get(type.name());
        if (jsonElement != null && jsonElement.isJsonObject()) {
            final JsonObject section = jsonElement.getAsJsonObject();
            final int lastUpdated = section.get(LAST_UPDATED).getAsInt();
            final DUpdateToken of = DUpdateToken.of(type, lastUpdated);


            return DLastUpdate.of(of, sectionDecoder.apply(section));
        }
        return DLastUpdate.of(DUpdateToken.of(type, 0), sectionDecoder.apply(null));
    }

    private <T extends DCopyable<T>> List<DUpdate<T>> decodeTable(
            JsonObject section,
            Class<T> typeCls,
            Supplier<T> typeFactory,
            JsonDeserializationContext context) {
        if (section == null) {
            return Collections.emptyList();
        }
        final JsonElement value = section.get(VALUE);
        if (!value.isJsonArray()) {
            throw new JsonParseException("Expecting an array be an " + section.getClass() + " was received");
        }
        return StreamSupport.stream(value.getAsJsonArray().spliterator(), false)
                .map(e -> decodeRow(e, typeCls, typeFactory, context, ID))
                .collect(Collectors.toList());
    }

    /**
     * Singleton object are sent with a table intermediate object and without and id
     */
    private <T extends DCopyable<T>> DUpdate<T> decodeSingle(
            JsonElement element,
            Class<T> typeCls,
            Supplier<T> typeFactory,
            JsonDeserializationContext context) {
        //only singletone object may be
        if (element == null || !element.isJsonObject() || element.getAsJsonObject().get("name") == null) {
            return null;
        }
        return decodeRow(element, typeCls, typeFactory, context, "name");
    }

    private <T extends DCopyable<T>> DUpdate<T> decodeRow(
            JsonElement element,
            Class<T> typeCls,
            Supplier<T> typeFactory,
            JsonDeserializationContext context, String idField) {
        if (element == null || !element.isJsonObject()) {
            throw new JsonParseException("Unable to read " + typeCls + " from element " + element);
        }
        final JsonObject row = element.getAsJsonObject();
        final String id = row.get(idField).getAsString();
        boolean isAdded = getBoolean(row.get(IS_ADDED));
        boolean isRemoved = getBoolean(row.get(IS_REMOVED));

        if (isRemoved) {
            return DUpdate.ofDelete(id);
        }

        final JsonElement values = row.get(VALUE);
        if (values == null || !values.isJsonArray()) {
            throw new JsonParseException("Unable to read " + typeCls + " from element " + element);
        }

        final JsonArray asJsonArray = values.getAsJsonArray();
        final Consumer<T> tConsumer = decodeFields(typeCls, context, asJsonArray);
        if (isAdded) {
            return DUpdate.ofCreate(id, () -> {
                final T newRow = typeFactory.get();
                tConsumer.accept(newRow);
                return newRow;
            });
        } else {
            return DUpdate.ofUpdate(id, tConsumer);
        }
    }

    private <T> Consumer<T> decodeFields(Class<T> typeCls, JsonDeserializationContext context, JsonArray asJsonArray) {
        //only directly accessible methods will be mapped object type set is well know
        final Map<String, Method> methods = Stream.of(typeCls.getDeclaredMethods())
                .filter(e -> e.getParameterCount() == 1)
                .collect(Collectors.toMap(Method::getName, e -> e));
        final List<Consumer<T>> fieldMappers = new ArrayList<>();
        for (JsonElement field : asJsonArray) {
            //{name, value} pairs
            if (field.isJsonObject()) {
                this.decodeField(typeCls, context, methods, field).ifPresent(fieldMappers::add);
            }
        }
        return t -> {
            fieldMappers.forEach(f -> f.accept(t));
        };
    }

    private boolean getBoolean(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return false;
        }
        return value.getAsBoolean();
    }

    private <T> Optional<Consumer<T>> decodeField(Class<T> typeCls, JsonDeserializationContext context, Map<String, Method> methods, JsonElement element) {
        final JsonObject fieldUpdate = element.getAsJsonObject();
        final JsonElement name = fieldUpdate.get("name");
        final String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name.getAsString());
        final JsonElement value = fieldUpdate.get("value");
        final Method method = methods.get(methodName);
        if (method != null) {
            final Type type1 = method.getParameters()[0].getParameterizedType();
            final Object deserialize = context.deserialize(value, type1);
            return Optional.of(t -> map(t, method, deserialize));
        } else {
            DLog.DEGIRO.warn("Field " + name + " not found in " + typeCls.getSimpleName() + ": " + value);
        }
        return Optional.empty();
    }

    @SneakyThrows
    private <T> void map(T t, Method method, Object deserialize) {
        method.invoke(t, deserialize);
    }
}
