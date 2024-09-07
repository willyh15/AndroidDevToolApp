import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class CommandsFragment extends Fragment {

    private EditText editTextCommand;
    private Button buttonExecuteCommand;
    private ProgressBar progressBar;

    public CommandsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commands, container, false);

        // Initialize UI components
        editTextCommand = view.findViewById(R.id.editTextCommand);
        buttonExecuteCommand = view.findViewById(R.id.buttonExecuteCommand);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE); // Initially hide progress bar

        buttonExecuteCommand.setOnClickListener(v -> {
            String command = editTextCommand.getText().toString();

            if (command.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a command", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar while executing
                executeCommand(command);
            }
        });

        return view;
    }

    private void executeCommand(String command) {
        // Simulate command execution (replace this with actual execution logic)
        new Thread(() -> {
            try {
                // Simulating execution delay
                Thread.sleep(2000); 

                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar when done
                    Toast.makeText(getContext(), "Executed: " + command, Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Execution failed", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
