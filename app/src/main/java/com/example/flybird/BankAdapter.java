package com.example.flybird;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flybird.Models.Bank;
import com.example.flybird.Tools.MyDbOpenHelper;

import java.util.List;

public class BankAdapter extends SlideRecyclerView.Adapter<BankAdapter.ViewHolder>{

    private List<Bank> bankList;
    private Context context;
    private AlertDialog alert;

    private SQLiteDatabase db;
    private MyDbOpenHelper myDbOpenHelper;


    public BankAdapter(Context context, List<Bank> bankList){
        this.context = context;
        this.bankList = bankList;

        myDbOpenHelper = new MyDbOpenHelper(context, MyDbOpenHelper.DB_NAME, null, MyDbOpenHelper.DB_VERSION);
        db = myDbOpenHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public BankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BankAdapter.ViewHolder holder, final int position) {
        Bank bank = bankList.get(position);
//        holder.id.setText(bank.getId());
        holder.full_name.setText(bank.getFullName());
        holder.simple_name.setText(bank.getSimpleName());
        holder.owner.setText(bank.getOwner());


        holder.bank_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyBank(position);
            }
        });

        holder.bank_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBank(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView full_name;
        TextView simple_name;
        TextView owner;
        ImageButton bank_edit;
        ImageButton bank_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            full_name = itemView.findViewById(R.id.bank_full_name);
            simple_name = itemView.findViewById(R.id.bank_simple_name);
            owner = itemView.findViewById(R.id.owner);
            bank_edit = itemView.findViewById(R.id.bank_edit);
            bank_delete = itemView.findViewById(R.id.bank_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater factory = LayoutInflater.from(context);
                    final View accountShowView = factory.inflate(R.layout.dialog_bank_show, null);
                    final TextView textViewFullName = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_bank_full_name);
                    final TextView textViewSimpleName = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_bank_simple_name);
                    final TextView textViewOwner = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_bank_owner);
                    final TextView textViewPassword = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_bank_password);
                    final TextView textViewOther = (TextView) accountShowView.findViewById(R.id.dialog_show_tv_bank_other);

                    textViewFullName.setText(bankList.get(getLayoutPosition()).getFullName());
                    textViewSimpleName.setText(bankList.get(getLayoutPosition()).getSimpleName());
                    textViewOwner.setText(bankList.get(getLayoutPosition()).getOwner());
                    textViewPassword.setText(bankList.get(getLayoutPosition()).getPassword());
                    textViewOther.setText(bankList.get(getLayoutPosition()).getOther());

                    final View accountTitleView = factory.inflate(R.layout.dialog_account_title, null);
                    final TextView title = (TextView) accountTitleView.findViewById(R.id.dialog_title);
                    title.setText("详细信息");

                    alert = new AlertDialog.Builder(context)
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

    private void modifyBank(final int position) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View bankEditView = factory.inflate(R.layout.dialog_bank_insert, null);
        final EditText editTextFullName = (EditText) bankEditView.findViewById(R.id.dialog_insert_ed_bank_full_name);
        final EditText editTextSimpleName = (EditText) bankEditView.findViewById(R.id.dialog_insert_ed_bank_simple_name);
        final EditText editTextPassword = (EditText)bankEditView.findViewById(R.id.dialog_insert_ed_bank_password);
        final EditText editTextOwner = (EditText)bankEditView.findViewById(R.id.dialog_insert_ed_bank_owner);
        final EditText editTextOther = (EditText)bankEditView.findViewById(R.id.dialog_insert_ed_bank_other);

        Bank bank = bankList.get(position);

        editTextFullName.setText(bank.getFullName());
        editTextSimpleName.setText(bank.getSimpleName());
        editTextPassword.setText(bank.getPassword());
        editTextOwner.setText(bank.getOwner());
        editTextOther.setText(bank.getOther());

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle("修改项目")
            .setView(bankEditView)
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String bankFullName =  editTextFullName.getText().toString();
                    String bankSimpleName =  editTextSimpleName.getText().toString();
                    String bankPassword =  editTextPassword.getText().toString();
                    String bankOwner =  editTextOwner.getText().toString();
                    String bankOther =  editTextOther.getText().toString();

//                    if(TextUtils.isEmpty(bankName) |
//                            TextUtils.isEmpty(bankPassword) |
//                            TextUtils.isEmpty(bankOwner)){
//                        return;
//                    }

                    ContentValues values = new ContentValues();
                    values.put("FULLNAME", bankFullName);
                    values.put("SIMPLENAME", bankSimpleName);
                    values.put("PASSWORD", bankPassword);
                    values.put("OWNER", bankOwner);
                    values.put("OTHER", bankOther);

                    //修改更新数据库
                    db.update("BANKSAFEBOX",values,"id=?",new String[]{bankList.get(position).getId()});

                    //修改list内容
                    bankList.get(position).setFullName(bankFullName);
                    bankList.get(position).setSimpleName(bankSimpleName);
                    bankList.get(position).setPassword(bankPassword);
                    bankList.get(position).setOwner(bankOwner);
                    bankList.get(position).setOther(bankOther);
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

    public void deleteBank(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context)
            .setTitle("删除提醒")
            .setMessage("您确定要删除" + bankList.get(position).getFullName()+"吗")
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //删除
                    db.delete("BANKSAFEBOX","id=?",new String[]{bankList.get(position).getId()});
                    bankList.remove(position);
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
