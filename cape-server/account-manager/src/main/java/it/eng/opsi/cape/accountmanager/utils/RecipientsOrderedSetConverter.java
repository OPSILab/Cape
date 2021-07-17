package it.eng.opsi.cape.accountmanager.utils;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;

import it.eng.opsi.cape.serviceregistry.data.Recipient;

public class RecipientsOrderedSetConverter extends StdConverter<Set<Recipient>, Set<Recipient>> {
	@Override
	public Set<Recipient> convert(Set<Recipient> value) {
		return value == null ? null
				: value.stream().sorted(Comparator.nullsLast(Comparator.naturalOrder()))
						.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}