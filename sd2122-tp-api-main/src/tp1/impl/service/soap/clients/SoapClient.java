package tp1.impl.service.soap.clients;

import java.util.Map;
import jakarta.ws.rs.ProcessingException;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapException;
import tp1.api.service.util.Result;

import com.sun.xml.ws.client.BindingProviderProperties;

public class SoapClient {

	protected static final int REQUEST_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 10000;
	protected static final int RETRY_SLEEP = 3000;
	protected static final int MAX_RETRIES = 10; // depois experimentar para 3

	public <T> Result<T> reTry(ThrowsSupplier<T> func) {
		// Mudar erro default
		Result<T> result = Result.error(Result.ErrorCode.NOT_IMPLEMENTED);
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				var resultOk = func.get();
				result = Result.ok(resultOk);
				break;
			} catch (WebServiceException we) { 
				sleep(RETRY_SLEEP);
			} catch (Exception e) {
				if (e instanceof SoapException) result = Result.error(((SoapException) e).getErrorCode());
				e.printStackTrace(); 
				break;
			}
		}
		return result;
	}
	/*
	try {
	    files.writeFile(fileId, data, token);
	} catch( FilesException x ) {
	    // handle service error
	} catch( WebServiceException we) {
	    // handle invocation error, maybe retry?
	}
	*/
	
	public <T> Result<T> reTry(VoidSupplier<T> func) {
		// Mudar erro default
		Result<T> result = Result.error(Result.ErrorCode.NOT_IMPLEMENTED);
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				func.run();
				return Result.ok();
			} catch (WebServiceException we) { 
				sleep(RETRY_SLEEP);
			} catch (Exception e) {
				if (e instanceof SoapException) result = Result.error(((SoapException) e).getErrorCode());
				e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	/* Método sugerido pelo IDE (não é <T> Result<T> como o nosso
	public Result<Void> reTry(ThrowsSupplier<T> func) {
		// TODO Auto-generated method stub
		return null;
	}*/

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
