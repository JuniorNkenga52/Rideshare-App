package com.app.rideshare.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SendOTPResponse;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MobileNumberActivity extends AppCompatActivity {
    private TextView mTitleTv;
    private EditText mMobileEt;
    private TextView mInfoTv;

    private TextView mSendOTPTv;

    //private Typeface mRobotoRegular;
    CustomProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_number_activity);
        mProgressDialog = new CustomProgressDialog(this);

        PrefUtils.initPreference(this);

        //mRobotoRegular = TypefaceUtils.getOpenSansRegular(this);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mMobileEt = (EditText) findViewById(R.id.mobile_et);
        mInfoTv = (TextView) findViewById(R.id.title_number_tv);

        /*mTitleTv.setTypeface(mRobotoRegular);
        mMobileEt.setTypeface(mRobotoRegular);
        mInfoTv.setTypeface(mRobotoRegular);*/

        mSendOTPTv=(TextView)findViewById(R.id.send_code_tv);
        //mSendOTPTv.setTypeface(mRobotoRegular);

        mSendOTPTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mMobileEt.getText().toString().isEmpty()){
                    ToastUtils.showShort(MobileNumberActivity.this,"Please enter mobile number.");
                }else{
                    sendOTP(mMobileEt.getText().toString(),PrefUtils.getUserInfo().getmUserId());
                }

            }
        });

    }

    private void sendOTP(final String mobileNuber, String nUserId)
    {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).sendOTP(mobileNuber, nUserId).enqueue(new Callback<SendOTPResponse>() {
            @Override
            public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null)
                {
                    User bean=PrefUtils.getUserInfo();
                    bean.setmMobileNo(mobileNuber);
                    PrefUtils.addUserInfo(bean);

                    Intent i=new Intent(MobileNumberActivity.this,VerifyMobileNumberActivity.class);
                    startActivity(i);
                    finish();
                } else {
                        ToastUtils.showShort(MobileNumberActivity.this,"Please try againg..");
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}