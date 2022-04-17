package tp1.api.service.soap;

import tp1.api.service.util.Result;

public class SoapException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public SoapException() {
		super("");
	}

	public SoapException(String errorMessage ) {
		super(errorMessage);
	}

	public Result.ErrorCode getErrorCode() {
		return Result.ErrorCode.valueOf(this.getMessage());
	}

}
