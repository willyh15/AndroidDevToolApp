import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkflowsResponse {

    @SerializedName("workflows")
    private List<Workflow> workflows;

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }
}
