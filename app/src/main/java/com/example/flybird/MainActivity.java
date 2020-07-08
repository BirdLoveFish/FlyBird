package com.example.flybird;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flybird.Tools.MyDbOpenHelper;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Button btnBankSafeBox;
    private Button btnAccountSafeBox;
    private String TAG = "MainActivity";
    private SQLiteDatabase db;
    private MyDbOpenHelper myDbHelper;

    private Button btnNotification;

    private Button btnServerStart;
    private Button btnServerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        myDbHelper = new MyDbOpenHelper(context, MyDbOpenHelper.DB_NAME, null, MyDbOpenHelper.DB_VERSION);
        db = myDbHelper.getWritableDatabase();

        MyListener listener = new MyListener();

        btnBankSafeBox = (Button)findViewById(R.id.btn_bank_safe_box);
        btnBankSafeBox.setOnClickListener(listener);

        btnAccountSafeBox = (Button)findViewById(R.id.btn_account_safe_box);
        btnAccountSafeBox.setOnClickListener(listener);

        btnNotification = (Button)findViewById(R.id.btn_notification);
        btnNotification.setOnClickListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.info_item:
                showSetAppInfoDialog();
                break;
            case R.id.set_password:
                showSetPasswordDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSetAppInfoDialog(){
        String urlAddress = null;
        Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{"URLADDRESS"}, null, null, null);
        if(cursor.moveToNext()){
            urlAddress = cursor.getString(cursor.getColumnIndex("VALUE"));
        }
        cursor.close();

        LayoutInflater factory = LayoutInflater.from(context);
        final View appInfo = factory.inflate(R.layout.dialog_app_info_insert, null);
        final EditText textUrlAddress = (EditText) appInfo.findViewById(R.id.dialog_app_info_insert_url);

        textUrlAddress.setText(urlAddress);

        final String finalUrlAddress = urlAddress;
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle("查看或设置App信息")
                .setView(appInfo)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canCloseDialog(dialog, true);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String updateUrlAddress = textUrlAddress.getText().toString();

                        if(finalUrlAddress != updateUrlAddress){
                            ContentValues values = new ContentValues();
                            values.put("VALUE", textUrlAddress.getText().toString());
                            db.update("SYSTEM", values, "NAME=?", new String[]{"URLADDRESS"});
                        }

                        canCloseDialog(dialog, true);
                    }
                }).show();
    }

    /*
    *       设置密码的弹窗
    *
    * */
    private void showSetPasswordDialog(){

        String password = null;
        Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{"PASSWORD"}, null, null, null);
        if(cursor.moveToNext()){
            password = cursor.getString(cursor.getColumnIndex("VALUE"));
        }
        cursor.close();

        if(password == null || "".equals(password)){

            LayoutInflater factory = LayoutInflater.from(context);
            final View passwordSetting = factory.inflate(R.layout.dialog_password_setting, null);
            final EditText editPasswordOriginal = (EditText) passwordSetting.findViewById(R.id.dialog_password_setting_pwd);
            final EditText editPasswordConfirm = (EditText) passwordSetting.findViewById(R.id.dialog_password_setting_pwd_confirm);
            final EditText editPasswordTips = (EditText) passwordSetting.findViewById(R.id.dialog_password_setting_tips);

            new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle("设置密码")
                .setMessage("请输入密码")
                .setView(passwordSetting)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show();
                        canCloseDialog(dialog, true);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String passwordOriginal = editPasswordOriginal.getText().toString();
                        String passwordConfirm = editPasswordConfirm.getText().toString();
                        String passwordTips = editPasswordTips.getText().toString();

                        if("".equals(passwordOriginal) || "".equals(passwordConfirm)){
                            Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
                            canCloseDialog(dialog, false);
                            return;
                        }

                        if(!passwordOriginal.equals(passwordConfirm)){
                            Toast.makeText(context, "密码不一致", Toast.LENGTH_SHORT).show();
                            canCloseDialog(dialog, false);
                            return;
                        }

                        ContentValues values1 = new ContentValues();
                        values1.put("VALUE", passwordOriginal);
                        db.update("SYSTEM", values1, "NAME=?" , new String[]{"PASSWORD"});

                        ContentValues values2 = new ContentValues();
                        values2.put("VALUE", passwordTips);
                        db.update("SYSTEM", values2, "NAME=?" , new String[]{"PASSWORD_TIPS"});
                        Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                        canCloseDialog(dialog, true);
                    }
                }).show();
        }else{

            LayoutInflater factory = LayoutInflater.from(context);
            final View passwordChange = factory.inflate(R.layout.dialog_password_change, null);
            final EditText editPasswordOriginal = (EditText) passwordChange.findViewById(R.id.dialog_password_change_pwd_original);
            final EditText editPasswordNew = (EditText) passwordChange.findViewById(R.id.dialog_password_change_pwd_new);
            final EditText editPasswordConfirm = (EditText) passwordChange.findViewById(R.id.dialog_password_change_pwd_confirm);
            final EditText editPasswordTips = (EditText) passwordChange.findViewById(R.id.dialog_password_change_tips);

            final String finalPassword = password;
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("设置密码")
                    .setMessage("请输入密码")
                    .setView(passwordChange)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show();
                            canCloseDialog(dialog, true);
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String passwordOriginal = editPasswordOriginal.getText().toString();
                            String passwordNew = editPasswordNew.getText().toString();
                            String passwordConfirm = editPasswordConfirm.getText().toString();
                            String passwordTips = editPasswordTips.getText().toString();

                            if("".equals(passwordOriginal) || "".equals(passwordNew) || "".equals(passwordConfirm)){
                                Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
                                canCloseDialog(dialog, false);
                                return;
                            }

                            if(!passwordOriginal.equals(finalPassword)){
                                Toast.makeText(context, "旧密码错误", Toast.LENGTH_SHORT).show();
                                canCloseDialog(dialog, false);
                                return;
                            }

                            if(passwordOriginal.equals(passwordNew)){
                                Toast.makeText(context, "新旧密码相同", Toast.LENGTH_SHORT).show();
                                canCloseDialog(dialog, false);
                                return;
                            }

                            if(!passwordNew.equals(passwordConfirm)){
                                Toast.makeText(context, "新密码与确认密码不一致", Toast.LENGTH_SHORT).show();
                                canCloseDialog(dialog, false);
                                return;
                            }

                            ContentValues values1 = new ContentValues();
                            values1.put("VALUE", passwordNew);
                            db.update("SYSTEM", values1, "NAME=?" , new String[]{"PASSWORD"});

                            ContentValues values2 = new ContentValues();
                            values2.put("VALUE", passwordTips);
                            db.update("SYSTEM", values2, "NAME=?" , new String[]{"PASSWORD_TIPS"});
                            Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                            canCloseDialog(dialog, true);

                        }
                    }).show();
        }
    }

    private void canCloseDialog(DialogInterface dialogInterface, boolean close){
        try{
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right, menu);
        return true;
    }

    public class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_bank_safe_box:
                    Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{getResources().getString(R.string.s_db_validate_time)}, null, null, null);

                    long value = 0;
                    if(cursor.moveToNext()){
                        value = cursor.getLong(cursor.getColumnIndex("VALUE"));
                    }
                    cursor.close();

                    long time = Calendar.getInstance().getTimeInMillis();
                    Intent intent2 = new Intent(context, BankSafeBoxActivity.class);
                    //2分钟 =  2 * 60秒 * 1000 = 120000毫秒
                    if((time - value) > 120000){
                        showValidatePwdDialog(intent2);
                    }else{
                        startActivity(intent2);
                    }
                    break;
                case R.id.btn_account_safe_box:

                    Cursor cursor2 = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{getResources().getString(R.string.s_db_validate_time)}, null, null, null);

                    long value2 = 0;
                    if(cursor2.moveToNext()){
                        value2 = cursor2.getLong(cursor2.getColumnIndex("VALUE"));
                    }
                    cursor2.close();

                    long time2 = Calendar.getInstance().getTimeInMillis();
                    Intent intent3 = new Intent(context, AccountSafeBoxActivity.class);
                    //2分钟 =  2 * 60秒 * 1000 = 120000毫秒
                    if((time2 - value2) > 120000){
                        showValidatePwdDialog(intent3);
                    }else{
                        startActivity(intent3);
                    }
                    break;
                case R.id.btn_notification:
//                    Intent intent4 = new Intent(context, NotificationActivity.class);
//                    startActivity(intent4);

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("xxxABC",true);
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("xxxEFG", xxxEFG);
                    intent.putExtras(bundle);


                    NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    //需添加的代码
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        String channelId = "default";
                        String channelName = "默认通知";
                        manager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
                    }
                    //
                    Notification notification =new NotificationCompat.Builder(context,"default")
                            .setContentTitle("This is content title")
                            .setContentText("this is content text")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                            .setContentIntent(PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                            .build()
                            ;
                    manager.notify(1,notification);


                    Log.d(TAG, "onClick: " + "点了");
                    break;
                case R.id.service_start:
                    startService(new Intent(getBaseContext(), MyService.class));
                    break;
                case R.id.service_end:
                    stopService(new Intent(getBaseContext(), MyService.class));
                    break;
            }
        }
    }

    /*
            校验密码的弹窗
     */
    private void showValidatePwdDialog(final Intent intent){
        String password = null;
        Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{"PASSWORD"}, null, null, null);
        if(cursor.moveToNext()){
            password = cursor.getString(cursor.getColumnIndex("VALUE"));
        }
        cursor.close();

        LayoutInflater factory = LayoutInflater.from(context);
        final View passwordInput = factory.inflate(R.layout.dialog_password_input, null);
        final EditText editPassword = (EditText) passwordInput.findViewById(R.id.dialog_et_input_password);
        final TextView textPassword = (TextView) passwordInput.findViewById(R.id.dialog_tv_input_password);
        final TextView textTips = (TextView) passwordInput.findViewById(R.id.dialog_tv_input_password_tips);

        textPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{"PASSWORD_TIPS"}, null, null, null);
                if(cursor.moveToNext()){
                    String tips = cursor.getString(cursor.getColumnIndex("VALUE"));
                    Log.d(TAG, "onClick: " + tips);
                    textTips.setText(tips);
                }
                cursor.close();
            }
        });

        final String finalPassword = password;
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle("请输入密码")
                .setView(passwordInput)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editPassword.getText().toString().equals(finalPassword)){
                            long time = Calendar.getInstance().getTimeInMillis();
                            ContentValues values = new ContentValues();
                            values.put("NAME", getResources().getString(R.string.s_db_validate_time));
                            values.put("VALUE", time);
                            db.update("SYSTEM", values, "NAME=?", new String[]{getResources().getString(R.string.s_db_validate_time)});

                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, "密码错误,请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

}