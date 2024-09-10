package com.example.androiddevtoolapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubActionsFragment extends Fragment {

    private Button buttonListWorkflows, buttonTriggerWorkflow;
    private ListView listViewWorkflows;
    private ProgressBar progressBar;  // Added ProgressBar for loading indication
    private GitHubService gitHubService;
    private List<Workflow> workflowList = new ArrayList<>();
    private String selectedWorkflowId = "";

    public GitHubActionsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_github_actions, container, false);

        // Initialize UI components
        buttonListWorkflows = view.findViewById(R.id.buttonListWorkflows);
        buttonTriggerWorkflow = view.findViewById(R.id.buttonTriggerWorkflow);
        listViewWorkflows = view.findViewById(R.id.listViewWorkflows);
        progressBar = view.findViewById(R.id.progressBar);  // Initialize ProgressBar
        progressBar.setVisibility(View.GONE);  // Initially hide ProgressBar

        // Set up Retrofit for GitHub API
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    // Add the OAuth token to the request
                    return chain.proceed(chain.request().newBuilder()
                            .header("Authorization", "Bearer YOUR_GITHUB_TOKEN")
                            .build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubService = retrofit.create(GitHubService.class);

        // List workflows on button click
        buttonListWorkflows.setOnClickListener(v -> {
            listWorkflows("your-github-username", "your-repo-name");
        });

        // Trigger workflow on button click
        buttonTriggerWorkflow.setOnClickListener(v -> {
            if (!selectedWorkflowId.isEmpty()) {
                triggerWorkflow("your-github-username", "your-repo-name", selectedWorkflowId);
            } else {
                Toast.makeText(getContext(), "Please select a workflow to trigger", Toast.LENGTH_SHORT).show();
            }
        });

        listViewWorkflows.setOnItemClickListener((parent, view1, position, id) -> {
            selectedWorkflowId = String.valueOf(workflowList.get(position).getId());
            Toast.makeText(getContext(), "Selected Workflow ID: " + selectedWorkflowId, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void listWorkflows(String owner, String repo) {
        progressBar.setVisibility(View.VISIBLE);  // Show ProgressBar while loading

        gitHubService.listWorkflows(owner, repo).enqueue(new Callback<WorkflowsResponse>() {
            @Override
            public void onResponse(Call<WorkflowsResponse> call, Response<WorkflowsResponse> response) {
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar after response

                if (response.isSuccessful() && response.body() != null) {
                    workflowList = response.body().getWorkflows();
                    List<String> workflowNames = new ArrayList<>();
                    for (Workflow workflow : workflowList) {
                        workflowNames.add(workflow.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, workflowNames);
                    listViewWorkflows.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to list workflows: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkflowsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar after failure
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void triggerWorkflow(String owner, String repo, String workflowId) {
        progressBar.setVisibility(View.VISIBLE);  // Show ProgressBar during the API call

        WorkflowDispatch dispatch = new WorkflowDispatch("main"); // or other branch
        gitHubService.triggerWorkflow(owner, repo, workflowId, dispatch).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar after response

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Workflow triggered successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to trigger workflow: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar after failure
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
