package com.znw.parentsforum.defineview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;

public class MyToggle extends View {

	private Bitmap switch_on_bkg;
	private Bitmap switch_off_bkg;
	private Bitmap slip_btn;
	private Rect rect_on;
	private Rect rect_off;
	private boolean isSwitchOn;//记录当前开关的状态
	private OnSwitchStateListener switchStateListener;//开关监听器
	private boolean isSwitchStateListenerOn;//是否使用了 开关监听器
	private float currentX;
	private boolean isSlipping;//当前是否可以滑动 
	private boolean oldSwitchState = true;//原来开关的状态

	public MyToggle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyToggle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyToggle(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 指定开关样式
	 * @param bkgSwitchOn	开启的背景图片
	 * @param bkgSwitchOff  关闭的背景图片
	 * @param btnSlip		滑动块图片
	 */
	public void setImageRes(int bkgSwitchOn, int bkgSwitchOff, int btnSlip) {
		
		switch_on_bkg = BitmapFactory.decodeResource(getResources(), bkgSwitchOn);
		switch_off_bkg = BitmapFactory.decodeResource(getResources(), bkgSwitchOff);
		slip_btn = BitmapFactory.decodeResource(getResources(), btnSlip);
		
		
		//使用Rect 记录 开关的位置  简化开发
		//开启
		rect_on = new Rect(switch_off_bkg.getWidth() - slip_btn.getWidth(), 0, switch_off_bkg.getWidth(), slip_btn.getHeight());
		//关闭
		rect_off = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		
		
	}

	//设置开关的状态
	public void setSwitchState(boolean state) {
		isSwitchOn =  state;
	}
	
	
	public interface OnSwitchStateListener{
		abstract void onSwitch(boolean state);
	}
	
	//对外提供一个开关状态改变的回调方法
	public void setOnSwitchStateListener(OnSwitchStateListener listener){
		switchStateListener = listener;
		
		isSwitchStateListenerOn = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://按下
			
			currentX = event.getX();
			
			isSlipping = true;
			
			break;
		case MotionEvent.ACTION_MOVE://移动
			
			currentX = event.getX();
			
			break;
		case MotionEvent.ACTION_UP://松开
			
			isSlipping = false;
			
			if(currentX > switch_off_bkg.getWidth()/2){
				isSwitchOn = true;
			} else {
				isSwitchOn = false;
			}
			
			//注册了开关监听器 同时开关状态发生变化时 调用
			if(isSwitchStateListenerOn && isSwitchOn != oldSwitchState){
				switchStateListener.onSwitch(isSwitchOn);
				
				//将原来开状态更新下
				oldSwitchState = isSwitchOn;
			}
			
			break;

		}
		
		invalidate();//重新绘制
		return true;
		
//		return super.onTouchEvent(event);
	}

	
	//测量控件的尺寸大小 (开关)
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(switch_off_bkg.getWidth(), switch_off_bkg.getHeight());
	}

	
	//绘制控件 (开关)
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Matrix matrix = new Matrix();//图片显示风格
		Paint paint = new Paint();//画笔 刷子
		
		//绘制开关的背景图片
		if(currentX > switch_off_bkg.getWidth()/2){
			//开关开启状态
			canvas.drawBitmap(switch_on_bkg, matrix, paint);
		} else {
			//开关关闭状态
			canvas.drawBitmap(switch_off_bkg, matrix, paint);
		}
		
		
		//绘制滑动块
		
		float left_slip = 0;//滑动块的左边
		
		if(isSlipping){//处于滑动状态时
			
			if(currentX > switch_off_bkg.getWidth()){
				//划过去了
				//指定滑动块左边位置
				left_slip = switch_off_bkg.getWidth() - slip_btn.getWidth();
				
			} else {
				
				left_slip = currentX - slip_btn.getWidth()/2;
			}
		} else { //非滑动状态时
			
			if(isSwitchOn){//开启
				left_slip = rect_on.left;
			} else { //关闭
				left_slip = rect_off.left;
			}
			
		}
		
		if(left_slip < 0){
			left_slip = 0;
		} else if(left_slip > switch_off_bkg.getWidth() - slip_btn.getWidth()){
			left_slip = switch_off_bkg.getWidth() - slip_btn.getWidth();
		}
		
		canvas.drawBitmap(slip_btn, left_slip, 0, paint);
		
	}
	
	
	
}
