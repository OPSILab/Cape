package it.eng.opsi.cape.sdk;

import org.springframework.core.convert.converter.Converter;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.PurposeCategory;

@RequestParameterConverter
public class StringToPurposeCategoryEnumConverter implements Converter<String, PurposeCategory> {

	@Override
	public PurposeCategory convert(String source) {
		return PurposeCategory.fromValue(source);
	}

}
