package schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.lbstest.MainActivity;
import com.example.lbstest.R;
import schedule.base.BaseActivity;
import schedule.db.SQLite;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Schdule extends BaseActivity {
    RecyclerView recyclerView;
    CommonAdapter<Note> mAdapter;
    Context context;
    SQLite sqLite;
    Cursor cursor;
    public static final int REQUES_CODE = 1;
    private List<Note> mDatas = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        context = this;
        sqLite = new SQLite(context);
        recyclerView = findViewById(R.id.rv_my_list);
        Button toMap = (Button) findViewById(R.id.to_map);
        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Schdule.this, MainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_tj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("_id", "null");
                intent.putExtra("Modified_date", "null");
                startActivityForResult(intent, REQUES_CODE);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mAdapter = new CommonAdapter<Note>(context, R.layout.item_note, mDatas) {
            @Override
            protected void convert(final ViewHolder holder, Note dataBean, int position) {

                holder.setText(R.id.tv_nr, "计划内容：" + dataBean.getContext());
                String yue = dataBean.getModified_date();
                String date = yue.substring(0, 4) + "/" + yue.substring(4, 6)+"/"+yue.substring(6,8);
                holder.setText(R.id.tv_sj, "时间：" + date);

            }

        };
        recyclerView.setAdapter(mAdapter);
        getData();
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(Schdule.this,
                        NoteActivity.class);
                intent.putExtra("_id", mDatas.get(position).get_id());
                intent.putExtra("Context", mDatas.get(position).getContext());
                intent.putExtra("Modified_date", mDatas.get(position)
                        .getModified_date());
                startActivityForResult(intent, REQUES_CODE);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, final int position) {
               new AlertDialog.Builder(context).setTitle("删除当前计划？")
                        //设置正面按钮
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sqLite.delete(mDatas.get(position).get_id());
                                getData();
                                dialog.dismiss();
                            }
                        })
                        //设置反面按钮
                        .setNegativeButton("不是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });
    }

    private void getData() {
        // TODO Auto-generated method stub
        cursor = sqLite.queryAll();
        mDatas.clear();
        while (cursor.moveToNext()) {
            Note dataNote = new Note();
            dataNote.setContext(cursor.getString(cursor
                    .getColumnIndex("context")));
            dataNote.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            dataNote.setCreate_date(cursor.getString(cursor
                    .getColumnIndex("create_date")));
            dataNote.setModified_date(cursor.getString(cursor
                    .getColumnIndex("modified_date")));

            mDatas.add(dataNote);
        }
        if (mDatas.size() > 0) {
            findViewById(R.id.rl_no_data).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.rl_no_data).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUES_CODE) {
            getData();

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
