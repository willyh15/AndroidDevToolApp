import com.google.gson.annotations.SerializedName;

public class GitHubRepo {

  @SerializedName("name")
  private String name;

  @SerializedName("description")
  private String description;

  @SerializedName("private")
  private boolean isPrivate;

  public GitHubRepo(String name, String description, boolean isPrivate) {
    this.name = name;
    this.description = description;
    this.isPrivate = isPrivate;
  }

  // Getters and setters
}
