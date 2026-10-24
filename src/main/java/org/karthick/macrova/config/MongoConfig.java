package org.karthick.macrova.config;

import java.time.*;
import java.util.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.NonNull;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Override
  protected @NonNull String getDatabaseName() {
    return "macrova";
  }

  @Override
  public @NonNull MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new LocalDateToDateConverter());
    converters.add(new DateToLocalDateConverter());
    return new MongoCustomConversions(converters);
  }

  static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Date convert(@NonNull LocalDate source) {
      return Date.from(source.atStartOfDay(ZoneOffset.UTC).toInstant());
    }
  }

  static class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(@NonNull Date source) {
      return source.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
  }
}
