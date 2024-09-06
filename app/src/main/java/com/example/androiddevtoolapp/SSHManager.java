import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SSHManager {

    private static SSHManager instance;
    private Session session;

    private SSHManager() {
        // Private constructor to prevent instantiation
    }

    public static SSHManager getInstance() {
        if (instance == null) {
            instance = new SSHManager();
        }
        return instance;
    }

    public boolean connect(String host, String username, String password) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            return true;
        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Session getSession() {
        return session;
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -t " + remoteFilePath);
            OutputStream out = channel.getOutputStream();
            channel.connect();

            FileInputStream fis = new FileInputStream(localFilePath);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            fis.close();
            out.close();
            channel.disconnect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean downloadFile(String remoteFilePath, String localFilePath) {
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -f " + remoteFilePath);
            OutputStream out = channel.getOutputStream();
            FileOutputStream fos = new FileOutputStream(localFilePath);
            byte[] buffer = new byte[1024];
            int len;

            channel.connect();

            while ((len = channel.getInputStream().read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            out.close();
            channel.disconnect();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
