package com.app.rideshare.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import com.app.rideshare.R;
import com.app.rideshare.adapter.AdminFuncitonsAdapter;
import com.app.rideshare.adapter.ChooseGroupAdapter;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.GroupListResponce;
import com.app.rideshare.api.response.MyGroupsResponce;
import com.app.rideshare.model.ChooseGroupModel;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_FunctionsActivity extends AppCompatActivity {

    private ListView list_riders;
    private AdminFuncitonsAdapter adminFuncitonsAdapter;
    private Context context;

    private ArrayList<ChooseGroupModel> mygroupdata = new ArrayList<>();
    private Spinner admin_choose_group;

    private ChooseGroupModel chooseGroupModel;
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);

        context = this;
        PrefUtils.initPreference(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_fun_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Functions");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        list_riders = (ListView) findViewById(R.id.list_riders);
        mProgressDialog = new CustomProgressDialog(this);



        my_group_data(PrefUtils.getUserInfo().getmUserId());
        group_data("35", PrefUtils.getUserInfo().getmUserId());
        admin_choose_group = (Spinner) findViewById(R.id.admin_choose_group);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void my_group_data(final String user_id) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).mygroups(user_id).enqueue(new Callback<GroupListResponce>() {
            @Override
            public void onResponse(Call<GroupListResponce> call, Response<GroupListResponce> response) {
                ToastUtils.showShort(Admin_FunctionsActivity.this, "Success");
                if (response.isSuccessful() && response.body() != null) {

                    for (int i = 0; i < response.body().getResult().size(); i++) {
                        int group_id = response.body().getResult().get(i).getId();
                        String name = response.body().getResult().get(i).getGroup_name();
                        chooseGroupModel = new ChooseGroupModel(group_id, name);

                        mygroupdata.add(chooseGroupModel);
                    }
                    bindSpinner(admin_choose_group, mygroupdata.get(0).getGroup_name());
                    mProgressDialog.dismiss();
                }


            }

            @Override
            public void onFailure(Call<GroupListResponce> call, Throwable t) {
                ToastUtils.showShort(Admin_FunctionsActivity.this, "Problem in Retrieving data");
            }
        });
        {

        }
    }

    private void group_data(String group_id, String user_id) {
        ApiServiceModule.createService(RestApiInterface.class).groupusers(group_id, user_id).enqueue(new Callback<MyGroupsResponce>() {
            @Override
            public void onResponse(Call<MyGroupsResponce> call, Response<MyGroupsResponce> response) {
                ToastUtils.showShort(Admin_FunctionsActivity.this, "Success");
                if (response.isSuccessful() && response.body() != null) {
                    PrefUtils.addAdminInfo(response.body().getResult().get(0));
                    adminFuncitonsAdapter = new AdminFuncitonsAdapter(context, response.body().getResult());
                    list_riders.setAdapter(adminFuncitonsAdapter);
                }
            }

            @Override
            public void onFailure(Call<MyGroupsResponce> call, Throwable t) {
                ToastUtils.showShort(Admin_FunctionsActivity.this, "Problem in Retrieving data");
            }
        });
    }

    public void bindSpinner(final Spinner spinner, final String value) {
        spinner.setAdapter(new ChooseGroupAdapter(this, mygroupdata));
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(mygroupdata.indexOf(value));
            }
        });
    }
}
