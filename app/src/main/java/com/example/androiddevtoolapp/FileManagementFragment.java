import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagementFragment extends Fragment {

    private Button buttonListFiles, buttonUploadFile, buttonDownloadFile, buttonEditFile, buttonSaveFile;
    private ListView listViewFiles;
    private EditText editTextRemoteFilePath, editTextLocalFilePath, editTextFileContent;
    private ProgressBar progressBar;
    private List<String> fileList = new ArrayList<>();
    private String currentFilePath;

    public FileManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_management, container, false);

        // Initialize UI components
        buttonListFiles = view.findViewById(R.id.buttonListFiles);
        buttonUploadFile = view.findViewById(R.id.buttonUploadFile);
        buttonDownloadFile = view.findViewById(R.id.buttonDownloadFile);
        buttonEditFile = view.findViewById(R.id.buttonEditFile);
        buttonSaveFile = view.findViewById(R.id.buttonSaveFile);
        listViewFiles = view.findViewById(R.id.listViewFiles);
        editTextRemoteFilePath = view.findViewById(R.id.editTextRemoteFilePath);
        editTextLocalFilePath = view.findViewById(R.id.editTextLocalFilePath);
        editTextFileContent = view.findViewById(R.id.editTextFileContent);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE); // Hide initially

        buttonListFiles.setOnClickListener(v -> listFiles());
        buttonUploadFile.setOnClickListener(v -> uploadFile());
        buttonDownloadFile.setOnClickListener(v -> downloadFile());
        buttonEditFile.setOnClickListener(v -> editFile());
        buttonSaveFile.setOnClickListener(v -> saveFile());

        listViewFiles.setOnItemClickListener((parent, view1, position, id) -> {
            String fileName = fileList.get(position);
            String filePath = new File(getContext().getFilesDir(), fileName).getAbsolutePath();
            editTextLocalFilePath.setText(filePath);
            currentFilePath = filePath;
        });

        return view;
    }

    private void listFiles() {
        progressBar.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.GONE);
    }

    private void uploadFile() {
        String localFilePath = editTextLocalFilePath.getText().toString();
        String remoteFilePath = editTextRemoteFilePath.getText().toString();

        if (localFilePath.isEmpty() || remoteFilePath.isEmpty()) {
            Toast.makeText(getContext(), "Please specify both local and remote file paths", Toast.LENGTH_SHORT).show();
        } else {
            new SCPUploadTask().execute(localFilePath, remoteFilePath);
        }
    }

    private void downloadFile() {
        String localFilePath = editTextLocalFilePath.getText().toString();
        String remoteFilePath = editTextRemoteFilePath.getText().toString();

        if (localFilePath.isEmpty() || remoteFilePath.isEmpty()) {
            Toast.makeText(getContext(), "Please specify both local and remote file paths", Toast.LENGTH_SHORT).show();
        } else {
            new SCPDownloadTask().execute(remoteFilePath, localFilePath);
        }
    }

    private void editFile() {
        if (currentFilePath != null && !currentFilePath.isEmpty()) {
            try {
                FileInputStream fis = new FileInputStream(currentFilePath);
                byte[] data = new byte[(int) new File(currentFilePath).length()];
                fis.read(data);
                fis.close();
                editTextFileContent.setText(new String(data, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error reading file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please select a file to edit", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFile() {
        if (currentFilePath != null && !currentFilePath.isEmpty()) {
            try {
                FileOutputStream fos = new FileOutputStream(currentFilePath);
                fos.write(editTextFileContent.getText().toString().getBytes());
                fos.close();
                Toast.makeText(getContext(), "File saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error saving file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No file to save", Toast.LENGTH_SHORT).show();
        }
    }

    private class SCPUploadTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String localFilePath = params[0];
            String remoteFilePath = params[1];
            return SSHManager.getInstance().uploadFile(localFilePath, remoteFilePath);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            if (result) {
                Toast.makeText(getContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "File upload failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SCPDownloadTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String remoteFilePath = params[0];
            String localFilePath = params[1];
            return SSHManager.getInstance().downloadFile(remoteFilePath, localFilePath);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            if (result) {
                Toast.makeText(getContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "File download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}