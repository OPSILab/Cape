package it.eng.opsi.cape.sdk.repository;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
	@Override
	public ZonedDateTime convert(Date date) {
		return date.toInstant().atZone(ZoneOffset.UTC);
	}
}