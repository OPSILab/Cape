package it.eng.opsi.cape.sdk.repository;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
	@Override
	public Date convert(ZonedDateTime zonedDateTime) {
		return Date.from(zonedDateTime.toInstant());
	}
}