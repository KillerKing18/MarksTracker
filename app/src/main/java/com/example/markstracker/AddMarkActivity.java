package com.example.markstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

public class AddMarkActivity extends AppCompatActivity {

    MarkSQLiteHelper mdbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark);

        // Initialize SQLite helper class
        mdbh = new MarkSQLiteHelper(this);

        EditText subject = ((EditText) findViewById(R.id.subjectNameEditText));
        subject.requestFocus();

        setButtonListeners();
    }
    
    private void setButtonListeners() {
        Button buttonAddMark = (Button) findViewById(R.id.addButton);
        buttonAddMark.setOnClickListener(view -> {
            String subjectText = ((EditText) findViewById(R.id.subjectNameEditText)).getText().toString();
            String mark = ((EditText) findViewById(R.id.markEditText)).getText().toString();

            if (validateMark(subjectText, mark)) {
                db = mdbh.getWritableDatabase();
                if (db != null)
                {
                    mdbh.insertMark(db, subjectText, Float.parseFloat(mark));
                    db.close();

                    Toast.makeText(this, R.string.markAddedToast, Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent(getApplicationContext(), SubjectActivity.class);
                    intent.putExtra("subjectName", subjectText);
                    intent.putExtra("mark", StringUtils.decimalFormat(Float.parseFloat(mark)));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateMark(String subject, String mark) {
        if (subject.isEmpty() || mark.isEmpty()) {
            Toast.makeText(this, R.string.markIsEmptyToast, Toast.LENGTH_LONG).show();
            return false;
        }
        db = mdbh.getReadableDatabase();
        if (mdbh.checkSubjectExists(db, subject)) {
            Toast.makeText(this, R.string.markAlreadyExistsToast, Toast.LENGTH_LONG).show();
            db.close();
            return false;
        }
        float markFloat = Float.parseFloat(mark);
        // We check if the mark introduced is between 0 and 10 and has a correct format
        if (markFloat < 0.00 || markFloat > 10.00 || (mark.split("\\.").length == 2 && mark.split("\\.")[1].length() > 2)) {
            Toast.makeText(this, R.string.wrongMark, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        clearForms();
    }
    
    private void clearForms() {
        EditText subject = ((EditText) findViewById(R.id.subjectNameEditText));
        EditText mark = ((EditText) findViewById(R.id.markEditText));

        subject.requestFocus();
        subject.setText("");
        mark.setText("");
    }
}