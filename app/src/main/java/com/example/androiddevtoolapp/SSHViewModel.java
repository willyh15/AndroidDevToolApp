import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SSHViewModel extends ViewModel {

    private MutableLiveData<String> host;
    private MutableLiveData<String> username;
    private MutableLiveData<String> password;
    private MutableLiveData<String> privateKeyPath;
    private MutableLiveData<Boolean> isConnected;

    public SSHViewModel() {
        host = new MutableLiveData<>();
        username = new MutableLiveData<>();
        password = new MutableLiveData<>();
        privateKeyPath = new MutableLiveData<>();
        isConnected = new MutableLiveData<>(false);
    }

    public MutableLiveData<String> getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host.setValue(host);
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath.setValue(privateKeyPath);
    }

    public MutableLiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean isConnected) {
        this.isConnected.setValue(isConnected);
    }

    // You can add additional business logic here to handle connection persistence
}
