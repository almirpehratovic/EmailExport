package ba.ocean.mail;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Main class for downloading and parsing email messages
 * @author almir
 */
public class ExportMessageService {
    
    private ExportFileService fileService;
    private ExportConfiguration configuration;

    public ExportMessageService(ExportFileService fileService, ExportConfiguration configuration) {
        this.fileService = fileService;
        this.configuration = configuration;
    }
    
    /**
     * Responsible for downloading and parsing email messages
     * @param serverFoldername for example INBOX
     * @throws MessagingException
     * @throws IOException 
     */
    public void downloadMessages() throws MessagingException, IOException {
        Properties props = new Properties();
        
        // user must choose one of available profiles
        configuration.askForProfile();
        
        // connect to email server
        Session session = Session.getInstance(props, new ExportAuthenticator());
        Store store = session.getStore(configuration.getActiveProfile().getProtocol());
        store.connect(configuration.getActiveProfile().getHost(), null, null);
        
        // ask which folder to download
        configuration.askForFolder(store);
        
        // read and open folder on email server
        Folder serverFolder = configuration.getFolder();
        if (serverFolder == null) {
            System.out.println("No folder " + serverFolder.getName());
            System.exit(1);
        }

        serverFolder.open(Folder.READ_ONLY);
        
        // user must choose range of email messages to download
        configuration.askForMessagesRange(serverFolder.getName(), serverFolder.getMessageCount());
        
        Message[] messages = serverFolder.getMessages(configuration.getFirstMessage(), configuration.getLastMessage());
        
        // process every message in folder
        for (Message message : messages) {
            System.out.println("Downloading message: " + message.getSubject());
            
            File newFolder = null;
            try {
                newFolder = fileService.createMessageFolder(message);
            } catch (IOException e){
                System.out.println("Skipping message because: " + e.getMessage());
                continue;
            }

            Object messageBody = message.getContent();

            if (messageBody instanceof Multipart) {
                processMessageMultipart((Multipart) messageBody, newFolder);
            } else {
                processMessagePart(message, newFolder);
            }
            
            // write main output file (html) which prints message details
            fileService.createMessageFileFromTemplate(message, newFolder);
        }

        serverFolder.close(false);
        store.close();
    }

    private void processMessageMultipart(Multipart multipart, File folder) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            processMessagePart(multipart.getBodyPart(i), folder);
        }
    }
    
    /**
     * Parser of email message
     * @param part Email part
     * @param folder Folder in which app should create files
     * @throws MessagingException
     * @throws IOException 
     */
    private void processMessagePart(Part part, File folder) throws MessagingException, IOException {
        String attachmentFilename = part.getFileName();
        String disposition = part.getDisposition();
        String contentType = part.getContentType();
        
        if (contentType.toLowerCase().startsWith("multipart/")) {
            processMessageMultipart((Multipart) part.getContent(), folder);
        } else if (attachmentFilename == null && (Part.ATTACHMENT.equalsIgnoreCase(disposition) || !contentType.equalsIgnoreCase("text/plain"))) {
            attachmentFilename = File.createTempFile("attachment", ".html").getName();
        }

        if (attachmentFilename == null) {
            // this is inline part of message; write it to the file
            File file = fileService.createInlineAttachment(part, folder, ExportFileService.EXPORT_INLINE_FILE);
        } else {
            // this is attachment; write it to the file
            if (contentType.toLowerCase().contains("text/plain")) {
                // in this case, attachment is file that contains inline message
                attachmentFilename = ExportFileService.EXPORT_TEXT_FILE;
            }
            File file = fileService.createAttachment(part.getInputStream(), folder, attachmentFilename);
        }
    }

}
