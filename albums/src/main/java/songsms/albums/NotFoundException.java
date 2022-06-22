package songsms.albums;

/**
 * @author Julian Knepel
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Exception that is thrown when an Object couldn't be found.
	 */
	public NotFoundException() {
        super();
    }
}