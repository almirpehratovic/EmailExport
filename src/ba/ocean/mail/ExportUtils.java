package ba.ocean.mail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * Utilities / helper methods
 *
 * @author almir
 */
public class ExportUtils {

    public static Date convertString(String str, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static int convertString(String str) {
        int num = -1;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {

        }
        return num;
    }

    public static String convertDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        DateFormat format = new SimpleDateFormat(pattern);
        String str = null;
        try {
            str = format.format(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return str;
    }

    public static String addressToString(Address[] address) {
        if (address == null){
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        if (address != null) {
            for (Address a : address) {
                buffer.append(((InternetAddress) a).getAddress() + " ");
            }
        }
        return buffer.toString();
    }
    
    /**
     * Gets only username parts of email address and converts to string
     * @param address
     * @return 
     */
    public static String addressUsernameToString(Address[] address){
        if (address == null){
            return "";
        }
        StringBuffer usernames = new StringBuffer();
        String addString = addressToString(address);
        String[] array = addString.split(" ");
        for (int i=0; i<array.length; i++){
            if (array[i].contains("@")){
                String[] addSplit = array[i].split("@");
                usernames.append(addSplit[0]);
            } else {
                usernames.append(array[i]);
            }
        }
        return usernames.toString();
    }
    
    /**
     * Gets only domain parts of email address and converts to string
     * @param address
     * @return 
     */
    public static String addressDomainToString(Address[] address){
        if (address == null){
            return "";
        }
        StringBuffer usernames = new StringBuffer();
        String addString = addressToString(address);
        String[] array = addString.split(" ");
        for (int i=0; i<array.length; i++){
            if (array[i].contains("@")){
                String[] addSplit = array[i].split("@");
                usernames.append(addSplit[1]);
            } else {
                usernames.append("unknown-domain");
            }
        }
        return usernames.toString();
    }
}
