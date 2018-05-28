package ru.eamosov.revolut.utils;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimePersister extends BaseDataType {

    private static final DateTimePersister singleTon = new DateTimePersister();

    private DateTimePersister() {
        super(SqlType.DATE, new Class<?>[]{ZonedDateTime.class});
    }

    public static DateTimePersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        final ZonedDateTime dateTime = (ZonedDateTime) javaObject;
        if (dateTime == null) {
            return null;
        } else {
            return new java.sql.Timestamp(dateTime.toInstant().toEpochMilli());
        }
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Timestamp) sqlArg).getTime()), ZoneId.of("GMT"));
    }


    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        throw new SQLException("Problems with field " + fieldType + " parsing default DateTime value: " + defaultStr);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getTimestamp(columnPos);
    }

}