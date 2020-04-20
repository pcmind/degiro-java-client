package cat.indiketa.degiro.utils;

import cat.indiketa.degiro.model.DOrderAction;
import cat.indiketa.degiro.model.DOrderTime;
import cat.indiketa.degiro.model.DOrderType;
import cat.indiketa.degiro.model.DProductType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author indiketa
 */
public class DUtils {

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
            if(reader.peek() == JsonToken.STRING) {
                return DOrderTime.getOrderByValue(reader.nextString());
            }else {
                int value = reader.nextInt();
                return DOrderTime.getOrderByValue(value);
            }

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
            if (reader.peek() != JsonToken.STRING) {
                //most scenario ordinal is received by when type is used in array or update it comes as a string
                int value = reader.nextInt();

                return DOrderType.getOrderByValue(value);
            } else {

                return DOrderType.getOrderByValue(reader.nextString());
            }

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

            /*e
            Degiro has two date format we need to accept.
                - Update by delta receive mostly date in the form of: 31/10 (day/month) and assume same year as now
                - Date accepted by LocalDate: 2020-03-20 (Year-month-day)
             */
            LocalDate d = null;
            try {
                final int i = value.indexOf("/");
                if(i >0) {
                    d = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(value.substring(i+1)),Integer.parseInt(value.substring(0, i)));
                }else{
                    d = LocalDate.parse(value);
                }
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

}
