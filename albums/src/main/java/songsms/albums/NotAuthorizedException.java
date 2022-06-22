package songsms.albums;

/**
 * @author Julian Knepel
 */
public class NotAuthorizedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Exception that is thrown when the given authorization token is not valid.
	 */
	public NotAuthorizedException() {
        super();
    }
}