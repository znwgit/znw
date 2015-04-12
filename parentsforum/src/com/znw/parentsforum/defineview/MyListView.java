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

	private int firstVisibleIndex;//ListView中 第一个看见的Item的下标    当下标为0的时候 再下拉的话  下拉刷新的控件显示
	private OnRefreshListener refreshListener;//刷新监听器
	private boolean isRefreshListenerOn;//是否使用刷新监听器
	private View headView;
	private ImageView arrow;
	private ProgressBar progress;
	private TextView title;
	private TextView last_update;
	private int headContentHeight;
	private int headContentWidth;
	private Animation reverseAnimation;
	private Animation animation;
	
	private static final int PULL_TO_REFRESH = 0;//下拉刷新
	private static final int RELEASE_TO_REFRESH = 1;//松开刷新
	private static final int REFRESHING = 2;//正在刷新
	private static final int DONE = 3;//刷新完成
	private int state;//用来记录当前下拉刷新状态
	private boolean isRecord;//起始点坐标是否被记录
	private float startY;
	private float tempY;
	private boolean isBack; //是否由 松开刷新  回到的 下拉刷新
	
	private static final int RATIO = 3;// 实际拉动距离和 headview距离上边距 距离之比
	
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

	//初始化操作
	private void init(Context context) {
		
			headView = View.inflate(context, R.layout.header, null);
			
			arrow = (ImageView) headView.findViewById(R.id.arrow);
			progress = (ProgressBar) headView.findViewById(R.id.progress);
			title = (TextView) headView.findViewById(R.id.title);
			last_update = (TextView) headView.findViewById(R.id.last_update);
			
			arrow.setMinimumWidth(70);
			arrow.setMinimumHeight(50);
			
			//得到headView 控件宽高 （下拉刷新控件）
			measureView(headView);
			
			headContentHeight = headView.getMeasuredHeight();
			headContentWidth = headView.getMeasuredWidth();
			
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			
			headView.invalidate();//重绘
			
			addHeaderView(headView);//为listView 添加一个顶部试图
//			addFooterView(headView);
			
			setOnScrollListener(this);
			
			animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setDuration(250);
			animation.setFillAfter(true);
			animation.setInterpolator(new LinearInterpolator());//指定动画的运行速度的方式
			/**
			 * 
			Interpolator 定义了动画的变化速度，可以实现匀速、正加速、负加速、无规则变加速等；
			
			AccelerateDecelerateInterpolator，延迟减速，在动作执行到中间的时候才执行该特效。
			AccelerateInterpolator, 会使慢慢以(float)的参数降低速度。
			LinearInterpolator，平稳不变的
			DecelerateInterpolator，在中间加速,两头慢
			CycleInterpolator，曲线运动特效，要传递float型的参数。
			 */
			
			reverseAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			reverseAnimation.setDuration(200);
			reverseAnimation.setFillAfter(true);
			reverseAnimation.setInterpolator(new LinearInterpolator());//指定动画的运行速度的方式
			
			state = DONE;
			isRecord = false;
	}

	//测量控件的宽高
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
	
	//对外提供一个刷新监听 回调方法
	public void setOnRefreshListener(OnRefreshListener listener){
		refreshListener = listener;
		
		isRefreshListenerOn = true;
	}

	//刷新完成后 执行的操作   下拉刷新的状态 更改     更新时间
	public void onRefreshComplete() {
		state = DONE;
		
		changeOfHeadViewState();
		
		last_update.setText("更新于 ： " + new Date().toLocaleString());
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
				
				if( state == PULL_TO_REFRESH){//下拉刷新
					
					setSelection(0);
					
					if((tempY - startY) / RATIO > headContentHeight){//向下拉动了 将整个headView控件 全都显示出来  
						//由 下拉刷新 --> 松开刷新
						
						state = RELEASE_TO_REFRESH;
						
						changeOfHeadViewState();
					} else if((tempY - startY) < 0){// 向上推了  整个headView控件 都看不见到了
						// 由下拉刷新 ——> 刷新完成
						
						state = DONE;
						
						changeOfHeadViewState();
					}
				}
				
				if( state == RELEASE_TO_REFRESH){//松开刷新
					
					setSelection(0);
					
					if((tempY - startY) /RATIO  < headContentHeight && (tempY - startY) > 0){//向上推了 headView控件 显示出一部分 
						// 松开刷新 --> 下拉刷新
						
						state = PULL_TO_REFRESH;
						
						isBack = true;// 松开刷新 --> 下拉刷新
						
						changeOfHeadViewState();
						
					} else if((tempY - startY) < 0){// 向上推了  整个headView控件 都看不见到了
						// 松开刷新 --> 刷新完成
						
						state = DONE;
						
						changeOfHeadViewState();
					}
				}
				
				if( state == DONE){ //刷新完成
					if((tempY - startY) > 0){//向下拉动了 
						//由 刷新完成 --> 下拉刷新
						
						state = PULL_TO_REFRESH;
						
						changeOfHeadViewState();
					}
				}
				
				//headView 距离 上边距距离
				if(state == PULL_TO_REFRESH || state == RELEASE_TO_REFRESH){
					headView.setPadding(0, (int) ((tempY - startY) /RATIO  - headContentHeight), 0, 0);
				}
				
			}
			
			
			
			break;
		case MotionEvent.ACTION_UP:
			
			if(state != REFRESHING){
				
				if(state == PULL_TO_REFRESH){//下拉刷新
					
					state = DONE;
					
					changeOfHeadViewState();
				}
				
				if(state == RELEASE_TO_REFRESH){//松开刷新
					
					state = REFRESHING;
					
					changeOfHeadViewState();
					
					onRefresh();
				}
			}
			
			
			break;
		}
		
		return super.onTouchEvent(event);
	}

	//刷新数据s
	private void onRefresh() {
		if(isRefreshListenerOn){
			refreshListener.onRefresh();
		}
		
	}

	//headView 状态改变
	private void changeOfHeadViewState() {
		// TODO Auto-generated method stub
		
		switch (state) {
		case PULL_TO_REFRESH:
			
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);

			arrow.clearAnimation();
			title.setText("下拉刷新");
			
			if(isBack){//由 松开刷新  回到的 下拉刷新
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
			title.setText("松开刷新");
			
			arrow.startAnimation(reverseAnimation);
			break;

		case REFRESHING:
			
			arrow.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);
			
			arrow.clearAnimation();
			title.setText("正在刷新中...");
			
			headView.setPadding(0, 0, 0, 0);
			
			break;

		case DONE:
			
			arrow.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			last_update.setVisibility(View.VISIBLE);

			arrow.clearAnimation();
			title.setText("下拉刷新");
			
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			
			break;

		}
		
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		last_update.setText("更新于 ： " + new Date().toLocaleString());
		
		super.setAdapter(adapter);
	}

}
