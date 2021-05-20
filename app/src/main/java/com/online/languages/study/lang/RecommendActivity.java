package com.online.languages.study.lang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.online.languages.study.lang.recommend.TaskItem;
import com.online.languages.study.lang.recommend.RecommendListAdapter;
import com.online.languages.study.lang.recommend.RecommendTask;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.databinding.ActivityRecommendBinding;
import com.online.languages.study.lang.recommend.TaskActionDialog;
import com.online.languages.study.lang.recommend.TaskCompleteDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.recommend.Task.TASK_TYPE_COMPLETED;
import static com.online.languages.study.lang.recommend.TaskFragment.ACTION_BLOCK;
import static com.online.languages.study.lang.recommend.TaskFragment.ACTION_CLEAR_DATA;
import static com.online.languages.study.lang.recommend.TaskFragment.ACTION_DOWNGRADE;
import static com.online.languages.study.lang.recommend.TaskFragment.ACTION_SECTION_STATS;
import static com.online.languages.study.lang.recommend.TaskFragment.ACTION_SUGGEST;

public class RecommendActivity extends ThemedActivity {



    OpenActivity openActivity;

    ActivityRecommendBinding binding;

    ArrayList<String> list;
    ArrayList<TaskItem> tasks;

    RecyclerView recyclerView;

    NavStructure navStructure;
    DataManager dataManager;

    RecommendListAdapter adapter;
    LayoutManager mLayoutManager;
    RecommendTask task;

    TaskItem expectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openActivity = new OpenActivity(this);
        openActivity.setOrientation();

        dataManager = new DataManager(this);

        task = new RecommendTask(this);

        resetExpected();

        //noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(this.getApplication(), noteId)).get(NoteViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend);
        binding.setLifecycleOwner(this);
        //binding.setNoteViewModel(noteViewModel);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tasks");

        navStructure  = new DataManager(this).getNavStructure();

        recyclerView = binding.recyclerView;
        //adapter = new RecommendListAdapter(this, tasks);
        mLayoutManager  = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        tasks = new ArrayList<>();

        updateList();

    }

    private void updateList() {

        ArrayList<TaskItem> oldList = new ArrayList<>();
        oldList.addAll(tasks);


        new Handler().postDelayed(() -> getDataAndRefreshList(oldList), 100);


    }

    private void getDataAndRefreshList(ArrayList<TaskItem> oldList) {
        getDataList();

        tasks = checkListsUpdates(oldList, tasks);

        adapter = new RecommendListAdapter(this, tasks) {
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
    }


    private void getDataList() {

        task.setExpectedTaskItem(expectedTask);
        tasks = task.getRecommendations();

    }


    public void clickTaskSection(String sectionId) {

        Intent i = new Intent(this, SectionActivity.class);
        openActivity.openSection(i, navStructure, sectionId, "root");

        // Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

    }

    private void openTask(String action, int position) {

        if (action.equals(ACTION_SECTION_STATS)) {
            openSectionStats(position);
        } else if (action.equals(ACTION_SUGGEST)) {
            openSuggest(position);
        } else {
            openTask(position );
        }

    }

    private void openSuggest(int position) {
        final TaskItem taskItem = tasks.get(position);

        InfoDialog infoDialog = new InfoDialog(this);
        infoDialog.simpleDialog("Совет", "Выполняйте тесты и улучшайте прогесс");

    }


    private void openTask(int position) {
        final TaskItem taskItem = tasks.get(position);

        expectedTask = taskItem;

       //Log.i("Tasks", "Task (activity): expected t -" + taskItem.getId() );

        if (taskItem.getType().equals(TASK_TYPE_COMPLETED)) {

            testPage(taskItem);

        } else if (taskItem.getType().equals("section")) {

            clickTaskSection(taskItem.getId());

        }else if (taskItem.getId().equals("all_errors")) {

            openErrors();

        } else {
            openActivity.openFromViewCat(navStructure, taskItem.getViewCategory().sectionId, taskItem.getViewCategory());
        }
    }


    public void openSectionStats(int position) {

        final TaskItem taskItem = tasks.get(position);

        Intent i = new Intent(this, SectionStatsActivity.class);

        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, taskItem.getSectionStats().getSectionId());
        i.putExtra(Constants.EXTRA_SECTION_NUM, 0);
        //i.putExtra("from_section", PARAM_EMPTY);
        startActivityForResult(i, 1);
        openActivity.pageTransition();
    }


    private void openErrors() {

        Intent i = new Intent(this, CustomDataListActivity.class);
        i.putParcelableArrayListExtra("dataItems", task.getErrorsList());
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, "errors");
        startActivityForResult(i, 25);
        openActivity.pageTransition();
    }

    private void openTaskDialog(int position ) {

        final TaskItem taskItem = tasks.get(position);

        TaskActionDialog taskActionDialog= new TaskActionDialog(this, taskItem, position) {

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

        Intent i = new Intent(this, ExerciseActivity.class) ;

        ArrayList<String> ids = new ArrayList<>();

        for (ViewCategory cat : taskItem.getSectionsData() ) {
                ids.add(cat.id);
        }

        String[] stringArray = ids.toArray(new String[0]);

        i.putExtra("ex_type", 1);
        i.putExtra(Constants.EXTRA_SECTION_ID, taskItem.getSectionsData().get(0).sectionId);

       // Log.i("Tasks", "Tasks: " + taskItem.getSectionsData().get(0).sectionId);

        i.putExtra(Constants.EXTRA_CAT_TAG, taskItem.getId());
        i.putExtra("ids", stringArray);
        i.putParcelableArrayListExtra("dataItems", new ArrayList<>());


        startActivityForResult(i, 1);

        openActivity.pageTransition();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        updateList();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity.pageBackTransition();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            openActivity.pageBackTransition();
            return true;
        }  else if (id == R.id.info_item) {
            showInfo();
            return true;
        } else if (id == R.id.delete_note) {
            int t =
            dataManager.dbHelper.deleteAllTasks();
                    Toast.makeText(this, "Cleared: " + t, Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {

        super.finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }


    private void showInfo() {
        InfoDialog infoDialog = new InfoDialog(this);
        infoDialog.simpleDialog(getString(R.string.info_notes_title), getString(R.string.info_notes_text));
    }


    private ArrayList<TaskItem> checkListsUpdates(ArrayList<TaskItem> oldList, ArrayList<TaskItem> newList) {

        Log.i("Expect", "Received (task activity): " + expectedTask.getId() + ": "  + expectedTask.getProgress() + " - "
          +  task.getExpectedTaskUpdated().getId() + ": " + task.getExpectedTaskUpdated().getProgress() );

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

    private void checkExpectedTask(TaskItem expectedTask, TaskItem newTask) {

        //expectedTask, newTask

        TaskItem t1;
        TaskItem t2;

       // t1 = new TaskItem("", 0, 95);
       // t2 = new TaskItem("", 0, 95);

        t1 = expectedTask;
        t2 = newTask;

        //TaskCompleteDialog taskCompleteDialog = new TaskCompleteDialog(this, binding, t1, t2);
       // taskCompleteDialog.checkTask();

        resetExpected();

    }

    private void resetExpected() {
        expectedTask = new TaskItem("", 0, 0, "expected_reset");
        task.resetExpected();
    }




}
