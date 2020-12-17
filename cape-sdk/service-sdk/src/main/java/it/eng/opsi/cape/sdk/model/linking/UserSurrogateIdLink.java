package it.eng.opsi.cape.sdk.model.linking;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("user2surrogate")
@CompoundIndex(unique = true, def = "{ 'userId': 1, 'surrogateId': 1}")
public class UserSurrogateIdLink {

	@Id
	@JsonIgnore
	private ObjectId _id;

	@NotBlank(message = "userId field is mandatory")
	private String userId;

	@NotBlank(message = "surrogateId field is mandatory")
	@Indexed(unique = true)
	private String surrogateId;

	@NotBlank(message = "serviceId field is mandatory")
	private String serviceId;

	@NotBlank(message = "operatorId field is mandatory")
	private String operatorId;
	
	private ZonedDateTime created;

}
