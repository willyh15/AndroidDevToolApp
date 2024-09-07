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

public class GitRepoFragment extends Fragment {

    private EditText editTextRepoPath, editTextCommitMessage, editTextBranchName;
    private Button buttonInitRepo, buttonCommit, buttonCreateBranch, buttonCheckoutBranch, buttonMergeBranch;
    private ProgressBar progressBar;  // Progress bar for loading indicator
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
        progressBar = view.findViewById(R.id.progressBar);  // Reference to the ProgressBar

        gitManager = new GitManager();
        progressBar.setVisibility(View.GONE);  // Hide progress bar initially

        buttonInitRepo.setOnClickListener(v -> {
            String repoPath = editTextRepoPath.getText().toString();
            if (repoPath == null || repoPath.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a repository path", Toast.LENGTH_SHORT).show();
            } else {
                new GitTask("init", repoPath, null).execute();
            }
        });

        buttonCommit.setOnClickListener(v -> {
            String message = editTextCommitMessage.getText().toString();
            if (message == null || message.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a commit message", Toast.LENGTH_SHORT).show();
            } else {
                new GitTask("commit", null, message).execute();
            }
        });

        buttonCreateBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                new GitTask("createBranch", branchName, null).execute();
            }
        });

        buttonCheckoutBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                new GitTask("checkoutBranch", branchName, null).execute();
            }
        });

        buttonMergeBranch.setOnClickListener(v -> {
            String branchName = editTextBranchName.getText().toString();
            if (branchName == null || branchName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a branch name", Toast.LENGTH_SHORT).show();
            } else {
                new GitTask("mergeBranch", branchName, null).execute();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gitManager.close();
    }

    // AsyncTask to handle Git operations in the background
    private class GitTask extends AsyncTask<Void, Void, Boolean> {
        private String operation;
        private String pathOrBranch;
        private String commitMessage;

        public GitTask(String operation, String pathOrBranch, String commitMessage) {
            this.operation = operation;
            this.pathOrBranch = pathOrBranch;
            this.commitMessage = commitMessage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);  // Show progress bar before starting task
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (operation) {
                case "init":
                    return gitManager.initializeRepository(pathOrBranch);
                case "commit":
                    return gitManager.commitChanges(commitMessage);
                case "createBranch":
                    return gitManager.createBranch(pathOrBranch);
                case "checkoutBranch":
                    return gitManager.checkoutBranch(pathOrBranch);
                case "mergeBranch":
                    return gitManager.mergeBranch(pathOrBranch);
                default:
                    return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);  // Hide progress bar when task is done
            if (result) {
                Toast.makeText(getContext(), operation + " operation completed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to " + operation, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
