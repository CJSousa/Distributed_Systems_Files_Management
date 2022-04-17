package tp1.impl.service.soap.clients;

public interface ThrowsSupplier<T> {
	T get() throws Exception;
}
