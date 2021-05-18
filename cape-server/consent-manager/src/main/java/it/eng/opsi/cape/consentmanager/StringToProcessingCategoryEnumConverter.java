package it.eng.opsi.cape.consentmanager;

import org.springframework.core.convert.converter.Converter;

import it.eng.opsi.cape.serviceregistry.data.ProcessingCategory;

@RequestParameterConverter
public class StringToProcessingCategoryEnumConverter implements Converter<String, ProcessingCategory> {

	@Override
	public ProcessingCategory convert(String source) {
		return ProcessingCategory.fromValue(source);
	}

}
