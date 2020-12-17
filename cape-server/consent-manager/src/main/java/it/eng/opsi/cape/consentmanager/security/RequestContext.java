package it.eng.opsi.cape.consentmanager.security;

public class RequestContext {

	public static final String REQUEST_HEADER_NAME = "Authorization";

	private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

	private String token;

	public static RequestContext getContext() {
		RequestContext result = CONTEXT.get();

		if (result == null) {
			result = new RequestContext();
			CONTEXT.set(result);
		}

		return result;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
