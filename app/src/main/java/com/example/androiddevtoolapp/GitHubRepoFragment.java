import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubRepoFragment extends Fragment {

    private EditText editTextRepoName, editTextRepoDescription;
    private Button buttonCreateRepo;
    private GitHubService gitHubService;

    public GitHubRepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_github_repo, container, false);

        // Initialize UI components
        editTextRepoName = view.findViewById(R.id.editTextRepoName);
        editTextRepoDescription = view.findViewById(R.id.editTextRepoDescription);
        buttonCreateRepo = view.findViewById(R.id.buttonCreateRepo);

        // Set up Retrofit for GitHub API
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    // Add the OAuth token to the request
                    return chain.proceed(chain.request().newBuilder()
                            .header("Authorization", "Bearer YOUR_GITHUB_TOKEN")
                            .build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubService = retrofit.create(GitHubService.class);

        buttonCreateRepo.setOnClickListener(v -> {
            // Collect repository details
            String repoName = editTextRepoName.getText().toString();
            String repoDescription = editTextRepoDescription.getText().toString();

            if (repoName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a repository name", Toast.LENGTH_SHORT).show();
            } else {
                // Create repository
                createRepository(new GitHubRepo(repoName, repoDescription, false));
            }
        });

        return view;
    }

    private void createRepository(GitHubRepo repo) {
        gitHubService.createRepository(repo).enqueue(new Callback<GitHubRepo>() {
            @Override
            public void onResponse(Call<GitHubRepo> call, Response<GitHubRepo> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Repository created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to create repository: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GitHubRepo> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
