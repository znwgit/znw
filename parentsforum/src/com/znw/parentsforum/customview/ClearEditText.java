package com.znw.parentsforum.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import com.znw.parentsforum.R;

/**
 * �����ı��� �ұ����Դ���ɾ����ť ��������ʱ����ʾɾ����ť����������ʱ������ʾɾ����ť��
 * 
 * 
 */
@SuppressLint("ResourceAsColor")
public class ClearEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {
	/**
	 * ɾ����ť������
	 */
	private Drawable mClearDrawable;
	/**
	 * �ؼ��Ƿ��н���
	 */
	private boolean hasFoucs;

	private Paint mPaint;

	public ClearEditText(Context context) {
		this(context, null);
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		// ���ﹹ�췽��Ҳ����Ҫ����������ܶ����Բ�����XML���涨��
		this(context, attrs, android.R.attr.editTextStyle);
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(R.color.dodgerblue);
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// ��ȡEditText��DrawableRight,����û���������Ǿ�ʹ��Ĭ�ϵ�ͼƬ
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			// throw new
			// NullPointerException("You can add drawableRight attribute in XML");
			mClearDrawable = getResources().getDrawable(
					R.drawable.button_login_delete);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		// Ĭ����������ͼ��
		setClearIconVisible(false);
		// ���ý���ı�ļ���
		setOnFocusChangeListener(this);
		// ����������������ݷ����ı�ļ���
		addTextChangedListener(this);
	}

	/**
	 * ��Ϊ���ǲ���ֱ�Ӹ�EditText���õ���¼������������ü�ס���ǰ��µ�λ����ģ�����¼� �����ǰ��µ�λ�� �� EditText�Ŀ�� -
	 * ͼ�굽�ؼ��ұߵļ�� - ͼ��Ŀ�� �� EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ��֮�����Ǿ�������ͼ�꣬��ֱ�����û�п���
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * ��ClearEditText���㷢���仯��ʱ���ж������ַ��������������ͼ�����ʾ������
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	/**
	 * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * ��������������ݷ����仯��ʱ��ص��ķ���
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// ������
		canvas.drawLine(10, this.getHeight() - 10, this.getWidth() - 10,
				this.getHeight() - 10, mPaint);
	}

}
