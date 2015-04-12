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
	private boolean isSwitchOn;//��¼��ǰ���ص�״̬
	private OnSwitchStateListener switchStateListener;//���ؼ�����
	private boolean isSwitchStateListenerOn;//�Ƿ�ʹ���� ���ؼ�����
	private float currentX;
	private boolean isSlipping;//��ǰ�Ƿ���Ի��� 
	private boolean oldSwitchState = true;//ԭ�����ص�״̬

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
	 * ָ��������ʽ
	 * @param bkgSwitchOn	�����ı���ͼƬ
	 * @param bkgSwitchOff  �رյı���ͼƬ
	 * @param btnSlip		������ͼƬ
	 */
	public void setImageRes(int bkgSwitchOn, int bkgSwitchOff, int btnSlip) {
		
		switch_on_bkg = BitmapFactory.decodeResource(getResources(), bkgSwitchOn);
		switch_off_bkg = BitmapFactory.decodeResource(getResources(), bkgSwitchOff);
		slip_btn = BitmapFactory.decodeResource(getResources(), btnSlip);
		
		
		//ʹ��Rect ��¼ ���ص�λ��  �򻯿���
		//����
		rect_on = new Rect(switch_off_bkg.getWidth() - slip_btn.getWidth(), 0, switch_off_bkg.getWidth(), slip_btn.getHeight());
		//�ر�
		rect_off = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		
		
	}

	//���ÿ��ص�״̬
	public void setSwitchState(boolean state) {
		isSwitchOn =  state;
	}
	
	
	public interface OnSwitchStateListener{
		abstract void onSwitch(boolean state);
	}
	
	//�����ṩһ������״̬�ı�Ļص�����
	public void setOnSwitchStateListener(OnSwitchStateListener listener){
		switchStateListener = listener;
		
		isSwitchStateListenerOn = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://����
			
			currentX = event.getX();
			
			isSlipping = true;
			
			break;
		case MotionEvent.ACTION_MOVE://�ƶ�
			
			currentX = event.getX();
			
			break;
		case MotionEvent.ACTION_UP://�ɿ�
			
			isSlipping = false;
			
			if(currentX > switch_off_bkg.getWidth()/2){
				isSwitchOn = true;
			} else {
				isSwitchOn = false;
			}
			
			//ע���˿��ؼ����� ͬʱ����״̬�����仯ʱ ����
			if(isSwitchStateListenerOn && isSwitchOn != oldSwitchState){
				switchStateListener.onSwitch(isSwitchOn);
				
				//��ԭ����״̬������
				oldSwitchState = isSwitchOn;
			}
			
			break;

		}
		
		invalidate();//���»���
		return true;
		
//		return super.onTouchEvent(event);
	}

	
	//�����ؼ��ĳߴ��С (����)
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(switch_off_bkg.getWidth(), switch_off_bkg.getHeight());
	}

	
	//���ƿؼ� (����)
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Matrix matrix = new Matrix();//ͼƬ��ʾ���
		Paint paint = new Paint();//���� ˢ��
		
		//���ƿ��صı���ͼƬ
		if(currentX > switch_off_bkg.getWidth()/2){
			//���ؿ���״̬
			canvas.drawBitmap(switch_on_bkg, matrix, paint);
		} else {
			//���عر�״̬
			canvas.drawBitmap(switch_off_bkg, matrix, paint);
		}
		
		
		//���ƻ�����
		
		float left_slip = 0;//����������
		
		if(isSlipping){//���ڻ���״̬ʱ
			
			if(currentX > switch_off_bkg.getWidth()){
				//����ȥ��
				//ָ�����������λ��
				left_slip = switch_off_bkg.getWidth() - slip_btn.getWidth();
				
			} else {
				
				left_slip = currentX - slip_btn.getWidth()/2;
			}
		} else { //�ǻ���״̬ʱ
			
			if(isSwitchOn){//����
				left_slip = rect_on.left;
			} else { //�ر�
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
