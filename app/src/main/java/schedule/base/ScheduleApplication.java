package schedule.base;

import android.app.Application;
import android.content.Context;

import schedule.db.SQLite;

/**
 * Created by taozipc on 2018/5/23.
 */

public class ScheduleApplication extends Application {
    SQLite sqLite;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sqLite = new SQLite(context);

    }
}
