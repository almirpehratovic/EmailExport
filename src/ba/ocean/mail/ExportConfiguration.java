package ba.ocean.mail;

import java.util.ArrayList;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * Application configuration
 * @author almir
 */
public class ExportConfiguration {
    // number of first message to download
    private int firstMessage;
    // number of last message to download
    private int lastMessage;
    // list of profiles written in profiles folder
    private List<ExportServerProfile> profiles = new ArrayList<ExportServerProfile>();
    // chosen profile
    private ExportServerProfile activeProfile;
    // chosen folder
    private Folder folder;

    public ExportConfiguration(List<ExportServerProfile> profiles) {
        this.profiles = profiles;
    }
    
    public int getFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(int firstMessage) {
        this.firstMessage = firstMessage;
    }

    public int getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(int lastMessage) {
        this.lastMessage = lastMessage;
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
     * Asks user to choose first and last message to download
     * @param folder Server folder name (for example INBOX)
     * @param messagesCount Total number of messages in folder
     */
    public void askForMessagesRange(String folder, int messagesCount){
        String line = null; int num = -1;
        System.out.println("Folder " + folder + " has total of " + messagesCount + " of messages.");
        do {
            System.out.println("Please enter the number of first message to download:");
            line = System.console().readLine();
            num = convertString(line);
        } while (num == -1);
        setFirstMessage(num);
        
        do {
            System.out.println("Please enter the number of last message to download:");
            line = System.console().readLine();
            num = convertString(line);
        } while (num == -1 || num < this.firstMessage || num > messagesCount);
        setLastMessage(num);
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
    
    
}
