package ed.launcher;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;


/**
 * Created by Eike on 08.06.2017.
 */
public class FTP_Handler {

    private FTPClient ftp = null;

    public FTP_Handler(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if(!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Konnte keine Verbindung zum Server herstellen");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void downloadFile(String remoteFile, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            this.ftp.retrieveFile(remoteFile, fos);
            System.out.println(remoteFile + " erfolgreich geladen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException e) {
                //Mache gar nichts
                System.out.println("Mache nichts");
            }
        }
    }

}
