package com.example.expensemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.expensemanager.Model.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class smsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String PREFS_NAME = "smsPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current user UID (Email) as used in Dashboard
        SharedPreferences userPrefs = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String uid = userPrefs.getString("email", "default_user");

        // Patterns for Bank SMS
        Pattern transactionPattern = Pattern.compile(
                "(?:Credited to your Ac \\w+ on (\\d{2}-\\d{2}-\\d{2}) by UPI ref No\\.(\\d+)|" +
                "A/C \\w+ debited by (\\d+\\.\\d+|\\d+) on date (\\d{2}[a-zA-Z]{3}\\d{2}) trf to (.*) Refno (\\d+)|" +
                "your A/c \\w+-credited by Rs\\.(\\d+(\\.\\d{1,2})?) on (\\d{2}[a-zA-Z]{3}\\d{2}) transfer from (.*) Ref No (\\d+))"
        );

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String messageBody = sms.getMessageBody();
            String messageId = sms.getTimestampMillis() + "_" + sms.getOriginatingAddress();

            // Prevent duplicate processing
            if (sharedPreferences.contains(messageId)) continue;

            Matcher matcher = transactionPattern.matcher(messageBody);

            if (matcher.find()) {
                String type = "";
                int amount = 0;
                String note = "";
                String dateStr = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

                if (matcher.group(3) != null) { // Debit Case
                    type = "Expense";
                    amount = (int) Double.parseDouble(matcher.group(3));
                    note = "Auto: Sent to " + matcher.group(5);
                } else if (matcher.group(7) != null) { // Credit Case
                    type = "Income";
                    amount = (int) Double.parseDouble(matcher.group(7));
                    note = "Auto: Recv from " + matcher.group(9);
                }

                if (!type.isEmpty()) {
                    // Create Data object for SQLite
                    // Note: 'Salary', 'Shopping', etc are used as 'type' (category) in your Data model
                    String category = (type.equals("Income")) ? "Salary" : "Others";
                    
                    Data data = new Data(amount, category, note, UUID.randomUUID().toString(), dateStr);
                    dbHelper.insertTransaction(data, uid, type);
                    
                    Log.d(TAG, "Successfully added " + type + " from SMS: ₹" + amount);
                    
                    // Mark as processed
                    editor.putBoolean(messageId, true);
                    editor.apply();
                }
            }
        }
    }
}
