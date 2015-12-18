# Email export console application
## About
This is java console application for downloading email messages on a hard drive. Application is based on javax.mail library and in this moment enables downloading email messages from Gmail, Yahoo and Startlogic. Application can be started from command line as:

```java
java -jar EmailExport.jar
```

After this command, application will asks questions about username, password, folder etc.

## List of implemented features

* Configuring email servers as profiles in .properties files
* Reading list of folders on server (inbox, sent, ...)
* For downloading email messages from Gmail security needs to be adjusted temporarily (see [link](https://www.google.com/settings/security/lesssecureapps))
* Every message is downloaded as a folder which containt main message file and all attachments
* Output file for email message is velocity template (html)

## List of features that are currently missing

* User should be able to choose format of output filename
* User should be able to filter email messages (for example, by receiving date, sender, receiver, subject etc.)
* Application should enable downloading only new messages
* Oauth2 authentication for convenient Gmail connection

