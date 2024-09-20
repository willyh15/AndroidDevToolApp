import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.jcraft.jsch.ChannelExec;

public class ScheduledTaskWorker extends Worker {

  public static final String TASK_TYPE = "TASK_TYPE";
  public static final String TASK_DETAILS = "TASK_DETAILS";

  public ScheduledTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    String taskType = getInputData().getString(TASK_TYPE);
    String taskDetails = getInputData().getString(TASK_DETAILS);

    if (taskType == null || taskDetails == null) {
      return Result.failure();
    }

    switch (taskType) {
      case "SSH_SCRIPT":
        return executeSSHScript(taskDetails);
      case "FILE_SYNC":
        return syncFiles(taskDetails);
      case "GIT_OPERATION":
        return performGitOperation(taskDetails);
      default:
        return Result.failure();
    }
  }

  private Result executeSSHScript(String scriptName) {
    SSHManager sshManager = SSHManager.getInstance();
    if (sshManager.getSession() == null || !sshManager.getSession().isConnected()) {
      return Result.failure(
          new Data.Builder().putString("error", "SSH session not connected").build());
    }

    // Execute the script
    String command = "bash " + scriptName;
    try {
      ChannelExec channel = (ChannelExec) sshManager.getSession().openChannel("exec");
      channel.setCommand(command);
      channel.connect();
      channel.disconnect();
      return Result.success();
    } catch (Exception e) {
      e.printStackTrace();
      return Result.failure(new Data.Builder().putString("error", e.getMessage()).build());
    }
  }

  private Result syncFiles(String details) {
    String[] paths = details.split(",");
    if (paths.length != 2) {
      return Result.failure(new Data.Builder().putString("error", "Invalid file paths").build());
    }

    String localPath = paths[0];
    String remotePath = paths[1];
    SSHManager sshManager = SSHManager.getInstance();

    boolean uploadResult = sshManager.uploadFile(localPath, remotePath);
    boolean downloadResult = sshManager.downloadFile(remotePath, localPath);
    return uploadResult && downloadResult ? Result.success() : Result.failure();
  }

  private Result performGitOperation(String operation) {
    GitManager gitManager = new GitManager();
    gitManager.initializeRepository("/path/to/local/repo");

    switch (operation) {
      case "COMMIT":
        return gitManager.commitChanges("Scheduled commit") ? Result.success() : Result.failure();
      case "PUSH":
        return Result.failure(
            new Data.Builder().putString("error", "Push not implemented").build());
      default:
        return Result.failure();
    }
  }
}
