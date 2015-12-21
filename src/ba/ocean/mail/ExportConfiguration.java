package ba.ocean.mail;

import java.util.ArrayList;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import static ba.ocean.mail.ExportFileService.NamingPattern;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application configuration
 * @author almir
 */
public class ExportConfiguration {
    // date of first message to download
    private Date firstDate;
    // date of last message to download
    private Date lastDate;
    // list of profiles written in profiles folder
    private List<ExportServerProfile> profiles = new ArrayList<>();
    // chosen profile
    private ExportServerProfile activeProfile;
    // chosen folder
    private Folder folder;
    // chosen naming pattern
    private NamingPattern namingPattern;

    public ExportConfiguration(List<ExportServerProfile> profiles) {
        this.profiles = profiles;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }
    
    

    public List<ExportServerProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ExportServerProfile> profiles) {
        this.profiles = profiles;
    }

    public ExportServerProfile getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(ExportServerProfile activeProfile) {
        this.activeProfile = activeProfile;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public NamingPattern getNamingPattern() {
        return namingPattern;
    }

    public void setNamingPattern(NamingPattern namingPattern) {
        this.namingPattern = namingPattern;
    }
    
    
    /**
     * Asks user to choose one profile from the list of available profiles
     */
    public void askForProfile(){
        String line = null; int num = -1;
        int i = 0;
        System.out.println("Choose email profile number:");
        for (ExportServerProfile profile : this.profiles){
            i++;
            System.out.println("[" + i + "] " + profile.getName());
        }
        do {
            System.out.println("Your selection: ");
            line = System.console().readLine();
            num = convertString(line);
        } while (num == -1 || num == 0 || num > i);
        setActiveProfile(profiles.get(num-1));
    }
    
    /**
     * Asks user to choose which messages to download
     * @param folder Server folder name (for example INBOX)
     * @param serverFolder All messages in server folder
     */
    public void askForMessagesFilter(String folder, Folder serverFolder) throws MessagingException{
        int numOfMessages = serverFolder.getMessageCount();
        System.out.println("Folder " + folder + " has total of " + numOfMessages
                + " of messages");
        
        if (numOfMessages > 0){
            System.out.println("Date of first message is " + serverFolder.getMessage(1).getReceivedDate());
        }
        
        String line = null; Date date = null; String datePattern = "yyyy/MM/dd";
        
        do {
            System.out.println("Please enter first date (" + datePattern + ") to download:");
            line = System.console().readLine();
            date = convertString(line, datePattern);
        } while (date == null);
        setFirstDate(date);
        
        do {
            System.out.println("Please enter last date (" + datePattern + ") to download:");
            line = System.console().readLine();
            date = convertString(line, datePattern);
        } while (date == null || date.compareTo(this.firstDate) < 0);
        setLastDate(date);

    }
    
    private Date convertString(String str, String pattern){
        DateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e){
            System.out.println(e.getMessage());
        }
        return date;
    }
    
    private int convertString(String str){
        int num = -1;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e){
            
        }
        return num;
    }

    public void askForFolder(Store store) throws MessagingException {
        String line = null; int num = -1;
        int i = 0;
        System.out.println("Choose email folder:");
        List<Folder> folders = new ArrayList<Folder>();
        for (Folder folder : store.getDefaultFolder().list("*")){
            i++;
            folders.add(folder);
            System.out.println("[" + i + "] " + folder.getName());
        }
        do {
            System.out.println("Your selection: ");
            line = System.console().readLine();
            num = convertString(line);
        } while (num == -1 || num == 0 || num > i);
        setFolder(folders.get(num-1));
    }
    
    public void askForNamingPattern() {
        String line = null; int num = -1;
        int i = 0;
        System.out.println("Choose naming pattern:");
        List<String> patterns = new ArrayList<>();
        for (NamingPattern p : NamingPattern.values()){
            i++;
            patterns.add(p.name());
            System.out.println("[" + i + "] " + p.name());
        }
        do {
            System.out.println("Your selection: ");
            line = System.console().readLine();
            num = convertString(line);
        } while (num == -1 || num == 0 || num > i);
        setNamingPattern(NamingPattern.valueOf(patterns.get(num-1)));
    }
    
    
    
    
}
