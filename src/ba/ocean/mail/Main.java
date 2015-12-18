package ba.ocean.mail;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

/**
 * Starts program on console
 * @author almir
 */
public class Main {

    public static void main(String[] args) throws NoSuchProviderException {
        
        try {
            ExportFileService fileService = new ExportFileService();
            ExportConfiguration config = new ExportConfiguration(fileService.readProfiles());
            ExportMessageService messageService = new ExportMessageService(fileService, config);

            messageService.downloadMessages();

        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
    }
    
}
