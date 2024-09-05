import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class CommandsFragment extends Fragment {

    private EditText editTextCommand;
    private Button buttonExecuteCommand;

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

        buttonExecuteCommand.setOnClickListener(v -> {
            // Mock action for executing a custom command
            String command = editTextCommand.getText().toString();
            if (command.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a command", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Executing: " + command, Toast.LENGTH_SHORT).show();
                // In the future, add command execution logic here
            }
        });

        return view;
    }
}
