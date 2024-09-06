import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GitHubRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextFilePath;
    private Button buttonUploadToGitHub;
    private GitHubService gitHubService;

    public GitHubRepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_github_repo, container, false);

        // Initialize UI components
        editTextRepoPath = view.findViewById(R.id.editTextRepoPath);
        editTextFilePath = view.findViewById(R.id.editTextFilePath);
        buttonUploadToGitHub = view.findViewById(R.id.buttonUploadToGitHub);

        buttonUploadToGitHub.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            String filePath = editTextFilePath.getText().toString();
            if (repoPath.isEmpty() || filePath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter both repository path and file path", Toast.LENGTH_SHORT).show();
            } else {
                uploadFileToGitHub("your-username", "your-repo", repoPath, filePath);
            }
        });

        return view;
    }

    private void uploadFileToGitHub(String owner, String repo, String path, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getContext(), "File does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String fileContentBase64 = android.util.Base64.encodeToString(fileContent, android.util.Base64.NO_WRAP);

            String body = "{ \"message\": \"Upload file via app\", \"content\": \"" + fileContentBase64 + "\" }";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);

            gitHubService.uploadFile(owner, repo, path, requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "File uploaded to GitHub successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to upload file to GitHub", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error reading file", Toast.LENGTH_SHORT).show();
        }
    }
}
