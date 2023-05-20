package com.example.markstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MarkSQLiteHelper mdbh;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SQLite helper class
        mdbh = new MarkSQLiteHelper(this);

        setButtonListeners();

        enableDeleteButton(!checkTableIsEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The number of entries in the mark table could have changed when we go back to the MainActivity
        // so we have to check if it has to be enabled/disabled
        enableDeleteButton(!checkTableIsEmpty());
    }

    private void setButtonListeners() {
        Button goToAddMarkButton = (Button) findViewById(R.id.goToAddMarkButton);
        goToAddMarkButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), AddMarkActivity.class);
            startActivity(intent);
        });

        Button checkMarksButton = (Button)findViewById(R.id.checkMarksButton);
        checkMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), List.class);
                startActivity(intent);
            }
        });

        Button deleteAllMarksButton = (Button)findViewById(R.id.deleteAllMarksButton);
        deleteAllMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle(R.string.deleteAllMarksText);
                alert.setMessage(R.string.deleteAllMarksQuestionText);
                alert.setPositiveButton(R.string.yes, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                db = mdbh.getWritableDatabase();
                                if (db != null)
                                {
                                    mdbh.deleteAllMarks(db);
                                    db.close();

                                    // Once all entries are deleted, we have to disable the button
                                    enableDeleteButton(false);

                                    Toast.makeText(MainActivity.this, R.string.allMarksDeletedToast, Toast.LENGTH_LONG).show();
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
            }
        });
    }

    private boolean checkTableIsEmpty() {
        db = mdbh.getReadableDatabase();
        boolean empty = mdbh.checkTableIsEmpty(db);
        db.close();
        return empty;
    }

    private void enableDeleteButton(boolean enabled) {
        Button deleteAllMarksButton = (Button)findViewById(R.id.deleteAllMarksButton);
        deleteAllMarksButton.setEnabled(enabled);
        // We change the button opacity depending on if it's enabled or disabled
        deleteAllMarksButton.setAlpha(enabled ? 1f : .5f);
    }
}