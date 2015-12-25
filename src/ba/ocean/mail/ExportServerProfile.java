package ba.ocean.mail;

import java.util.Properties;

/**
 * Profile containing server details. All profiles should be written in profiles directory.
 * @author almir
 */
public class ExportServerProfile {
    // this name is shown to user when asked to choose profile
    private String name;
    // server host
    private String host;
    // provider: imaps or pop3
    private String protocol;
    // does server support search terms
    private boolean supportedSearchTerms = true;
    
    public ExportServerProfile(){}

    public ExportServerProfile(String name, String host, String protocol) {
        this.name = name;
        this.host = host;
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isSupportedSearchTerms() {
        return supportedSearchTerms;
    }

    public void setSupportedSearchTerms(boolean supportedSearchTerms) {
        this.supportedSearchTerms = supportedSearchTerms;
    }
    
    
    
    /**
     * Converts file configuration in .properties file to instance of this class
     * @param props Properties object from profile file
     * @return profile
     */
    public static ExportServerProfile readFromProperties(Properties props){
        ExportServerProfile configuration = new ExportServerProfile();
        configuration.setHost(props.getProperty("profile.host"));
        configuration.setProtocol(props.getProperty("profile.protocol"));
        configuration.setName(props.getProperty("profile.name"));
        
        String searchSupport = props.getProperty("profile.searcthtermsupport");
        if (searchSupport.equalsIgnoreCase("no")){
            configuration.setSupportedSearchTerms(false);
        }
        return configuration;
    }
    
}
