import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GitHubService {

    @Headers("Accept: application/vnd.github.v3+json")
    @POST("/user/repos")
    Call<GitHubRepo> createRepository(@Body GitHubRepo repo);

    // List workflows
    @Headers("Accept: application/vnd.github.v3+json")
    @GET("/repos/{owner}/{repo}/actions/workflows")
    Call<WorkflowsResponse> listWorkflows(@Path("owner") String owner, @Path("repo") String repo);

    // Trigger workflow
    @Headers("Accept: application/vnd.github.v3+json")
    @PUT("/repos/{owner}/{repo}/contents/{path}")
    Call<Void> uploadFile(@Path("owner") String owner,
                          @Path("repo") String repo,
                          @Path("path") String path,
                          @Body RequestBody body);
}
