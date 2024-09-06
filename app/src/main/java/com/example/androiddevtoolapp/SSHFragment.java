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
import com.jcraft.jsch.JSchException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SSHFragment extends Fragment {

    private EditText editTextHost, editTextUsername, editTextPassword, editTextCommand, editTextExecuteFilePath;
    private Button buttonConnect, buttonExecuteCommand, buttonExecuteFile;

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
        editTextExecuteFilePath = view.findViewById(R.id.editTextExecuteFilePath);
        buttonConnect = view.findViewById(R.id.buttonConnect);
        buttonExecuteCommand = view.findViewById(R.id.buttonExecuteCommand);
        buttonExecuteFile = view.findViewById(R.id.buttonExecuteFile);

        buttonExecuteFile.setOnClickListener(v -> {
            String filePath = editTextExecuteFilePath.getText().toString();
            if (filePath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a file path to execute", Toast.LENGTH_SHORT).show();
            } else {
                new SSHExecuteFileTask().execute(filePath);
            }
        });

        return view;
    }

    private class SSHExecuteFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... filePaths) {
            String filePath = filePaths[0];
            StringBuilder output = new StringBuilder();
            try {
                ChannelExec channel = (ChannelExec) SSHManager.getInstance().getSession().openChannel("exec");
                channel.setCommand("bash " + filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                channel.disconnect();
            } catch (JSchException | IOException e) {
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
