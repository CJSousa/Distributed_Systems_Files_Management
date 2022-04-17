package tp1.api.service.soap;

import jakarta.xml.ws.WebFault;

@WebFault
public class UsersException extends SoapException {

	private static final long serialVersionUID = 1L;

	public UsersException() {
		super("");
	}

	public UsersException(String errorMessage ) {
		super(errorMessage);
	}
}
