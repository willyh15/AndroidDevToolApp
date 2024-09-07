import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GitHubRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextFilePath;
    private Button buttonUploadToGitHub;
    private ProgressBar progressBar;  // New ProgressBar to show progress
    private GitHubService gitHubService;
    private KeystoreManager keystoreManager;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "GitHubPrefs";
    private static final String GITHUB_TOKEN_KEY = "GitHubToken";

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
        progressBar = view.findViewById(R.id.progressBar);  // Progress bar to indicate progress
        progressBar.setVisibility(View.GONE);  // Initially hidden

        // Initialize KeystoreManager and SharedPreferences
        keystoreManager = new KeystoreManager(getContext());
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        buttonUploadToGitHub.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            String filePath = editTextFilePath.getText().toString();

            // Null and empty checks
            if (repoPath == null || repoPath.isEmpty() || filePath == null || filePath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter both repository path and file path", Toast.LENGTH_SHORT).show();
            } else {
                // Upload in the background
                new UploadFileTask().execute(repoPath, filePath);
            }
        });

        return view;
    }

    // Method to store the encrypted GitHub token
    private void storeGitHubToken(String token) {
        String encryptedToken = keystoreManager.encryptData(token);
        sharedPreferences.edit().putString(GITHUB_TOKEN_KEY, encryptedToken).apply();
    }

    // Method to retrieve and decrypt the GitHub token
    private String getGitHubToken() {
        String encryptedToken = sharedPreferences.getString(GITHUB_TOKEN_KEY, null);
        if (encryptedToken != null) {
            return keystoreManager.decryptData(encryptedToken);
        } else {
            Toast.makeText(getContext(), "GitHub token not found", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // AsyncTask to handle the file upload in the background
    private class UploadFileTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);  // Show progress bar before upload
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String owner = "your-username";  // Replace with your GitHub username
            String repo = "your-repo";  // Replace with your GitHub repo name
            String repoPath = params[0];
            String filePath = params[1];
            return uploadFileToGitHub(owner, repo, repoPath, filePath);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);  // Hide progress bar after upload
            if (result) {
                Toast.makeText(getContext(), "File uploaded to GitHub successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to upload file to GitHub", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean uploadFileToGitHub(String owner, String repo, String path, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        String token = getGitHubToken();
        if (token == null) {
            return false;
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String fileContentBase64 = android.util.Base64.encodeToString(fileContent, android.util.Base64.NO_WRAP);

            String body = "{ \"message\": \"Upload file via app\", \"content\": \"" + fileContentBase64 + "\" }";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);

            // Use the token in your GitHub API request headers
            gitHubService.uploadFile(owner, repo, path, requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to upload file: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
