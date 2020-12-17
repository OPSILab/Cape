package it.eng.opsi.cape.consentmanager.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = ConsentRecordSinkRoleSpecificPart.class, name = "sink"),
		@Type(value = ConsentRecordSourceRoleSpecificPart.class, name = "source") })
public abstract class ConsentRecordRoleSpecificPart {

}
