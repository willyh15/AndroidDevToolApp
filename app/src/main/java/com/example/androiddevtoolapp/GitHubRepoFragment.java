import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubRepoFragment extends Fragment {

  private EditText editTextRepoPath, editTextFilePath;
  private Button buttonUploadToGitHub;
  private GitHubService gitHubService;
  private KeystoreManager keystoreManager;
  private SharedPreferences sharedPreferences;
  private ProgressBar progressBar;

  private static final String PREFS_NAME = "GitHubPrefs";
  private static final String GITHUB_TOKEN_KEY = "GitHubToken";
  private ExecutorService executorService;

  public GitHubRepoFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_github_repo, container, false);

    // Initialize UI components
    editTextRepoPath = view.findViewById(R.id.editTextRepoPath);
    editTextFilePath = view.findViewById(R.id.editTextFilePath);
    buttonUploadToGitHub = view.findViewById(R.id.buttonUploadToGitHub);
    progressBar = view.findViewById(R.id.progressBar);
    progressBar.setVisibility(View.GONE); // Hide progress bar initially

    // Initialize KeystoreManager and SharedPreferences
    keystoreManager = new KeystoreManager(getContext());
    sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    executorService = Executors.newSingleThreadExecutor();

    buttonUploadToGitHub.setOnClickListener(
        v -> {
          String repoPath = editTextRepoPath.getText().toString();
          String filePath = editTextFilePath.getText().toString();
          if (repoPath.isEmpty() || filePath.isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Please enter both repository path and file path",
                    Toast.LENGTH_SHORT)
                .show();
          } else {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar during upload
            executeUploadFileTask(repoPath, filePath);
          }
        });

    return view;
  }

  private void executeUploadFileTask(String repoPath, String filePath) {
    executorService.execute(
        () -> {
          boolean result = uploadFileToGitHub("your-username", "your-repo", repoPath, filePath);

          new Handler(Looper.getMainLooper())
              .post(
                  () -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar after task completion
                    if (result) {
                      Toast.makeText(
                              getContext(),
                              "File uploaded to GitHub successfully",
                              Toast.LENGTH_SHORT)
                          .show();
                    } else {
                      Toast.makeText(
                              getContext(), "Failed to upload file to GitHub", Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
        });
  }

  private Boolean uploadFileToGitHub(String owner, String repo, String path, String filePath) {
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
      String fileContentBase64 =
          android.util.Base64.encodeToString(fileContent, android.util.Base64.NO_WRAP);

      String body =
          "{ \"message\": \"Upload file via app\", \"content\": \"" + fileContentBase64 + "\" }";
      RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/json"));

      gitHubService
          .uploadFile(owner, repo, path, requestBody)
          .enqueue(
              new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                  // Handle response in onPostExecute
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                  // Handle failure in onPostExecute
                }
              });

      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
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
}
