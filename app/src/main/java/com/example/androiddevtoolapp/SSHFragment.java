import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SSHFragment extends Fragment {

    private EditText editTextHost, editTextUsername, editTextPassword, editTextCommand;
    private Button buttonConnect, buttonExecuteCommand;
    private Session session;

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
        editTextCommand = view.findViewById(R.id.editTextCommand);
        buttonConnect = view.findViewById(R.id.buttonConnect);
        buttonExecuteCommand = view.findViewById(R.id.buttonExecuteCommand);

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

        buttonExecuteCommand.setOnClickListener(v -> {
            if (session != null && session.isConnected()) {
                String command = editTextCommand.getText().toString();
                if (!command.isEmpty()) {
                    new SSHCommandTask().execute(command);
                } else {
                    Toast.makeText(getContext(), "Please enter a command to execute", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not connected. Please connect first.", Toast.LENGTH_SHORT).show();
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
                session = jsch.getSession(username, host, 22);
                session.setPassword(password);

                // Avoid asking for key confirmation
                session.setConfig("StrictHostKeyChecking", "no");

                // Connect to the server
                session.connect();
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

    private class SSHCommandTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... commands) {
            StringBuilder output = new StringBuilder();

            try {
                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(commands[0]);
                channel.setErrStream(System.err);
                channel.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                output.append("Error: ").append(e.getMessage());
            }

            return output.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
