package schedule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLite {
    Context context;
    MySQLiteHelper sqLiteHelper;

    public SQLite(Context context) {
        super();
        this.context = context;
        sqLiteHelper = new MySQLiteHelper(context);

    }

    /**
     * ???
     *
     * @param create_date
     * @param modified_date
     * @param context
     */
    public void insert(String create_date, String modified_date, String context) {

        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("create_date", create_date);
        values.put("modified_date", modified_date);
        values.put("context", context);

        db.insert(MySQLiteHelper.TABLE, null, values);
        db.close();

    }

    /**
     * ??????
     *
     * @return
     */
    public Cursor queryAll() {
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE, null, null, null, null,
                null, "modified_date desc");
        Log.d(">>>>", "查詢數據");
        return cursor;

    }

    /**
     * ????
     * <p>
     * //     * @param create_date
     *
     * @param modified_date
     * @param context
     * @param id
     */
    public void update(String modified_date,
                       String context, String id) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("modified_date", modified_date);
        values.put("context", context);
        db.update(MySQLiteHelper.TABLE, values, "_id = ?", new String[]{id});

        db.close();
    }

    public void delete(String id) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        db.delete(MySQLiteHelper.TABLE, "_id=?", new String[]{id});
    }
}
