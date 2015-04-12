package com.znw.parentsforum.defineview;


import java.util.Date;

import com.znw.parentsforum.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener {

	private int firstVisibleIndex;//ListView�� ��һ��������Item���±�    ���±�Ϊ0��ʱ�� �������Ļ�  ����ˢ�µĿؼ���ʾ
	private OnRefreshListener refreshListener;//ˢ�¼�����
	private boolean isRefreshListenerOn;//�Ƿ�ʹ��ˢ�¼�����
	private View headView;
	private ImageView arrow;
	private ProgressBar progress;
	private TextView title;
	private TextView last_update;
	private int headContentHeight;
	private int headContentWidth;
	private Animation reverseAnimation;
	private Animation animation;
	
	private static final int PULL_TO_REFRESH = 0;//����ˢ��
	private static final int RELEASE_TO_REFRESH = 1;//�ɿ�ˢ��
	private static final int REFRESHING = 2;//����ˢ��
	private static final int DONE = 3;//ˢ�����
	private int state;//������¼��ǰ����ˢ��״̬
	private boolean isRecord;//��ʼ�������Ƿ񱻼�¼
	private float startY;
	private float tempY;
	private boolean isBack; //�Ƿ��� �ɿ�ˢ��  �ص��� ����ˢ��
	
	private static final int RATIO = 3;// ʵ����������� headview�����ϱ߾� ����֮��
	
	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	//��ʼ������
	private void init(Context context) {
		
			headView = View.inflate(context, R.layout.header, null);
			
			arrow = (ImageView) headView.findViewById(R.id.arrow);
			progress = (ProgressBar) headView.findViewById(R.id.progress);
			title = (TextView) headView.findViewById(R.id.title);
			last_update = (TextView) headView.findViewById(R.id.last_update);
			
			arrow.setMinimumWidth(70);
			arrow.setMinimumHeight(50);
			
			//�õ�headView �ؼ���� ������ˢ�¿ؼ���
			measureView(headView);
			
			headContentHeight = headView.getMeasuredHeight();
			headContentWidth = headView.getMeasuredWidth();
			
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			
			headView.invalidate();//�ػ�
			
			addHeaderView(headView);//ΪlistView ���һ��������ͼ
//			addFooterView(headView);
			
			setOnScrollListener(this);
			
			animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setDuration(250);
			animation.setFillAfter(true);
			animation.setInterpolator(new LinearInterpolator());//ָ�������������ٶȵķ�ʽ
			/**
			 * 
			Interpolator �����˶����ı仯�ٶȣ�����ʵ�����١������١������١��޹������ٵȣ�
			
			AccelerateDecelerateInterpolator���ӳټ��٣��ڶ���ִ�е��м��ʱ���ִ�и���Ч��
			AccelerateInterpolator, ��ʹ������(float)�Ĳ��������ٶȡ�
			LinearInterpolator��ƽ�Ȳ����
			DecelerateInterpolator�����м����,��ͷ��
			CycleInterpolator�������˶���Ч��Ҫ����float�͵Ĳ�����
			 */
			
			reverseAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			reverseAnimation.setDuration(200);
			reverseAnimation.setFillAfter(true);
			reverseAnimation.setInterpolator(new LinearInterpolator());//ָ�������������ٶȵķ�ʽ
			
			state = DONE;
			isRecord = false;
	}

	//�����ؼ��Ŀ��
	private void measureView(View child) {
		
		ViewGroup.LayoutParams lp = child.getLayoutParams();
		
		if(lp == null){
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); 		
		}
		
		int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
		int childMeasureHeight;
		
		if(lp.height > 0){
			childMeasureHeight = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
		} else {
			childMeasureHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		
		child.measure(childMeasureWidth, childMeasureHeight);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		firstVisibleIndex = firstVisibleItem;
	}
	
	
	public interface OnRefreshListener{
		abstract void onRefresh();
	}
	
	//�����ṩһ��ˢ�¼��� �ص�����
	public void setOnRefreshListener(OnRefreshListener listener){
		refreshListener = listener;
		
		isRefreshListenerOn = true;
	}

	//ˢ����ɺ� ִ�еĲ���   ����ˢ�µ�״̬ ����     ����ʱ��
	public void onRefreshComplete() {
		state = DONE;
		
		changeOfHeadViewState();
		
		last_update.setText("������ �� " + new Date().toLocaleString());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			if(firstVisibleIndex == 0 && isRecord){
				startY = event.getY();
				
				isRecord = true;
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			tempY = event.getY();
			
			if(firstVisibleIndex == 0 && isRecord){
				startY = tempY;
				
				isRecord = true;
			}
			
			if(state != REFRESHING){
				
				if( state == PULL_TO_REFRESH){//����ˢ��
					
					setSelection(0);
					
					if((tempY - startY) / RATIO > headContentHeight){//���������� ������headView�ؼ� ȫ����ʾ����  
						//�� ����ˢ�� --> �ɿ�ˢ��
						
						state = RELEASE_TO_REFRESH;
						
						changeOfHeadViewState();
					} else if((tempY - startY) < 0){// ��������  ����headView�ؼ� ������������
						// ������ˢ�� ����> ˢ�����
						
						state = DONE;
						
						changeOfHeadViewState();
					}
				}
				
				if( state == RELEASE_TO_REFRESH){//�ɿ�ˢ��
					
					setSelection(0);
					
					if((tempY - startY) /RATIO  < headContentHeight && (tempY - startY) > 0){//�������� headView�ؼ� ��ʾ��һ���� 
						// �ɿ�ˢ�� --> ����ˢ��
						
						state = PULL_TO_REFRESH;
						
						isBack = true;// �ɿ�ˢ�� --> ����ˢ��
						
						changeOfHeadViewState();
						
					} else if((tempY - startY) < 0){// ��������  ����headView�ؼ� ������������
						// �ɿ�ˢ�� --> ˢ�����
						
						state = DONE;
						
						changeOfHeadViewState();
					}
				}
				
				if( state == DONE){ //ˢ�����
					if((tempY - startY) > 0){//���������� 
						//�� ˢ����� --> ����ˢ��
						
						state = PULL_TO_REFRESH;
						
						changeOfHeadViewState();
					}
				}
				
				//headView ���� �ϱ߾����
				if(state == PULL_TO_REFRESH || state == RELEASE_TO_REFRESH){
					headView.setPadding(0, (int) ((tempY - startY) /RATIO  - headContentHeight), 0, 0);
				}
				
			}
			
			
			
			break;
		case MotionEvent.ACTION_UP:
			
			if(state != REFRESHING){
				
				if(state == PULL_TO_REFRESH){//����ˢ��
					
					state = DONE;
					
					changeOfHeadViewState();
				}
				
				if(state == RELEASE_TO_REFRESH){//�ɿ�ˢ��
					
					state = REFRESHING;
					
					changeOfHeadViewState();
					
					onRefresh();
				}
			}
			
			
			break;
		}
		
		return super.onTouchEvent(event);
	}

	//ˢ������s
	private void onRefresh() {
		if(isRefreshListenerOn){
			refreshListener.onRefresh();
		}
		
	}

	//headView ״̬�ı�
	private void changeOfHeadViewState() {
		// TODO Auto-generated method stub
		
		switch (state) {
		case PULL_TO_REFRESH:
			
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);

			arrow.clearAnimation();
			title.setText("����ˢ��");
			
			if(isBack){//�� �ɿ�ˢ��  �ص��� ����ˢ��
				arrow.startAnimation(animation);
				
				isBack = false;
			}
			
			
			break;
			
		case RELEASE_TO_REFRESH:
			
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);
			
			arrow.clearAnimation();
			title.setText("�ɿ�ˢ��");
			
			arrow.startAnimation(reverseAnimation);
			break;

		case REFRESHING:
			
			arrow.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);
			
			arrow.clearAnimation();
			title.setText("����ˢ����...");
			
			headView.setPadding(0, 0, 0, 0);
			
			break;

		case DONE:
			
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);

			arrow.clearAnimation();
			title.setText("����ˢ��");
			
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			
			break;

		}
		
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		last_update.setText("������ �� " + new Date().toLocaleString());
		
		super.setAdapter(adapter);
	}

}
