package it.eng.opsi.cape.serviceregistry.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceReportGroupingCriteria {

	TYPE("type"), SECTOR("sector");

	private final String text;

	private ServiceReportGroupingCriteria(final String text) {
		this.text = text;
	}

	@JsonValue
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}
}
