package com.example.flybird;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flybird.Models.Account;
import com.example.flybird.Tools.MyDbOpenHelper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AccountSafeBoxActivity extends AppCompatActivity {

    private Context context;

    private Button back;
    private Button setting;
    private TextView title;

    private MyListener myListener;

    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private List<Account> accountList = new ArrayList<>();

    private AlertDialog alert;

    private SQLiteDatabase db;
    private MyDbOpenHelper myDbOpenHelper;

    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_safe_box);

        context = this;

        back = findViewById(R.id.safe_box_back);
        setting = findViewById(R.id.safe_box_setting);
        title = findViewById(R.id.safe_box_title);
        recyclerView = findViewById(R.id.account_list);
        myDbOpenHelper = new MyDbOpenHelper(context, MyDbOpenHelper.DB_NAME, null, MyDbOpenHelper.DB_VERSION);
        db = myDbOpenHelper.getWritableDatabase();

        myListener = new MyListener();

        back.setOnClickListener(myListener);
        setting.setOnClickListener(myListener);

        title.setText(getResources().getText(R.string.s_account_safe_box));

        window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorGrey));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Query();
    }

    private void Query(){

        Cursor cursor = db.query("ACCOUNTSAFEBOX", null, null, null, null, null, "ID");
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                String account = cursor.getString(cursor.getColumnIndex("ACCOUNT"));
                String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                String other = cursor.getString(cursor.getColumnIndex("OTHER"));
                accountList.add(new Account(id, name, account, password, other));
            } while (cursor.moveToNext());
        }
        cursor.close();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider_line));
        recyclerView.addItemDecoration(divider);
        adapter = new AccountAdapter(context, accountList);
        recyclerView.setAdapter(adapter);
    }

    private class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.safe_box_back:
                    finish();
                    break;
                case R.id.safe_box_setting:
                    showMenu();
                    break;
            }
        }
    }

    private void showMenu(){
        PopupMenu popupMenu = new PopupMenu(context, setting);
        popupMenu.getMenuInflater().inflate(R.menu.menu_bank, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_bank_add:
                        showMenuAdd();
                        break;
                    case R.id.menu_bank_backups:

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String urlAddress = null;
                                Cursor cursor = db.query("SYSTEM", new String[]{"VALUE"}, "NAME=?", new String[]{"URLADDRESS"}, null, null, null);
                                if(cursor.moveToNext()){
                                    urlAddress = cursor.getString(cursor.getColumnIndex("VALUE"));
                                }
                                cursor.close();

                                String address = null;
                                if(urlAddress.endsWith("/")){
                                    address = urlAddress + "connect";
                                }else{
                                    address = urlAddress + "/connect";
                                }
                                Log.d("TAG", "run: " + address);
                                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

                                Request request = new Request.Builder().url(address).build();

                                try {
                                    Response response = client.newCall(request).execute();

                                    if(response.isSuccessful()){
                                        String body = response.body().string();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "备份成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "备份失败,请检查服务器地址", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("TAG", "run: " + e.getMessage());
                                }
                            }
                        }).start();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showMenuAdd(){
        LayoutInflater factory = LayoutInflater.from(context);
        final View accountInsertView = factory.inflate(R.layout.dialog_account_insert, null);
        final EditText editTextName = (EditText) accountInsertView.findViewById(R.id.dialog_insert_ed_account_name);
        final EditText editTextAccount = (EditText) accountInsertView.findViewById(R.id.dialog_insert_ed_account_account);
        final EditText editTextPassword = (EditText) accountInsertView.findViewById(R.id.dialog_insert_ed_account_password);
        final EditText editTextOther = (EditText) accountInsertView.findViewById(R.id.dialog_insert_ed_account_other);

        final View accountTitleView = factory.inflate(R.layout.dialog_account_title, null);
        final TextView title = (TextView) accountTitleView.findViewById(R.id.dialog_title);
        title.setText("添加项目");

        alert = new AlertDialog.Builder(context)
                .setCustomTitle(accountTitleView)
                .setView(accountInsertView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String accountName = editTextName.getText().toString();
                        String accountAccount = editTextAccount.getText().toString();
                        String accountPassword = editTextPassword.getText().toString();
                        String accountOther = editTextOther.getText().toString();

                        if("".equals(accountName) && "".equals(accountAccount) && "".equals(accountPassword) && "".equals(accountOther)){
                            Toast.makeText(context, "不能全为空", Toast.LENGTH_LONG).show();
                            return;
                        }

                        ContentValues values1 = new ContentValues();
                        values1.put("NAME", accountName);
                        values1.put("ACCOUNT", accountAccount);
                        values1.put("PASSWORD", accountPassword);
                        values1.put("OTHER", accountOther);
                        int id = (int) db.insert("ACCOUNTSAFEBOX", null, values1);
                        accountList.add(new Account((String.valueOf(id)), accountName, accountAccount, accountPassword, accountOther));
                        adapter.notifyItemInserted(id - 1);
                        adapter.notifyItemRangeChanged(id, accountList.size() - id - 1);
                    }
                }).create();
        alert.show();
    }

}
