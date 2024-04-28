package com.example.assignment3;


        import android.annotation.SuppressLint;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.widget.Toast;

        import androidx.annotation.Nullable;

        import java.util.ArrayList;

public class ItemsDB {
    private final String DATABASE_TABLE = "Items_Table";
    private final String DELETED_ITEMS_TABLE = "Deleted_Items_Table";
    private final String USERS_TABLE = "Users_Table";

    private final String KEY_ID = "_id";
    private final String KEY_USERNAME = "_username";
    private final String KEY_PASSWORD = "_password";
    private final String KEY_URL = "_url";

    private final String USER_KEY_ID = "_user_id";
    private final String USER_KEY_USERNAME = "_user_username";
    private final String USER_KEY_PASSWORD = "_user_password";

    Context context;

    MyDatabaseHelper helper;
    SQLiteDatabase sqLiteDatabase;

    public ItemsDB(Context c)
    {
        context = c;
    }

    public void open() {
        String DATABASE_NAME = "ItemsDB";
        int DATABASE_VERSION = 2;
        helper = new MyDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = helper.getWritableDatabase();
    }

    public void registerUser(String username, String password) {
        ContentValues cv = new ContentValues();
        cv.put(USER_KEY_USERNAME, username);
        cv.put(USER_KEY_PASSWORD, password);

        long temp = sqLiteDatabase.insert(USERS_TABLE, null, cv);
        if (temp == -1) {
            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    public int loginUser(String username, String password) {
        Cursor cursor = sqLiteDatabase.query(USERS_TABLE, null, USER_KEY_USERNAME + "=? AND " + USER_KEY_PASSWORD + "=?", new String[]{username, password}, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(USER_KEY_ID));
        }
        cursor.close();
        return userId;
    }

    public DataItem getItemById(int userId, int id) {
        Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE, null, KEY_ID + "=? AND " + USER_KEY_ID + "=?", new String[]{String.valueOf(id), String.valueOf(userId)}, null, null, null);
        DataItem item = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(KEY_URL));

            item = new DataItem(id, username, password, url);
        }
        cursor.close();
        return item;
    }

    @SuppressLint("Range")
    public int getUserIdByUsername(String username) {
        String[] columns = {USER_KEY_ID};
        String selection = USER_KEY_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = sqLiteDatabase.query(USERS_TABLE, columns, selection, selectionArgs, null, null, null);

        int userId = -1; // Default value if no user found

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(USER_KEY_ID));
        }

        cursor.close();
        return userId;
    }

    public void insert(int userId, String name, String password, String url) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME, name);
        cv.put(KEY_PASSWORD, password);
        cv.put(KEY_URL, url);
        cv.put(USER_KEY_ID, userId);

        long temp = sqLiteDatabase.insert(DATABASE_TABLE, null, cv);
        if (temp == -1) {
            Toast.makeText(context, "Contact not added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteItem(int userId, int id) {
        Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE, null, KEY_ID + "=? AND " + USER_KEY_ID + "=?", new String[]{String.valueOf(id), String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(KEY_URL));

            // Insert the deleted item into the new table
            ContentValues cv = new ContentValues();
            cv.put(KEY_USERNAME, username);
            cv.put(KEY_PASSWORD, password);
            cv.put(KEY_URL, url);
            cv.put(USER_KEY_ID, userId); // Adding user key

            sqLiteDatabase.insert(DELETED_ITEMS_TABLE, null, cv);
        }
        cursor.close();

        int rows = sqLiteDatabase.delete(DATABASE_TABLE, KEY_ID + "=? AND " + USER_KEY_ID + "=?", new String[]{String.valueOf(id), String.valueOf(userId)});
        if (rows > 0) {
            Toast.makeText(context, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Contact not deleted", Toast.LENGTH_SHORT).show();
        }
    }


    public ArrayList<DataItem> getDeletedItems(int userId) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + DELETED_ITEMS_TABLE + " WHERE " + USER_KEY_ID + "=?", new String[]{String.valueOf(userId)});
        ArrayList<DataItem> items = new ArrayList<>();
        int id_index = c.getColumnIndex(KEY_ID);
        int id_username = c.getColumnIndex(KEY_USERNAME);
        int id_password = c.getColumnIndex(KEY_PASSWORD);
        int id_url = c.getColumnIndex(KEY_URL);

        if (c.moveToFirst()) {
            do {
                DataItem item = new DataItem();
                item.setId(c.getInt(id_index));
                item.setUsername(c.getString(id_username));
                item.setUrl(c.getString(id_url));
                item.setPassword(c.getString(id_password));

                items.add(item);
            } while (c.moveToNext());
        }

        c.close();
        return items;
    }

    public ArrayList<DataItem> readAllItems(int userId) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE " + USER_KEY_ID + "=?", new String[]{String.valueOf(userId)});
        ArrayList<DataItem> items = new ArrayList<>();
        int id_index = c.getColumnIndex(KEY_ID);
        int id_username = c.getColumnIndex(KEY_USERNAME);
        int id_password = c.getColumnIndex(KEY_PASSWORD);
        int id_url = c.getColumnIndex(KEY_URL);

        if (c.moveToFirst()) {
            do {
                DataItem item = new DataItem();
                item.setId(c.getInt(id_index));
                item.setUsername(c.getString(id_username));
                item.setUrl(c.getString(id_url));
                item.setPassword(c.getString(id_password));

                items.add(item);
            } while (c.moveToNext());
        }

        c.close();
        return items;
    }
    public void close() {
        sqLiteDatabase.close();
        helper.close();
    }

    public void updateItem(int userId, int id, String newUsername, String newPassword, String newUrl) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME, newUsername);
        cv.put(KEY_PASSWORD, newPassword);
        cv.put(KEY_URL, newUrl);

        int rows = sqLiteDatabase.update(DATABASE_TABLE, cv, KEY_ID + "=? AND " + USER_KEY_ID + "=?", new String[]{String.valueOf(id), String.valueOf(userId)});
        if (rows > 0) {
            Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update contact", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyDatabaseHelper extends SQLiteOpenHelper
    {
        public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + USERS_TABLE + "(" + USER_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_KEY_USERNAME + " TEXT NOT NULL, " + USER_KEY_PASSWORD + " TEXT NOT NULL);");
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USERNAME + " TEXT NOT NULL, " + KEY_PASSWORD + " TEXT NOT NULL, " + KEY_URL + " TEXT NOT NULL, " + USER_KEY_ID + " INTEGER NOT NULL, FOREIGN KEY (" + USER_KEY_ID + ") REFERENCES " + USERS_TABLE + "(" + USER_KEY_ID + "));");
            db.execSQL("CREATE TABLE " + DELETED_ITEMS_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USERNAME + " TEXT NOT NULL, " + KEY_PASSWORD + " TEXT NOT NULL, " + KEY_URL + " TEXT NOT NULL, " + USER_KEY_ID + " INTEGER NOT NULL, FOREIGN KEY (" + USER_KEY_ID + ") REFERENCES " + USERS_TABLE + "(" + USER_KEY_ID + "));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DELETED_ITEMS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
            onCreate(db);
        }
    }
}
