package schedule;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.lbstest.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import schedule.base.BaseActivity;
import schedule.db.SQLite;

public class NoteActivity extends BaseActivity implements View.OnClickListener {
    EditText noteEditText;
    String string, getModified_date;
    SQLite sqLite;
    String _id;
    String noetContext;
    TextView date1;
    SimpleDateFormat formatte;
    Date curDate;
    private TimePickerView pvTime;
    private String begin_time = null, end_time = null;
    private Calendar startDate, endDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteEditText = (EditText) findViewById(R.id.note_edit_view);
        noteEditText.setFocusable(true);
        formatte = new SimpleDateFormat("yyyyMMddHHmmE ");
        curDate = new Date(System.currentTimeMillis());// 获取当前时间
        begin_time = formatte.format(curDate);
        getModified_date = getIntent().getStringExtra("Modified_date");
        curDate = new Date(System.currentTimeMillis());// 获取当前时间
        date1 = (TextView) findViewById(R.id.date1);


        if (getModified_date.equals("null")) {
            date1.setText(begin_time.subSequence(0, 4) + "年" + begin_time.substring(4, 6)
                    + "月" + begin_time.substring(6, 8) + "日");

        } else {
            date1.setText(getModified_date.subSequence(0, 4) + "年"
                    + getModified_date.substring(4, 6) + "月"
                    + getModified_date.substring(6, 8) + "日");

        }

        _id = getIntent().getStringExtra("_id");
        noetContext = getIntent().getStringExtra("Context");

        findViewById(R.id.tijiao).setOnClickListener(this);
        findViewById(R.id.fanhui).setOnClickListener(this);
        date1.setOnClickListener(this);
        sqLite = new SQLite(this);
        noteEditText.setText(noetContext);
        noteEditText.requestFocus();

    }

    @Override
    public void onBackPressed() {

        setResult(Schdule.REQUES_CODE);
        string = noteEditText.getText().toString();

        String create_date = begin_time;

        if (noetContext != null) {

            if (string.trim().length() - noetContext.length() > 0) {

                sqLite.update(create_date, string, _id);
            }
        } else {
            if (string.trim().length() > 0) {
                if (_id.equals("null")) {
                    sqLite.insert(create_date, create_date, string);
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        String create_date;
        switch (v.getId()) {
            case R.id.tijiao:
                string = noteEditText.getText().toString();

                setResult(Schdule.REQUES_CODE);
                create_date = begin_time;
                if (noetContext != null) {
                    if (string.trim().length() - noetContext.length() > 0) {
                        sqLite.update(getModified_date, string, _id);
                    }
                } else {
                    if (string.trim().length() > 0) {
                        if (_id.equals("null")) {
                            sqLite.insert(create_date, create_date, string);
                        }
                    }
                }
                finish();
                break;
            case R.id.fanhui:
                setResult(Schdule.REQUES_CODE);
                string = noteEditText.getText().toString();

                create_date = begin_time;
                if (noetContext != null) {
                    if (string.trim().length() - noetContext.length() > 0) {
                        sqLite.update(getModified_date, string, _id);
                    }
                } else {
                    if (string.trim().length() > 0) {
                        if (_id.equals("null")) {
                            sqLite.insert(create_date, create_date, string);
                        }
                    }
                }
                finish();

                break;
            case R.id.date1:
                initTimePicker();
                pvTime.show(v);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void initTimePicker() {

        startDate = Calendar.getInstance();

        startDate.set(1990, 0, 1);
        endDate = Calendar.getInstance();
        endDate.set(2030, 11, 31);
        pvTime = new TimePickerBuilder(this, new MainTimeSelectListener())
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setContentTextSize(18)//滚轮文字大小
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setDate(Calendar.getInstance())
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(true)//是否显示为对话框样式
                .build();

    }

    class MainTimeSelectListener implements OnTimeSelectListener {
        @Override
        public void onTimeSelect(Date date, View v) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmE");
            switch (v.getId()) {
                case R.id.date1:
                    begin_time = df.format(date);
                    date1.setText(begin_time.subSequence(0, 4) + "年" + begin_time.substring(4, 6)
                            + "月" + begin_time.substring(6, 8) + "日");
                    // date1.setText(begin_time);
                    //     Toast.makeText(MainActivity.this, df.format(day), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
}
