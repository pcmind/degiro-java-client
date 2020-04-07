package cat.indiketa.degiro;

import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DProductType;
import cat.indiketa.degiro.utils.DUtils;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DJsonDecoder {
    private final Gson gson;

    public DJsonDecoder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DProductType.class, new DUtils.ProductTypeAdapter());
        builder.registerTypeAdapter(DOrderType.class, new DUtils.OrderTypeListTypeAdapter());
        builder.registerTypeAdapter(DOrderTime.class, new DUtils.OrderTimeTypeAdapter());
        builder.registerTypeAdapter(new TypeToken<List<DOrderTime>>() {
        }.getType(), new DUtils.OrderTimeListTypeAdapter());
        builder.registerTypeAdapter(new TypeToken<List<DOrderType>>() {
        }.getType(), new DUtils.OrderTypeListTypeAdapter());
        builder.registerTypeAdapter(DOrderType.class, new DUtils.OrderTypeTypeAdapter());
        builder.registerTypeAdapter(DOrderAction.class, new DUtils.OrderActionTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new DUtils.CalendarTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DUtils.DateTypeAdapter());
        this.gson = builder.create();
    }

    public <T> T fromJson(String json, Type classOfT) throws IOException {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
           throw new IOException("Unable to decode: "+json+" for Type " + classOfT, e);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws IOException {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            throw new IOException("Unable to decode: "+json+" for class " + classOfT, e);
        }
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(JsonElement json, Class<T> classOfT) throws IOException {
        return gson.fromJson(json, classOfT);
    }

    public <T> T fromJsonData(String json, Type cls) throws IOException {
        return fromJsonField("data", json, cls);
    }

    public <T> T fromJsonData(String json, Class<T> cls) throws IOException {
        return fromJsonField("data", json, cls);
    }

    public <T> T fromJsonField(String field, String json, Type cls) throws IOException {
        if (json == null) {
            return null;
        }
        try {
            StringReader reader = new StringReader(json);
            JsonReader jsonReader = gson.newJsonReader(reader);
            jsonReader.beginObject();
            final String s = jsonReader.nextName();
            Preconditions.checkState(s.equals(field));
            T target = gson.fromJson(jsonReader, cls);
            jsonReader.endObject();
            return target;
        } catch (Exception e) {
            throw new IOException("Unable to decode: "+json+" for class " + cls, e);
        }
    }
}
