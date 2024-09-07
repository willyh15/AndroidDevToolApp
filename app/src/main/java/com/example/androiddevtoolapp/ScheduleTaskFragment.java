import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class ScheduleTaskFragment extends Fragment {

    private Spinner spinnerTaskType;
    private EditText editTextTaskDetails, editTextDelay;
    private Button buttonScheduleTask;
    private ProgressBar progressBar;

    public ScheduleTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_task, container, false);

        // Initialize UI components
        spinnerTaskType = view.findViewById(R.id.spinnerTaskType);
        editTextTaskDetails = view.findViewById(R.id.editTextTaskDetails);
        editTextDelay = view.findViewById(R.id.editTextDelay);
        buttonScheduleTask = view.findViewById(R.id.buttonScheduleTask);
        progressBar = view.findViewById(R.id.progressBar);  // ProgressBar for loading feedback
        progressBar.setVisibility(View.GONE);  // Initially hidden

        // Set up the spinner with task types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.task_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskType.setAdapter(adapter);

        buttonScheduleTask.setOnClickListener(v -> {
            String taskType = spinnerTaskType.getSelectedItem().toString();
            String taskDetails = editTextTaskDetails.getText().toString();
            String delayStr = editTextDelay.getText().toString();

            if (taskDetails.isEmpty() || delayStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long delay = Long.parseLong(delayStr);
            progressBar.setVisibility(View.VISIBLE);  // Show ProgressBar while scheduling
            scheduleTask(taskType, taskDetails, delay);
        });

        return view;
    }

    private void scheduleTask(String taskType, String taskDetails, long delay) {
        Data inputData = new Data.Builder()
                .putString(ScheduledTaskWorker.TASK_TYPE, taskType)
                .putString(ScheduledTaskWorker.TASK_DETAILS, taskDetails)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ScheduledTaskWorker.class)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .setInputData(inputData)
                .build();

        WorkManager workManager = WorkManager.getInstance(getContext());
        workManager.enqueue(workRequest);

        // Observe the task's progress
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo != null) {
                if (workInfo.getState().isFinished()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Task completed!", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
