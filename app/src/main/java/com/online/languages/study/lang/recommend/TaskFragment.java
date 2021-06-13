package com.online.languages.study.lang.recommend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.CustomDataListActivity;
import com.online.languages.study.lang.ExerciseActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.SectionActivity;
import com.online.languages.study.lang.SectionStatsActivity;
import com.online.languages.study.lang.adapters.HintDialog;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.databinding.FragmentTaskBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.recommend.Task.TASK_TYPE_COMPLETED;


public class TaskFragment extends Fragment {


    public static final String ACTION_SKIP = "skip";
    public static final String ACTION_BLOCK = "block";
    public static final String ACTION_DOWNGRADE = "downgrade";
    public static final String ACTION_CLEAR_DATA = "clear_data";
    public static final String ACTION_CLEAR_ALL_DATA = "clear_all_data";
    public static final String ACTION_OPEN_INFO = "open_info";

    public static final String ACTION_SECTION= "sections";
    public static final String ACTION_SUGGEST= "suggest";
    public static final String ACTION_SECTION_STATS= "section_stats";


    private FragmentTaskBinding binding;

    OpenActivity openActivity;

    ArrayList<String> list;
    ArrayList<TaskItem> tasks;

    RecyclerView recyclerView;

    NavStructure navStructure;
    DataManager dataManager;

    RecommendListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    RecommendTask task;

    TaskItem expectedTask;

    Context activityContext;

    AsyncTaskData asyncTask;

    ArrayList<TaskItem> oldList;

    View progressBar;
    String progressCurrentStatus = "unknown";
    boolean fragmentIsVisible = true;


    public TaskFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        activityContext = getActivity();

        openActivity = new OpenActivity(activityContext);
        openActivity.setOrientation();

        dataManager = new DataManager(activityContext);

        task = new RecommendTask(activityContext);

        resetExpected();

       // ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        initToolBarProgress();

        navStructure  = new DataManager(activityContext).getNavStructure();

        recyclerView = binding.recyclerView;
        //adapter = new RecommendListAdapter(this, tasks);
        mLayoutManager  = new LinearLayoutManager(activityContext);
        recyclerView.setLayoutManager(mLayoutManager);

        tasks = new ArrayList<>();

        //updateList();

        displayUpdateProgress("start");

        new Handler().postDelayed(this::updateList, 300);

        return view;

    }


    private void updateList() {

        oldList = new ArrayList<>();

        oldList.addAll(tasks);

        // getDataAndRefreshList(oldList);
        new Handler().postDelayed(this::checkSections, 100);


    }

    private void getDataAndRefreshList(ArrayList<TaskItem> oldList) {

        //getDataList();

        checkListsUpdates(oldList, tasks);

        if (!oldList.equals(tasks)) updateTasksList();

    }

    private void updateTasksList() {

        adapter = new RecommendListAdapter(activityContext, tasks) {
            @Override
            public void clickOnTask(@NonNull String action, int taskPosition) {
                openTask(action, taskPosition);
            }

            @Override
            public void clickOnMenu(int taskPosition) {
                openTaskDialog(taskPosition);
            }

            @Override
            public void clickOnSection(@NotNull String sectionId) {
                clickTaskSection(sectionId);
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void getDataList() {

        task.setExpectedTaskItem(expectedTask);
        tasks = task.getRecommendations();

    }


    public void clickTaskSection(String sectionId) {

        Intent i = new Intent(activityContext, SectionActivity.class);
        openActivity.openSection(i, navStructure, sectionId, "root");

    }

    private void openTask(String action, int position) {

        switch (action) {
            case ACTION_SECTION_STATS:
                openSectionStats(position);
                break;
            case ACTION_SUGGEST:
                openSuggest(position);
                break;
            case ACTION_OPEN_INFO:
                openTasksInfo();
                break;
            case ACTION_CLEAR_ALL_DATA:
                openClearConfirmDialog();
                break;
            default:
                openTask(position);
                break;
        }

    }

    private void openSuggest(int position) {

        //final TaskItem taskItem = tasks.get(position);

        InfoDialog infoDialog = new InfoDialog(activityContext);
        infoDialog.simpleDialog("Suggestion", PARAM_EMPTY);

    }


    private void openTasksInfo() {
        HintDialog infoDialog = new HintDialog(activityContext);
        infoDialog.showCustomDialog(getString(R.string.task_info_dialog_title), getString(R.string.task_info_dialog_text));
    }


    private void openTask(int position) {
        final TaskItem taskItem = tasks.get(position);

        expectedTask = taskItem;

        if (taskItem.getType().equals(TASK_TYPE_COMPLETED)) {

            testPage(taskItem);

        } else if (taskItem.getType().equals("section")) {

            clickTaskSection(taskItem.getId());

        }else if (taskItem.getId().equals("all_errors")) {

            openErrors();

        } else {
            if (taskItem.getProgress() > 10) openActivity.openExTab = true;

            openActivity.openFromViewCat(navStructure, taskItem.getViewCategory().sectionId, taskItem.getViewCategory());
            openActivity.openExTab = false;
        }
    }


    public void openSectionStats(int position) {

        final TaskItem taskItem = tasks.get(position);

        Intent i = new Intent(activityContext, SectionStatsActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, taskItem.getSectionStats().getSectionId());
        i.putExtra(Constants.EXTRA_SECTION_NUM, 0);
        //i.putExtra("from_section", PARAM_EMPTY);
        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    private void openErrors() {

        Intent i = new Intent(activityContext, CustomDataListActivity.class);
        i.putParcelableArrayListExtra("dataItems", task.getErrorsList());
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, "errors");
        startActivityForResult(i, 25);
        openActivity.pageTransition();
    }

    private void openTaskDialog(int position ) {

        final TaskItem taskItem = tasks.get(position);

        TaskActionDialog taskActionDialog= new TaskActionDialog(activityContext, taskItem, position) {

            @Override
            public void callBack(@NotNull String action, int position) {
                manageTask(action, position);
            }
        };

        taskActionDialog.createDialog();

    }

    private void manageTask(String action, int position) {
        saveTaskFromList(position, action);
    }


    private void saveTaskFromList(int position, String action) {

        int priority = 0;
        String taskItemID = tasks.get(position).getId();

        if (action.equals(ACTION_CLEAR_DATA)) {
            String taskSectionID = tasks.get(position).getSectionStats().getSectionId();
            clearSectionData(taskSectionID);

        } else {

            if (action.equals(ACTION_DOWNGRADE)) {

                String taskSectionID = tasks.get(position).getSectionStats().getSectionId();
                priority = -5;
                taskItemID = taskSectionID;


            } else {

                if (action.equals(ACTION_BLOCK)) priority = -10;

                animateTaskInList(position);
            }

            saveTaskAndUpdate(priority, taskItemID);
        }
    }

    private void clearSectionData(String taskItemID) {

        dataManager.dbHelper.deleteTasksWithId(taskItemID);
        new Handler().postDelayed(this::updateList, 100);

    }

    private void saveTaskAndUpdate(int priority, String taskItemID) {

        new Handler().postDelayed(() -> {

            dataManager.dbHelper.saveTask(taskItemID, priority);
            updateList();

        }, 100);
    }

    private void animateTaskInList(int position) {
        View v = mLayoutManager.findViewByPosition(position);
        v.animate().alpha(0f).setDuration(130);
    }


    private void testPage(TaskItem taskItem) {

        Intent i = new Intent(activityContext, ExerciseActivity.class) ;

        ArrayList<String> ids = new ArrayList<>();

        for (ViewCategory cat : taskItem.getSectionsData() ) {
            ids.add(cat.id);
        }

        String[] stringArray = ids.toArray(new String[0]);

        i.putExtra("ex_type", 1);
        i.putExtra(Constants.EXTRA_SECTION_ID, taskItem.getSectionsData().get(0).sectionId);
        i.putExtra(Constants.EXTRA_CAT_TAG, taskItem.getId());
        i.putExtra("ids", stringArray);
        i.putParcelableArrayListExtra("dataItems", new ArrayList<>());

        startActivityForResult(i, 1);

        openActivity.pageTransition();
    }


    private void checkExpectedTask(TaskItem expectedTask, TaskItem newTask) {

        TaskCompleteDialog taskCompleteDialog = new TaskCompleteDialog(activityContext, binding, expectedTask, newTask);
        taskCompleteDialog.checkTask();

        resetExpected();

    }

    private void resetExpected() {
        expectedTask = new TaskItem("", 0, 0, "expected_reset");
        task.resetExpected();
    }


    private ArrayList<TaskItem> checkListsUpdates(ArrayList<TaskItem> oldList, ArrayList<TaskItem> newList) {

        if (expectedTask.getId().equals( task.getExpectedTaskUpdated().getId() )) {
            checkExpectedTask(expectedTask, task.getExpectedTaskUpdated());
        }

        for (int n = 0; n < newList.size(); n++) {
            TaskItem newTask = newList.get(n);

            newTask.setStatus("normal");

            if (oldList.size() > n ) {

                if (!oldList.get(n).getId().equals(newTask.getId())) {
                    newTask.setStatus("changed");
                }

            } else {
                break;
            }
        }

        return newList;
    }


    private void clearAllTasksData() {

        dataManager.dbHelper.deleteAllTasks();
        updateList();

        Toast.makeText(activityContext, R.string.tasks_clear_data_msg, Toast.LENGTH_SHORT).show();

    }


    private void openClearConfirmDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);

        builder.setTitle(R.string.tasks_clear_data_confirm_title);
        builder.setMessage(R.string.tasks_clear_data_confirm_text);

        builder.setCancelable(true);
        builder.setPositiveButton(R.string.continue_txt, (dialog, which) -> clearAllTasksData() );
        builder.setNegativeButton(R.string.cancel_txt, (dialog, which) -> {  });

        builder.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        updateList();

    }


    private void checkSections() {
        if(asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING)
            asyncTask.cancel(true);

        asyncTask = new AsyncTaskData();
        asyncTask.execute();

    }

    public class AsyncTaskData extends AsyncTask<String, String[], String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            displayUpdateProgress("restart");
        }

        @Override
        protected String[] doInBackground(String... urls) {

            String[] str = {"0", "init"};

            try {

                getDataList();
                str[0] = "list updated";

            } catch (Exception e) {
                e.printStackTrace();
                str[0] = "failed";
            }

            return str;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            if (!isCancelled()) {

                postResults();

            }
        }

    }

    private void postResults() {

        displayUpdateProgress("end");

        new Handler().postDelayed(() -> getDataAndRefreshList(oldList),
                30);

    }

    @Override
    public void onStop() {

        super.onStop();

        displayUpdateProgress("stop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            asyncTask.cancel(true);
        }

    }

    private void initToolBarProgress() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null)
            progressBar = toolbar.findViewById(R.id.toolbar_progress_bar);
    }


    private void displayUpdateProgress(String status) {

        if (status.equals("start")) {

            binding.boxLoad.setVisibility(View.VISIBLE);
            progressCurrentStatus = "started";

        } else {


            if ((progressBar!=null) && (!progressCurrentStatus.equals("started")) ) {

                if (status.equals("restart")) {

                  if (fragmentIsVisible)  progressBar.setVisibility(View.VISIBLE);

                } else {
                    int time = 250;
                    if (status.equals("stop")) time = 0;

                    new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE),
                            time);
                }

            }

            if (status.equals("end")) {
                binding.boxLoad.setVisibility(View.GONE);
            }

            progressCurrentStatus = "finished";

        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        fragmentIsVisible = isVisibleToUser;

    }
}
