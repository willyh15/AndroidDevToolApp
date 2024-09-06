import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GitHubService {

    @Headers("Accept: application/vnd.github.v3+json")
    @POST("/user/repos")
    Call<GitHubRepo> createRepository(@Body GitHubRepo repo);

}
