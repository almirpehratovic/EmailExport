# Email export console application
## About
This is java console application for downloading email messages on a hard drive. Application is based on javax.mail library and in this moment enables downloading email messages from Gmail, Yahoo and Startlogic. To start application, please ensure that EmailExport.jar, profiles/ and template/ are contained on the same hierarchy level (as in this project), and type

```java
java -jar EmailExport.jar
```

After this command, application will asks questions about username, password, folder etc.

## List of implemented features

* Configuring email servers as profiles in .properties files
* Reading list of folders on server (inbox, sent, ...)
* For downloading email messages from Gmail security needs to be adjusted temporarily (Turn on on this [link](https://www.google.com/settings/security/lesssecureapps))
* Every message is downloaded as a folder which contains main message file and all attachments
* Output file for email message is velocity template (html)
* User can configure naming patterns in naming.properties
* Messages are downloading for the range of dates

## List of features that are currently missing

* User should be able to filter email messages (for example, by receiving date, sender, receiver, subject etc.)
* Application should enable downloading only new messages
* Oauth2 authentication for convenient Gmail connection

