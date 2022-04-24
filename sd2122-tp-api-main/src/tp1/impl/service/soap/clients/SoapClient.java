package tp1.impl.service.soap.clients;

import java.util.Map;
import java.util.function.Supplier;
import jakarta.ws.rs.ProcessingException;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

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
            } catch (WebServiceException x) {
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
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
		} catch (InterruptedException x) { // nothing to do...
		}
	}
	
	protected static Result.ErrorCode errorCode(Exception e) {
        switch (e.getMessage()) {
            case "NOT_FOUND": return Result.ErrorCode.NOT_FOUND;
            //case "NO_CONTENT": return Result.ErrorCode.NO_CONTENT;
            case "FORBIDDEN": return Result.ErrorCode.FORBIDDEN;
            case "OK": return Result.ErrorCode.OK;
            case "CONFLICT" : return Result.ErrorCode.CONFLICT;
            case "INTERNAL_ERROR": return Result.ErrorCode.INTERNAL_ERROR;
            case "REQUEST_TIMEOUT": return Result.ErrorCode.REQUESTED_TIMEOUT;
            default:
                return Result.ErrorCode.BAD_REQUEST;
        }
    }
	
}
