package ru.eamosov.revolut.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * Created by fluder on 27/05/2018.
 */
public class ZonedDateTimeType extends TypeAdapter<ZonedDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, ZonedDateTime zonedDateTime) throws IOException {
        jsonWriter.value(zonedDateTime.toString());
    }

    @Override
    public ZonedDateTime read(JsonReader jsonReader) throws IOException {
        return ZonedDateTime.parse(jsonReader.nextString());
    }
}
