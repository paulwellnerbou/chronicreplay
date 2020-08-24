package de.wellnerbou.chronic.logparser;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeFormatterTest {

    static {
        Locale.setDefault(new Locale("en", "EN"));
    }
    static final DateTimeFormatter format = DateTimeFormat.forPattern("[dd/MMM/yyyy:HH:mm:ss");

    @Test
    public void testDateTimeFormat() {
        //assertThat(Locale.getDefault().getLanguage()).isEqualTo("de");
        //assertThat(Locale.getDefault().getCountry()).isEqualTo("DE");
        assertThat(format.parseMillis("[02/Jan/2014:11:55:12")).isEqualTo(1388660112000L);
    }
}