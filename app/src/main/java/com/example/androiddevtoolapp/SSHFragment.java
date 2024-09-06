import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHFragment extends Fragment {

    private EditText editTextHost, editTextUsername, editTextPassword;
    private Button buttonConnect;

    public SSHFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ssh, container, false);

        // Initialize UI components
        editTextHost = view.findViewById(R.id.editTextHost);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonConnect = view.findViewById(R.id.buttonConnect);

        buttonConnect.setOnClickListener(v -> {
            // Collect SSH credentials
            String host = editTextHost.getText().toString();
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            
            if (host.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Attempt SSH connection
                new SSHConnectTask().execute(host, username, password);
            }
        });

        return view;
    }

    private class SSHConnectTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String host = params[0];
            String username = params[1];
            String password = params[2];

            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(username, host, 22);
                session.setPassword(password);

                // Avoid asking for key confirmation
                session.setConfig("StrictHostKeyChecking", "no");

                // Connect to the server
                session.connect();

                // If we reach here, the connection was successful
                session.disconnect();
                return true;

            } catch (JSchException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(getContext(), "Connected successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to connect. Please check your credentials.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
