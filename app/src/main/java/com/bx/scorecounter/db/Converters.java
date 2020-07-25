package com.bx.scorecounter.db;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static long dateTime2unix(ZonedDateTime dateTime) {
        return dateTime.toEpochSecond();
    }

    @TypeConverter
    public static ZonedDateTime unix2DateTime(long unix) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(unix), ZoneId.systemDefault());
    }

    @TypeConverter
    public static int style2int(ScoreData.Style style) {
        return style.ordinal();
    }

    @TypeConverter
    public static ScoreData.Style int2Style(int ordinal) {
        final ScoreData.Style[] styles = ScoreData.Style.values();
        if (ordinal < styles.length) {
            return styles[ordinal];
        } else {
            return styles[0];
        }
    }

    @TypeConverter
    public static int color2Int(ScoreData.Color color) {
        return color.ordinal();
    }

    @TypeConverter
    public static ScoreData.Color int2Color(int ordinal) {
        final ScoreData.Color[] colors = ScoreData.Color.values();
        if (ordinal < colors.length) {
            return colors[ordinal];
        } else {
            return colors[0];
        }
    }

    private Converters() {}
}
