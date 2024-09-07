import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class GitRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextCommitMessage, editTextBranchName;
    private Button buttonInitRepo, buttonCommit, buttonCreateBranch, buttonCheckoutBranch, buttonMergeBranch;
    private GitManager gitManager;

    public GitRepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_repo, container, false);

        // Initialize UI components
        editTextRepoPath = view.findViewById(R.id.editTextRepoPath);
        editTextCommitMessage = view.findViewById(R.id.editTextCommitMessage);
        editTextBranchName = view.findViewById(R.id.editTextBranchName);
        buttonInitRepo = view.findViewById(R.id.buttonInitRepo);
        buttonCommit = view.findViewById(R.id.buttonCommit);
        buttonCreateBranch = view.findViewById(R.id.buttonCreateBranch);
        buttonCheckoutBranch = view.findViewById(R.id.buttonCheckoutBranch);
        buttonMergeBranch = view.findViewById(R.id.buttonMergeBranch);

        gitManager = new GitManager();

        buttonInitRepo.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            if (repoPath.isEmpty()) {
                Toast.makeText(getContext(), "It seems like my response was cut off earlier. Let me continue providing the full updated version of each file you requested:

### Full Updated Version of `GitRepoFragment.java`
```java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class GitRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextCommitMessage, editTextBranchName;
    private Button buttonInitRepo, buttonCommit, buttonCreateBranch, buttonCheckoutBranch, buttonMergeBranch;
    private GitManager gitManager;

    public GitRepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_repo, container, false);

        // Initialize UI components
        editTextRepoPath = view.findViewById(R.id.editTextRepoPath);
        editTextCommitMessage = view.findViewById(R.id.editTextCommitMessage);
        editTextBranchName = view.findViewById(R.id.editTextBranchName);
        buttonInitRepo = view.findViewById(R.id.buttonInitRepo);
        buttonCommit = view.findViewById(R.id.buttonCommit);
        buttonCreateBranch = view.findViewById(R.id.buttonCreateBranch);
        buttonCheckoutBranch = view.findViewById(R.id.buttonCheckoutBranch);
        buttonMergeBranch = view.findViewById(R.id.buttonMergeBranch);

        gitManager = new GitManager();

        buttonInitRepo.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            if (repoPath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a repository path", Toast.LENGTH_SHORT).show();
            } else {
                if (gitManager.initializeRepository(repoPath)) {
                    Toast.makeText(getContext(), "Repository initialized successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to initialize repository", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCommit.setOnClickListener(v -> {
            String message = editTextCommitMessage.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a commit message", Toast.LENGTH_SHORT).show();
            } else {
                if (gitManager.commitChanges(message)) {
                    Toast.makeText(getContext(), "Changes committed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to commit changes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCreateBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                if (gitManager.createBranch(branchName)) {
                    Toast.makeText(getContext(), "Branch created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to create branch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCheckoutBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                if (gitManager.checkoutBranch(branchName)) {
                    Toast.makeText(getContext(), "Switched to branch: " + branchName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to switch branch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonMergeBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                if (gitManager.mergeBranch(branchName)) {
                    Toast.makeText(getContext(), "Branch merged successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to merge branch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gitManager.close();
    }
}
