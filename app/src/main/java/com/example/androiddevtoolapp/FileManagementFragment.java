import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagementFragment extends Fragment {

    private Button buttonListFiles;
    private ListView listViewFiles;
    private List<String> fileList = new ArrayList<>();

    public FileManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_management, container, false);

        // Initialize UI components
        buttonListFiles = view.findViewById(R.id.buttonListFiles);
        listViewFiles = view.findViewById(R.id.listViewFiles);

        buttonListFiles.setOnClickListener(v -> {
            listFiles();
        });

        listViewFiles.setOnItemClickListener((parent, view1, position, id) -> {
            String fileName = fileList.get(position);
            readFileContent(new File(getContext().getFilesDir(), fileName));
        });

        return view;
    }

    private void listFiles() {
        File directory = getContext().getFilesDir();
        File[] files = directory.listFiles();

        if (files != null) {
            fileList.clear();
            for (File file : files) {
                fileList.add(file.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, fileList);
            listViewFiles.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No files found", Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileContent(File file) {
        StringBuilder content = new StringBuilder();

        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[1024];
            int numCharsRead;
            while ((numCharsRead = reader.read(buffer)) > 0) {
                content.append(buffer, 0, numCharsRead);
            }

            Toast.makeText(getContext(), content.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error reading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
