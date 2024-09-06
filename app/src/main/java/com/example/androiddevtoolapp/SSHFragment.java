import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class SSHFragment extends Fragment {

    private EditText editTextHost, editTextUsername, editTextPassword, editTextPrivateKey;
    private RadioGroup radioGroupAuth;
    private RadioButton radioButtonPassword, radioButtonKey;
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
        editTextPrivateKey = view.findViewById(R.id.editTextPrivateKey);
        radioGroupAuth = view.findViewById(R.id.radioGroupAuth);
        radioButtonPassword = view.findViewById(R.id.radioButtonPassword);
        radioButtonKey = view.findViewById(R.id.radioButtonKey);
        buttonConnect = view.findViewById(R.id.buttonConnect);

        radioGroupAuth.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonPassword) {
                editTextPassword.setVisibility(View.VISIBLE);
                editTextPrivateKey.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioButtonKey) {
                editTextPassword.setVisibility(View.GONE);
                editTextPrivateKey.setVisibility(View.VISIBLE);
            }
        });

        buttonConnect.setOnClickListener(v -> {
            String host = editTextHost.getText().toString();
            String username = editTextUsername.getText().toString();
            
            if (radioButtonPassword.isChecked()) {
                String password = editTextPassword.getText().toString();
                if (host.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    new SSHConnectTask().execute(host, username, password, "password");
                }
            } else if (radioButtonKey.isChecked()) {
                String privateKeyPath = editTextPrivateKey.getText().toString();
                if (host.isEmpty() || username.isEmpty() || privateKeyPath.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    new SSHConnectTask().execute(host, username, privateKeyPath, "key");
                }
            }
        });

        return view;
    }

    private class SSHConnectTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String host = params[0];
            String username = params[1];
            String credential = params[2];
            String authType = params[3];

            if ("password".equals(authType)) {
                return SSHManager.getInstance().connectWithPassword(host, username, credential);
            } else if ("key".equals(authType)) {
                return SSHManager.getInstance().connectWithKey(host, username, credential);
            }

            return false;
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
