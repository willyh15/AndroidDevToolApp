import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

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
}
