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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GitRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextCommitMessage, editTextBranchName;
    private Button buttonInitRepo, buttonCommit, buttonCreateBranch, buttonCheckoutBranch, buttonMergeBranch;
    private ProgressBar progressBar;  // Progress bar for loading indicator
    private GitManager gitManager;
    private ExecutorService executorService;

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
        progressBar = view.findViewById(R.id.progressBar);  // Reference to the ProgressBar

        gitManager = new GitManager();
        executorService = Executors.newSingleThreadExecutor();
        progressBar.setVisibility(View.GONE);  // Hide progress bar initially

        buttonInitRepo.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            if (repoPath == null || repoPath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a repository path", Toast.LENGTH_SHORT).show();
            } else {
                executeGitTask("init", repoPath, null);
            }
        });

        buttonCommit.setOnClickListener(v -> {
            String message = editTextCommitMessage.getText().toString();
            if (message == null || message.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a commit message", Toast.LENGTH_SHORT).show();
            } else {
                executeGitTask("commit", null, message);
            }
        });

        buttonCreateBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                executeGitTask("createBranch", branchName, null);
            }
        });

        buttonCheckoutBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                executeGitTask("checkoutBranch", branchName, null);
            }
        });

        buttonMergeBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                executeGitTask("mergeBranch", branchName, null);
            }
        });

        return view;
    }

    private void executeGitTask(String operation, String pathOrBranch, String commitMessage) {
        progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            boolean result = false;
            switch (operation) {
                case "init":
                    result = gitManager.initializeRepository(pathOrBranch);
                    break;
                case "commit":
                    result = gitManager.commitChanges(commitMessage);
                    break;
                case "createBranch":
                    result = gitManager.createBranch(pathOrBranch);
                    break;
                case "checkoutBranch":
                    result = gitManager.checkoutBranch(pathOrBranch);
                    break;
                case "mergeBranch":
                    result = gitManager.mergeBranch(pathOrBranch);
                    break;
            }

            boolean finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> {
                progressBar.setVisibility(View.GONE);
                if (finalResult) {
                    Toast.makeText(getContext(), operation + " operation completed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to " + operation, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gitManager.close();
        executorService.shutdown();
    }
}