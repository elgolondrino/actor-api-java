package im.actor.torlib.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import im.actor.torlib.errors.TorParsingException;

public class Timestamp {
    private final Date date;

    public Timestamp(Date date) {
        this.date = date;
    }

    public long getTime() {
        return date.getTime();
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public boolean hasPassed() {
        final Date now = new Date();
        return date.before(now);
    }

    public String toString() {
        return date.toString();
    }
}
