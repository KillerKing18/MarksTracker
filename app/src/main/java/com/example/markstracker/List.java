package com.example.markstracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class List extends Activity {


    MarkSQLiteHelper mdbh;
    SQLiteDatabase db;
    ListView listView;
    CustomAdapter customAdapter = new CustomAdapter();
    java.util.List<String> subjectNames = new ArrayList<String>();
    java.util.List<String> marks = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        fillData();

        listView = (ListView) findViewById(android.R.id.list);

        listView.setEmptyView(findViewById(android.R.id.empty));

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SubjectActivity.class);
                intent.putExtra("subjectName", subjectNames.get(position));
                intent.putExtra("mark", marks.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // We have to update the data lists each time we go back to this activity, in case a mark has been added/deleted/updated
        clearData();
        fillData();
        customAdapter.notifyDataSetChanged();
    }

    private void fillData() {
        mdbh = new MarkSQLiteHelper(this);

        db = mdbh.getReadableDatabase();
        Cursor c = mdbh.getAllMarks(db);
        // We check if at least one register exists
        if (c.moveToFirst()) {
            do {
                @SuppressLint("Range") String subject = c.getString(c.getColumnIndex("subject"));
                @SuppressLint("Range") String mark = StringUtils.decimalFormat(c.getFloat(c.getColumnIndex("mark")));
                subjectNames.add(subject);
                marks.add(mark);
            } while(c.moveToNext());
        }
        db.close();
    }

    private void clearData() {
        subjectNames.clear();
        marks.clear();
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return marks.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.subject_row, null);

            TextView subjectName = view1.findViewById(R.id.subjectNameRow);
            TextView mark = view1.findViewById(R.id.markRow);

            subjectName.setText(subjectNames.get(i));
            mark.setText(marks.get(i));

            return view1;
        }
    }
}
