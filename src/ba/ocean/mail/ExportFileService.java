package ba.ocean.mail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Responsible for writing attachments, folders and creating main output file
 *
 * @author almir
 */
public class ExportFileService {

    public static final String EXPORT_FOLDER = "output";
    public static final String EXPORT_INLINE_FILE = "body_inline.txt";
    public static final String EXPORT_TEXT_FILE = "body_text.txt";
    public static final String EXPORT_MESSAGE_FILE = "message.html";

    public static final String TEMPLATE_FOLDER = "template";
    public static final String PROFILE_FOLDER = "profiles";
    public static final String NEW_LINE = "<br />";

    public static final File outputFolder = new File(EXPORT_FOLDER);

    public ExportFileService() throws IOException {
        // does output folder exists?
        if (!outputFolder.exists()) {
            Files.createDirectory(outputFolder.toPath());
        }
    }

    /**
     * Creates folder for one email message. Name of the folder should depend of
     * user choices
     *
     * @param message Email message
     * @return created folder
     * @throws IOException
     * @throws MessagingException
     */
    public File createMessageFolder(ExportServerProfile profile, Message message, String namingPattern) throws IOException, MessagingException {
        // create folder for server folder name, for example inbox
        File folder = new File(outputFolder, cleanFilename(profile.getName()));

        if (!folder.exists()) {
            folder.mkdir();
        }

        folder = new File(folder, cleanFilename(message.getFolder().getName()));

        if (!folder.exists()) {
            folder.mkdir();
        }

        // create name of the folder according to naming rules
        String name = createNameFromPattern(message, namingPattern);

        if (name == null || name.length() == 0) {
            name = "Message " + message.getMessageNumber();
        }

        name = cleanFilename(name.substring(0, name.length() > 200 ? 200 : name.length()));

        File messageFolder = new File(folder, name);
        if (messageFolder.exists()) {
            throw new IOException("Folder with name " + name + " already exists.");
        } else {
            if (!messageFolder.mkdir()) {
                System.out.println("Couldn't create folder " + messageFolder);
            }
        }
        return messageFolder;
    }
    
    /**
     * Creates .eml file for one email message
     *
     * @param message Email message
     * @return created file
     * @throws IOException
     * @throws MessagingException
     */
    public File createEml(ExportServerProfile profile, Message message, String namingPattern) throws IOException, MessagingException {
        // create folder for server folder name, for example inbox
        File folder = new File(outputFolder, cleanFilename(profile.getName()));

        if (!folder.exists()) {
            folder.mkdir();
        }

        folder = new File(folder, cleanFilename(message.getFolder().getName()));

        if (!folder.exists()) {
            folder.mkdir();
        }

        // create name of the folder according to naming rules
        String name = createNameFromPattern(message, namingPattern);

        if (name == null || name.length() == 0) {
            name = "Message " + message.getMessageNumber();
        }

        name = cleanFilename(name.substring(0, name.length() > 200 ? 200 : name.length()));

        File messageFile = new File(folder, name + ".eml");
        if (messageFile.exists()) {
            throw new IOException("File with name " + name + " already exists.");
        } else {
           FileOutputStream fos = new FileOutputStream(messageFile);
           message.writeTo(fos);
           fos.flush();
           fos.close();
        }
        return messageFile;
    }

    /**
     * Creates email attachment file
     *
     * @param in InputStream of attachment
     * @param folder Folder in which attachment will be created
     * @param filename Filename of the attachment
     * @return
     * @throws IOException
     */
    public File createAttachment(InputStream in, File folder, String filename) throws IOException {
        filename = filename.replace(" ", "");
        File file = new File(folder, cleanFilename(filename));
        
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        } catch (IOException e){
            
        }
        
        out.flush();
        out.close();
        in.close();

        return file;
    }

    /**
     * Create attachment file for message inline
     *
     * @param part Message part
     * @param folder Folder in which attachment will be created
     * @param filename Filename of the attachment
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public File createInlineAttachment(Part part, File folder, String filename) throws IOException, MessagingException {
        File file = new File(folder, cleanFilename(filename));
        OutputStream out = new FileOutputStream(file);
        part.writeTo(out);
        out.flush();
        out.close();

        return file;
    }

    /**
     * Creates HTML file which contains details of the message, using Velocity
     *
     * @param message Message
     * @param folder Folder in which attachment will be created
     * @return created file
     * @throws MessagingException
     * @throws IOException
     */
    public File createMessageFileFromTemplate(Message message, File folder) throws MessagingException, IOException {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        Template velocityTemplate = velocityEngine.getTemplate(TEMPLATE_FOLDER + "/template.html");

        VelocityContext ctx = new VelocityContext();
        ctx.put("subject", message.getSubject());
        ctx.put("from", ExportUtils.addressToString(message.getFrom()));
        ctx.put("to", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.TO)));
        ctx.put("bcc", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.BCC)));
        ctx.put("cc", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.CC)));
        ctx.put("flags", getFlags(message.getFlags()));

        ctx.put("date", getAppropriateDate(message.getReceivedDate(), message.getSentDate(), "MM/dd/yyyy HH:mm:ss"));

        StringBuffer mailText = null;
        StringBuffer mailInline = null;
        List<String> attachments = new ArrayList<>();

        for (File newFile : folder.listFiles()) {
            if (newFile.getName().equals(EXPORT_TEXT_FILE)) {
                mailText = readTextFromFile(newFile);
            } else if (newFile.getName().equals(EXPORT_INLINE_FILE)) {
                mailInline = readTextFromFile(newFile);
                attachments.add(newFile.getName());
            } else {
                attachments.add(newFile.getName());
            }
        }

        if (mailText != null) {
            ctx.put("body", mailText.toString());
        } else if (mailInline != null) {
            ctx.put("body", mailInline.toString());
        } else {
            ctx.put("body", "");
        }

        if (attachments.size() > 0) {
            ctx.put("attachments", attachments);
        }

        FileWriter fileWriter = new FileWriter(new File(folder, EXPORT_MESSAGE_FILE));
        velocityTemplate.merge(ctx, fileWriter);
        fileWriter.flush();
        fileWriter.flush();

        return null;
    }

    /**
     * Reads email flags and returns them as a string
     *
     * @param flags Flags object from Message
     * @return flags as a string
     */
    private String getFlags(Flags flags) {
        StringBuffer buffer = new StringBuffer();

        if (flags.contains(Flags.Flag.ANSWERED)) {
            buffer.append("ANSWERED ");
        }
        if (flags.contains(Flags.Flag.DELETED)) {
            buffer.append("DELETED ");
        }
        if (flags.contains(Flags.Flag.DRAFT)) {
            buffer.append("DRAFT ");
        }
        if (flags.contains(Flags.Flag.FLAGGED)) {
            buffer.append("FLAGGED ");
        }
        if (flags.contains(Flags.Flag.RECENT)) {
            buffer.append("RECENT ");
        }
        if (flags.contains(Flags.Flag.SEEN)) {
            buffer.append("SEEN ");
        }
        if (flags.contains(Flags.Flag.USER)) {
            buffer.append("USER ");
        }

        for (String flag : flags.getUserFlags()) {
            buffer.append(flag + " ");
        }

        return buffer.toString();
    }

    /**
     * Reads profiles from .properties files
     *
     * @return list of ExportServerProfile objects
     */
    public List<ExportServerProfile> readProfiles() {
        File folder = new File(PROFILE_FOLDER);
        List<ExportServerProfile> profiles = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (file.getName().contains(".properties")) {
                Properties props = new Properties();
                try {
                    InputStream fis = new FileInputStream(file);
                    props.load(fis);
                    fis.close();
                    profiles.add(ExportServerProfile.readFromProperties(props));
                } catch (IOException ex) {
                    System.out.println("Error while loading " + file.getAbsolutePath());
                    ex.printStackTrace();
                }
            }
        }

        return profiles;
    }

    /**
     * Reads profiles from .properties files
     *
     * @return all configured patterns
     */
    public Properties readNamingPatterns() {
        File folder = new File(TEMPLATE_FOLDER);

        File file = new File(folder, "naming.properties");

        Properties props = new Properties();

        if (!file.exists()) {
            props.put("1", "{sender}_{receiver}_{subject}");
        } else {

            try {
                InputStream fis = new FileInputStream(file);
                props.load(fis);
                fis.close();
            } catch (IOException ex) {
                System.out.println("Error while loading " + file.getAbsolutePath());
                ex.printStackTrace();
            }
        }

        return props;
    }

    /**
     * Helper method for reading file (body of message)
     *
     * @param file File to read
     * @return Text from the file
     * @throws IOException
     */
    private static StringBuffer readTextFromFile(File file) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line = "init";
        while (line != null) {
            line = reader.readLine();
            if (line != null) {
                buffer.append(line + NEW_LINE);
            }
        }
        return buffer;
    }

    private String getAppropriateDate(Date receivedDate, Date sentDate, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        if (receivedDate != null) {
            return format.format(receivedDate);
        } else if (sentDate != null) {
            return format.format(sentDate);
        }
        return null;
    }

    /**
     * Create file name from chosen naming pattern
     *
     * @param message Email message
     * @param namingPattern naming pattern
     * @return
     */
    private String createNameFromPattern(Message message, String namingPattern) throws MessagingException {
        String name = namingPattern;

        name = name.replace("{rDateYear}", ExportUtils.convertDate(message.getReceivedDate(), "yyyy"));
        name = name.replace("{rDateMonth}", ExportUtils.convertDate(message.getReceivedDate(), "MM"));
        name = name.replace("{rDateDay}", ExportUtils.convertDate(message.getReceivedDate(), "dd"));
        name = name.replace("{rDateHour}", ExportUtils.convertDate(message.getReceivedDate(), "HH"));
        name = name.replace("{rDateMinute}", ExportUtils.convertDate(message.getReceivedDate(), "mm"));
        name = name.replace("{rDateSecond}", ExportUtils.convertDate(message.getReceivedDate(), "ss"));

        name = name.replace("{sDateYear}", ExportUtils.convertDate(message.getSentDate(), "yyyy"));
        name = name.replace("{sDateMonth}", ExportUtils.convertDate(message.getSentDate(), "MM"));
        name = name.replace("{sDateDay}", ExportUtils.convertDate(message.getSentDate(), "dd"));
        name = name.replace("{sDateHour}", ExportUtils.convertDate(message.getSentDate(), "HH"));
        name = name.replace("{sDateMinute}", ExportUtils.convertDate(message.getSentDate(), "mm"));
        name = name.replace("{sDateSecond}", ExportUtils.convertDate(message.getSentDate(), "ss"));

        name = name.replace("{from}", ExportUtils.addressToString(message.getFrom()));
        name = name.replace("{fromUsername}", ExportUtils.addressUsernameToString(message.getFrom()));
        name = name.replace("{fromDomain}", ExportUtils.addressDomainToString(message.getFrom()));
        
        name = name.replace("{to}", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.TO)));
        name = name.replace("{toUsername}", ExportUtils.addressUsernameToString(message.getRecipients(Message.RecipientType.TO)));
        name = name.replace("{toDomain}", ExportUtils.addressDomainToString(message.getRecipients(Message.RecipientType.TO)));
        
        name = name.replace("{cc}", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.CC)));
        name = name.replace("{ccUsername}", ExportUtils.addressUsernameToString(message.getRecipients(Message.RecipientType.CC)));
        name = name.replace("{ccDomain}", ExportUtils.addressDomainToString(message.getRecipients(Message.RecipientType.CC)));
                
        name = name.replace("{bcc}", ExportUtils.addressToString(message.getRecipients(Message.RecipientType.BCC)));
        name = name.replace("{bccUsername}", ExportUtils.addressUsernameToString(message.getRecipients(Message.RecipientType.BCC)));
        name = name.replace("{bccDomain}", ExportUtils.addressDomainToString(message.getRecipients(Message.RecipientType.BCC)));

        name = name.replace("{subject}", message.getSubject() == null ? "" : message.getSubject());

        return name;
    }

    /**
     * Cleans odd and unallowed characters from filename
     *
     * @param filename
     * @return
     */
    public String cleanFilename(String filename) {
        if (filename != null) {
            String name = filename;
            name = name.replaceAll("[^a-zA-Z0-9.@:_\\s-]", "");
            return name;
        }
        return null;
    }

}
