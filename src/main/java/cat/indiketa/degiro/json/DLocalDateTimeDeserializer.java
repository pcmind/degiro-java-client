package cat.indiketa.degiro.json;

import com.google.common.base.Strings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DLocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || !json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            return null;
        }
        String date = Strings.nullToEmpty(json.getAsJsonPrimitive().getAsString());
        LocalDateTime parsed = null;
        /*
            Some API field receive a date or a time but no both .
                - Date: 31/10 (day/month) and assume same year as now
                - Time: 14:57 (hour:minute) and assume date as now

            In the case Degiro send the full date one should use OffsetDateTime instead of LocalDateTime
         */
        if (date.contains(":")) {
            final String[] split = date.split(":");
            parsed = LocalDateTime.of(
                    LocalDate.now(),
                    LocalTime.of(
                            Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0
                    )
            );
        } else if (date.contains("/")) {
            final String[] split = date.split("/");
            parsed = LocalDateTime.of(
                    LocalDate.of(
                            LocalDate.now().getYear(),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[0])
                    ),
                    LocalTime.now()
            );

        } else {
            throw new JsonParseException("Unknow date/time format: " + date);
        }
        return parsed;
    }
}
