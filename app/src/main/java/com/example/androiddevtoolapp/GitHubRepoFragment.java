import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class GitHubRepoFragment extends Fragment {

    private Button buttonCreateRepo;

    public GitHubRepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_github_repo, container, false);

        // Initialize UI components
        buttonCreateRepo = view.findViewById(R.id.buttonCreateRepo);

        buttonCreateRepo.setOnClickListener(v -> {
            // Mock action for creating a GitHub repository
            Toast.makeText(getContext(), "Creating GitHub repository...", Toast.LENGTH_SHORT).show();
            // In the future, add GitHub API logic here
        });

        return view;
    }
}
