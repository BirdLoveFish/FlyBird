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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flybird.Models.Bank;
import com.example.flybird.Tools.MyDbOpenHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class BankSafeBoxActivity extends AppCompatActivity {

    private Context context;
    private Button back;
    private Button setting;
    private TextView title;
    private TextView name;
    private TextView owner;
    private SQLiteDatabase db;
    private MyDbOpenHelper myDbOpenHelper;
    private MyListener myListener;
    private RecyclerView recyclerView;
    private BankAdapter adapter;
    private AlertDialog alert;
    private List<Bank> bankList = new LinkedList<>();
    private boolean nameOrder = false;
    private boolean ownerOrder = true;

    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bank_safe_box);

        context = this;
        myDbOpenHelper = new MyDbOpenHelper(context, "flyBird.db", null, 1);
        back = findViewById(R.id.safe_box_back);
        setting = findViewById(R.id.safe_box_setting);
        recyclerView = findViewById(R.id.bank_list);
        title = findViewById(R.id.safe_box_title);
        name = findViewById(R.id.bank_safe_box_name);
        owner = findViewById(R.id.bank_safe_box_owner);

        myListener = new MyListener();
        window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorGrey));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Query();

        title.setText(getResources().getText(R.string.s_bank_safe_box));

        back.setOnClickListener(myListener);
        setting.setOnClickListener(myListener);
        name.setOnClickListener(myListener);
        owner.setOnClickListener(myListener);


    }

    private void Query(){
        db = myDbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("BANKSAFEBOX", null, null, null, null, null, "SIMPLENAME");
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("ID"));
                String fullName = cursor.getString(cursor.getColumnIndex("FULLNAME"));
                String simpleName = cursor.getString(cursor.getColumnIndex("SIMPLENAME"));
                String owner = cursor.getString(cursor.getColumnIndex("OWNER"));
                String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
                String other = cursor.getString(cursor.getColumnIndex("OTHER"));
                bankList.add(new Bank(id, fullName, simpleName, owner, password, other));
            } while (cursor.moveToNext());
        }
        cursor.close();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.divider_line));
        recyclerView.addItemDecoration(divider);
        adapter = new BankAdapter(context, bankList);
        recyclerView.setAdapter(adapter);
        return;
    }

    private class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            db = myDbOpenHelper.getWritableDatabase();
            switch (v.getId()){
                case R.id.safe_box_back:
                    finish();
                    break;
                case R.id.safe_box_setting:
                    showMenu();
                    break;
                case R.id.bank_safe_box_name:
                    Collections.sort(bankList, new Comparator<Bank>() {
                        @Override
                        public int compare(Bank o1, Bank o2) {
                            if(nameOrder){
                                return o1.getSimpleName().compareTo(o2.getSimpleName());
                            }
                            return o2.getSimpleName().compareTo(o1.getSimpleName());
                        }
                    });
                    nameOrder = !nameOrder;
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.bank_safe_box_owner:
                    Collections.sort(bankList, new Comparator<Bank>() {
                        @Override
                        public int compare(Bank o1, Bank o2) {
                            if(ownerOrder){
                                return o1.getOwner().compareTo(o2.getOwner());
                            }
                            return o2.getOwner().compareTo(o1.getOwner());
                        }
                    });
                    ownerOrder = !ownerOrder;
                    adapter.notifyDataSetChanged();
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
                        Toast.makeText(context, "备份成功", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showMenuAdd(){
        LayoutInflater factory = LayoutInflater.from(context);
        final View bankInsertView = factory.inflate(R.layout.dialog_bank_insert, null);
        final EditText editTextFullName = (EditText) bankInsertView.findViewById(R.id.dialog_insert_ed_bank_full_name);
        final EditText editTextSimpleName = (EditText) bankInsertView.findViewById(R.id.dialog_insert_ed_bank_simple_name);
        final EditText editTextOwner = (EditText) bankInsertView.findViewById(R.id.dialog_insert_ed_bank_owner);
        final EditText editTextPassword = (EditText) bankInsertView.findViewById(R.id.dialog_insert_ed_bank_password);
        final EditText editTextOther = (EditText) bankInsertView.findViewById(R.id.dialog_insert_ed_bank_other);

        final View bankTitleView = factory.inflate(R.layout.dialog_bank_title, null);

        alert = new AlertDialog.Builder(context)
                .setCustomTitle(bankTitleView)
                .setView(bankInsertView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String bankFullName = editTextFullName.getText().toString();
                        String bankSimpleName = editTextSimpleName.getText().toString();
                        String bankOwner = editTextOwner.getText().toString();
                        String bankPassword = editTextPassword.getText().toString();
                        String bankOther = editTextOther.getText().toString();

                        if("".equals(bankFullName) && "".equals(bankSimpleName) && "".equals(bankOwner) && "".equals(bankPassword) && "".equals(bankOther)){
                            Toast.makeText(context, "不能全为空", Toast.LENGTH_LONG).show();
                            return;
                        }

                        ContentValues values1 = new ContentValues();
                        values1.put("FULLNAME", bankFullName);
                        values1.put("SIMPLENAME", bankSimpleName);
                        values1.put("OWNER", bankOwner);
                        values1.put("PASSWORD", bankPassword);
                        values1.put("OTHER", bankOther);
                        int id = (int) db.insert("BANKSAFEBOX", null, values1);
                        bankList.add(new Bank((String.valueOf(id)), bankFullName, bankSimpleName, bankOwner, bankPassword, bankOwner));
                        adapter.notifyItemInserted(id - 1);
                        adapter.notifyItemRangeChanged(id, bankList.size() - id - 1);
                    }
                }).create();
        alert.show();
    }


}
