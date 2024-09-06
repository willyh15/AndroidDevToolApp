import com.google.gson.annotations.SerializedName;

public class WorkflowDispatch {

    @SerializedName("ref")
    private String ref;

    public WorkflowDispatch(String ref) {
        this.ref = ref;
    }

    // Getter and setter
}
