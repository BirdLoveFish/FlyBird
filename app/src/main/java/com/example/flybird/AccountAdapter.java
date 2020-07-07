package com.example.flybird;

import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flybird.Models.Account;
import com.example.flybird.Tools.MyDbOpenHelper;

import java.util.List;

public class AccountAdapter extends SlideRecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<Account> accountList;
    private Context context;
    private AlertDialog alert;

    private SQLiteDatabase db;
    private MyDbOpenHelper myDbOpenHelper;

    public AccountAdapter(Context context, List<Account> accountList){
        this.context = context;
        this.accountList = accountList;

        myDbOpenHelper = new MyDbOpenHelper(context, MyDbOpenHelper.DB_NAME, null, MyDbOpenHelper.DB_VERSION);
        db = myDbOpenHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, final int position) {
        Account account = accountList.get(position);
        holder.account_name.setText(account.getName());
        holder.account_account.setText(String.valueOf(account.getAccount()));

        holder.account_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyAccount(position);
            }
        });

        holder.account_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView account_name;
        TextView account_account;
//        TextView account_tips;
        ImageButton account_edit;
        ImageButton account_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            account_name = itemView.findViewById(R.id.account_name);
            account_account = itemView.findViewById(R.id.account_account);
//            account_tips = itemView.findViewById(R.id.account_tips);
            account_edit = itemView.findViewById(R.id.account_edit);
            account_delete = itemView.findViewById(R.id.account_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, accountList.get(getLayoutPosition()).getId(), Toast.LENGTH_SHORT).show();

                    LayoutInflater factory = LayoutInflater.from(context);
                    final View accountShowView = factory.inflate(R.layout.dialog_account_show, null);
                    final TextView textViewName = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_account_name);
                    final TextView textViewAccount = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_account_account);
                    final TextView textViewPassword = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_account_password);
                    final TextView textViewOther = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_account_other);

                    textViewName.setText(accountList.get(getLayoutPosition()).getName());
                    textViewAccount.setText(accountList.get(getLayoutPosition()).getAccount());
                    textViewPassword.setText(accountList.get(getLayoutPosition()).getAccount());
                    textViewOther.setText(accountList.get(getLayoutPosition()).getOther());

                    final View accountTitleView = factory.inflate(R.layout.dialog_account_title, null);
                    final TextView title = (TextView) accountTitleView.findViewById(R.id.dialog_title);
                    title.setText("详细信息");

                    alert = new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setCustomTitle(accountTitleView)
                            .setView(accountShowView)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    alert.show();

                }
            });


        }
    }


    private void modifyAccount(final int position) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View accountEditView = factory.inflate(R.layout.dialog_account_insert, null);
        final EditText editTextName = (EditText) accountEditView.findViewById(R.id.dialog_insert_ed_account_name);
        final EditText editTextAccount = (EditText)accountEditView.findViewById(R.id.dialog_insert_ed_account_account);
        final EditText editTextPassword = (EditText)accountEditView.findViewById(R.id.dialog_insert_ed_account_password);
        final EditText editTextOther = (EditText)accountEditView.findViewById(R.id.dialog_insert_ed_account_other);

        final Account account = accountList.get(position);

        editTextName.setText(account.getName());
        editTextAccount.setText(account.getAccount());
        editTextPassword.setText(account.getPassword());
        editTextOther.setText(account.getOther());

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("修改项目")
                .setView(accountEditView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String accountName =  editTextName.getText().toString();
                        String accountAccount =  editTextAccount.getText().toString();
                        String accountPassword =  editTextPassword.getText().toString();
                        String accountOther =  editTextOther.getText().toString();

                        ContentValues values = new ContentValues();
                        values.put("NAME", accountName);
                        values.put("ACCOUNT", accountAccount);
                        values.put("PASSWORD", accountPassword);
                        values.put("OTHER", accountOther);

                        db.update("ACCOUNTSAFEBOX",values,"id=?",new String[]{accountList.get(position).getId()});

                        //修改list内容
                        accountList.get(position).setName(accountName);
                        accountList.get(position).setAccount(accountAccount);
                        accountList.get(position).setPassword(accountPassword);
                        accountList.get(position).setOther(accountOther);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void deleteAccount(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context)
                .setTitle("删除提醒")
                .setMessage("您确定要删除" + accountList.get(position).getName()+"吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete("BANKSAFEBOX","id=?",new String[]{accountList.get(position).getId()});
                        accountList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
