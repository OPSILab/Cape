package it.eng.opsi.cape.auditlogmanager;

import org.springframework.core.convert.converter.Converter;

import it.eng.opsi.cape.serviceregistry.data.ProcessingBasis.LegalBasis;

@RequestParameterConverter
public class StringToLegalBasisEnumConverter implements Converter<String, LegalBasis> {

	@Override
	public LegalBasis convert(String source) {
		return LegalBasis.fromValue(source);
	}

}
