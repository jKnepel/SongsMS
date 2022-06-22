package songsms.songs;

/**
 * @author Julian Knepel
 */
public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Exception that is thrown when the user doesn't have the rights to access or edit the content.
	 */
	public ForbiddenException() {
        super();
    }
}