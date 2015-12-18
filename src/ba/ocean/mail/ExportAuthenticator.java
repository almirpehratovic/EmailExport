package ba.ocean.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Enables entering username and password when needed
 * @author almir
 */
public class ExportAuthenticator extends Authenticator {
    
    /**
     * Asks user for username and password
     * @return PasswordAuthentication for provided username and password
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (System.console() != null) {
            System.out.println("Please enter your username:");
            String username = System.console().readLine();
            System.out.println("Please enter your password:");
            char[] password = System.console().readPassword();
            return new PasswordAuthentication(username, new String(password));
        }
        return null;
    }

}
