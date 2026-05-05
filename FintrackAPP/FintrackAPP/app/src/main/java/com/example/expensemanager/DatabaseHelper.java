package com.example.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.expensemanager.Model.Data;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseManager.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_TRANSACTIONS = "transactions";

    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TYPE = "type"; // Income or Expense
    private static final String KEY_NOTE = "note";
    private static final String KEY_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";

        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_UID + " TEXT,"
                + KEY_AMOUNT + " INTEGER,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_DATE + " TEXT" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID}, KEY_EMAIL + "=? AND " + KEY_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID}, KEY_EMAIL + "=?", new String[]{email}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, values, KEY_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    public void insertTransaction(Data data, String uid, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, data.getId());
        values.put(KEY_UID, uid);
        values.put(KEY_AMOUNT, data.getAmount());
        values.put(KEY_CATEGORY, data.getType());
        values.put(KEY_TYPE, type);
        values.put(KEY_NOTE, data.getNote());
        values.put(KEY_DATE, data.getDate());
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public List<Data> getAllTransactions(String uid) {
        List<Data> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, KEY_UID + "=?", new String[]{uid}, null, null, KEY_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE))
                );
                list.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int getTotalByType(String uid, String type) {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + KEY_UID + "=? AND " + KEY_TYPE + "=?", new String[]{uid, type});
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public void updateTransaction(Data data, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, data.getAmount());
        values.put(KEY_CATEGORY, data.getType());
        values.put(KEY_NOTE, data.getNote());
        values.put(KEY_DATE, data.getDate());
        values.put(KEY_TYPE, type);
        db.update(TABLE_TRANSACTIONS, values, KEY_ID + "=?", new String[]{data.getId()});
    }

    public void deleteTransaction(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + "=?", new String[]{id});
    }

    // Legacy support methods for compatibility with old code
    public List<Data> getAllIncome(String uid) { return getTransactionsByType(uid, "Income"); }
    public List<Data> getAllExpense(String uid) { return getTransactionsByType(uid, "Expense"); }
    public void updateIncome(Data data) { updateTransaction(data, "Income"); }
    public void updateExpense(Data data) { updateTransaction(data, "Expense"); }
    public void deleteIncome(String id) { deleteTransaction(id); }
    public void deleteExpense(String id) { deleteTransaction(id); }

    public List<Data> getTransactionsByType(String uid, String type) {
        List<Data> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null, KEY_UID + "=? AND " + KEY_TYPE + "=?", new String[]{uid, type}, null, null, KEY_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TYPE))
                );
                list.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}