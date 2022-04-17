package tp1.impl.service.soap.clients;

import java.util.Map;
import jakarta.ws.rs.ProcessingException;
import jakarta.xml.ws.BindingProvider;
import tp1.api.service.soap.SoapException;
import tp1.api.service.util.Result;

import com.sun.xml.ws.client.BindingProviderProperties;

public class SoapClient {

	protected static final int REQUEST_TIMEOUT = 5000;
	protected static final int CONNECT_TIMEOUT = 5000;
	protected static final int RETRY_SLEEP = 3000;
	protected static final int MAX_RETRIES = 10;

	@SuppressWarnings("unchecked")
	public <T> Result<T> reTry(ThrowsSupplier<T> func) {
		// Default error
		Result<T> result = Result.error(Result.ErrorCode.NOT_IMPLEMENTED);
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				var resultOk = func.get();
				result = Result.ok(resultOk);
				break;
			} catch (ProcessingException x) {
				// DO WE NEED THIS?
				sleep(RETRY_SLEEP);
			} catch (Exception x) {
				if (x instanceof SoapException)
					result = Result.error(((SoapException) x).getErrorCode());
				break;
			}
		return result;
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
		} catch (InterruptedException x) { // nothing to do...
		}
	}
}