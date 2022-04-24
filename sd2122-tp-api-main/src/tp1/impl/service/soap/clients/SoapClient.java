package tp1.impl.service.soap.clients;

import java.util.Map;

import java.util.function.Supplier;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import tp1.api.service.soap.SoapException;
import tp1.api.service.util.Result;

import com.sun.xml.ws.client.BindingProviderProperties;

public class SoapClient {

	protected static final int REQUEST_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 10000;
	protected static final int RETRY_SLEEP = 1000;
	protected static final int MAX_RETRIES = 3;

	@SuppressWarnings("unchecked")
	public <T> T reTry(Supplier<T> func) {

		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				return func.get();
			} catch (WebServiceException wx) {
				sleep(RETRY_SLEEP);
			} catch (Exception x) {
				if (x instanceof SoapException)
					return (T) Result.error(((SoapException) x).getErrorCode());
				x.printStackTrace();
				break;
			}
		return (T) Result.error(Result.ErrorCode.REQUESTED_TIMEOUT);
	}

	public static <T> void setTimeouts(T service) {
		// Accessing protocol and associated requests / answers
		Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
		requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, REQUEST_TIMEOUT);
		requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException x) { 
		}
	}

	
	protected static Result.ErrorCode getExceptionErrorCode(Exception x) {
		switch (x.getMessage()) {
		case "NOT_FOUND":
			return Result.ErrorCode.NOT_FOUND;
		case "FORBIDDEN":
			return Result.ErrorCode.FORBIDDEN;
		case "BAD_REQUEST":
			return Result.ErrorCode.BAD_REQUEST;
		case "OK":
			return Result.ErrorCode.OK;
		case "CONFLICT":
			return Result.ErrorCode.CONFLICT;
		case "INTERNAL_ERROR":
			return Result.ErrorCode.INTERNAL_ERROR;
		case "REQUEST_TIMEOUT":
			return Result.ErrorCode.REQUESTED_TIMEOUT;
		default:
			return Result.ErrorCode.INTERNAL_ERROR;
		}
	}
	

}
