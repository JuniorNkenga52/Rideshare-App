package com.app.rideshare.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.rideshare.model.ContactBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class AppUtils {

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getDeviceWidth(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Point point = new Point();
            wm.getDefaultDisplay().getSize(point);
            return point.x;
        } else {
            return wm.getDefaultDisplay().getWidth();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    public static boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return emailPattern.matcher(email).find();
    }

    public static void showMessage(Activity activity, boolean isSuccess, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        //showCustomToast(activity_waiting, message, R.drawable.ic_camera);
    }

    public static int dp2px(int dp, Context ctx) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public static ArrayList<ContactBean> readContacts(Context mContext) {
        ArrayList<ContactBean> mlist = new ArrayList<>();

        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String phone = null;


        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    ContactBean bean = new ContactBean();
                    bean.setName(name);

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        bean.setMobile(phone);
                    }

                    mlist.add(bean);
                    pCur.close();
                }
            }
        }
        return mlist;
    }

    public static String dateformat(String time)
    {
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String outputPattern = "EEE, dd MMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}