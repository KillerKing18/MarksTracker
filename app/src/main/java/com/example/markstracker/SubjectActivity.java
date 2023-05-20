package com.example.markstracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class SubjectActivity extends AppCompatActivity {

    TextView subjectName;
    TextView subjectMark;

    MarkSQLiteHelper mdbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        // Initialize SQLite helper class
        mdbh = new MarkSQLiteHelper(this);

        populateData();

        setButtonListeners();
    }

    private void populateData() {
        subjectName = findViewById(R.id.subjectNameTitle);
        subjectMark = findViewById(R.id.subjectMark);

        Intent intent = getIntent();

        subjectName.setText(intent.getStringExtra("subjectName"));
        subjectMark.setText(intent.getStringExtra("mark"));
    }

    private void setButtonListeners() {
        Button buttonChangeMark = (Button) findViewById(R.id.changeMarkButton);
        buttonChangeMark.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(SubjectActivity.this);

            final View customLayout = getLayoutInflater().inflate(R.layout.edit_mark_custom_layout, null);
            alert.setView(customLayout);

            alert.setTitle(R.string.newMarkText);

            alert.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    float mark = Float.parseFloat(((EditText)customLayout.findViewById(R.id.updatedMarkEditText)).getText().toString());

                    if (mark < 0.00 || mark > 10.00 || (Float.toString(mark).split("\\.").length == 2 && Float.toString(mark).split("\\.")[1].length() > 2)) {
                        Toast.makeText(SubjectActivity.this, R.string.wrongMark, Toast.LENGTH_LONG).show();
                    }
                    else {
                        db = mdbh.getWritableDatabase();
                        if (db != null)
                        {
                            mdbh.updateMarkBySubject(db, subjectName.getText().toString(), mark);
                            db.close();

                            // We have to update the mark TextView with the new value introduced by the user
                            subjectMark.setText(StringUtils.decimalFormat(mark));

                            Toast.makeText(SubjectActivity.this, R.string.markUpdatedToast, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            alert.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // If the user taps on "Cancel", we don't have to do anything
                }
            });
            alert.show();
        });

        Button buttonDeleteMark = (Button) findViewById(R.id.deleteMarkButton);
        buttonDeleteMark.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.deleteMarkText);
            alert.setMessage(R.string.deleteMarkQuestionText);
            alert.setPositiveButton(R.string.yes, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            db = mdbh.getWritableDatabase();
                            if (db != null)
                            {
                                mdbh.deleteMarkBySubject(db, subjectName.getText().toString());
                                db.close();

                                Intent intent = new Intent();
                                intent.setClass(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(SubjectActivity.this, R.string.markDeletedToast, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            alert.setNegativeButton(R.string.no, new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // If the user taps on "No", we don't have to do anything
                        }
                    });
            alert.show();
        });
    }
}