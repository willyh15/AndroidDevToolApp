import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitManager {

    private Git git;
    private Repository repository;

    public boolean initializeRepository(String repoPath) {
        try {
            File repoDir = new File(repoPath);
            if (!repoDir.exists()) {
                repoDir.mkdirs();
            }
            repository = new FileRepositoryBuilder()
                    .setGitDir(new File(repoPath + "/.git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();

            if (!repository.getObjectDatabase().exists()) {
                git = Git.init().setDirectory(repoDir).call();
            } else {
                git = new Git(repository);
            }

            return true;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean commitChanges(String message) {
        try {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(message).call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createBranch(String branchName) {
        try {
            git.branchCreate().setName(branchName).call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkoutBranch(String branchName) {
        try {
            git.checkout().setName(branchName).call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mergeBranch(String branchName) {
        try {
            git.merge().include(git.getRepository().findRef(branchName)).call();
            return true;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (git != null) {
            git.close();
        }
        if (repository != null) {
            repository.close();
        }
    }
}
