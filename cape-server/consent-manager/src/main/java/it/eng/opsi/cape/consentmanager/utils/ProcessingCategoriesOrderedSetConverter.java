package it.eng.opsi.cape.consentmanager.utils;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;

import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

public class ProcessingCategoriesOrderedSetConverter extends StdConverter<Set<ProcessingCategory>, Set<ProcessingCategory>> {
	@Override
	public Set<ProcessingCategory> convert(Set<ProcessingCategory> value) {
		return value == null ? null
				: value.stream().sorted(Comparator.nullsLast(Comparator.naturalOrder()))
						.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}