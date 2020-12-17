export default class PData {

	constructor(userId, slrId, slrToken, properties) {
		this.user_id = userId;
		this.slr_id = slrId;
		this.slr_token = slrToken;
		this.properties = properties ? properties : [];
		
	}

	toJsonString = () => {
		return JSON.stringify(this);
	};

}