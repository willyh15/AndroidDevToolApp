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

  public boolean connectWithPassword(String host, String username, String password) {
    return connect(host, username, password, null);
  }

  public boolean connectWithKey(String host, String username, String privateKeyPath) {
    return connect(host, username, null, privateKeyPath);
  }

  private boolean connect(String host, String username, String password, String privateKeyPath) {
    try {
      JSch jsch = new JSch();
      if (privateKeyPath != null) {
        jsch.addIdentity(privateKeyPath);
      }
      session = jsch.getSession(username, host, 22);
      if (password != null) {
        session.setPassword(password);
      }
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
      return true;
    } catch (JSchException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void disconnect() {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
  }

  public boolean uploadFile(String localFilePath, String remoteFilePath) {
    return performSCP(localFilePath, remoteFilePath, "upload");
  }

  public boolean downloadFile(String remoteFilePath, String localFilePath) {
    return performSCP(localFilePath, remoteFilePath, "download");
  }

  private boolean performSCP(String source, String destination, String operation) {
    try {
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      String command = operation.equals("upload") ? "scp -t " + destination : "scp -f " + source;
      channel.setCommand(command);
      OutputStream out = channel.getOutputStream();
      channel.connect();

      if (operation.equals("upload")) {
        FileInputStream fis = new FileInputStream(source);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
          out.write(buffer, 0, len);
        }
        fis.close();
      } else {
        FileOutputStream fos = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = channel.getInputStream().read(buffer)) != -1) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }

      out.close();
      channel.disconnect();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
