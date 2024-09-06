import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    @POST("/repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches")
    Call<Void> triggerWorkflow(@Path("owner") String owner, @Path("repo") String repo, @Path("workflow_id") String workflowId, @Body WorkflowDispatch dispatch);
}
