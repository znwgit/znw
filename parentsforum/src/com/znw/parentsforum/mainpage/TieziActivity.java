package com.znw.parentsforum.mainpage;

import java.util.ArrayList;

import com.znw.parentsforum.R;
import com.znw.parentsforum.defineview.MyListView;
import com.znw.parentsforum.defineview.MyListView.OnRefreshListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TieziActivity extends Activity {

    private MyListView listView;
	private MyAdapter adapter;
	private ArrayList<String> data;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiezi_list);
        
        data = new ArrayList<String>();
        data.add("帖子1");
        data.add("帖子2");
        data.add("帖子1333333");
        
        
        listView = (MyListView) findViewById(R.id.listView);
        
        adapter = new MyAdapter();
        
        listView.setAdapter(adapter);
        
        //添加刷新监听
        listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>(){

					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						SystemClock.sleep(2000);
						
						data.add("刷新后的帖子");
						
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						
						adapter.notifyDataSetChanged();
						
						listView.onRefreshComplete();
						
					}
				}.execute(null,null,null);
			}
		});
        
        
    }
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = new TextView(getApplicationContext());
			textView.setText(data.get(position));
			
			return textView;
		}
		
	}
}
