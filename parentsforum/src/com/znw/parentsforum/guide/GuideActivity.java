package com.znw.parentsforum.guide;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.znw.parentsforum.R;

public class GuideActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp_guide;
	private FragmentOneStep mFragment1;
	private FragmentTwoStep mFragment2;
	private FragmentThreeStep mFragment3;
	private FragmentFourStep mFragment4;
	private List<Fragment> mListFragment = new ArrayList<Fragment>();
	private PagerAdapter mPgAdapter;

	// �ײ�С���ͼƬ
	private ImageView[] points;

	// ��¼��ǰѡ��λ��
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_viewpager);
		initView();
	}

	private void initView() {
		vp_guide = (ViewPager) findViewById(R.id.vp_guide);
		mFragment1 = new FragmentOneStep();
		mFragment2 = new FragmentTwoStep();
		mFragment3 = new FragmentThreeStep();
		mFragment4 = new FragmentFourStep();
		mListFragment.add(mFragment1);
		mListFragment.add(mFragment2);
		mListFragment.add(mFragment3);
		mListFragment.add(mFragment4);
		mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				mListFragment);
		vp_guide.setAdapter(mPgAdapter);

		// ���ü���
		vp_guide.setOnPageChangeListener(this);

		// ��ʼ���ײ�С��
		initPoint();

	}

	/**
	 * ��ʼ���ײ�С��
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

		points = new ImageView[mListFragment.size()];

		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < mListFragment.size(); i++) {
			// �õ�һ��LinearLayout�����ÿһ����Ԫ��
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// Ĭ�϶���Ϊ��ɫ
			points[i].setEnabled(true);
			// ��ÿ��С�����ü���
			points[i].setOnClickListener(this);
			// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
			points[i].setTag(i);
		}

		// ���õ���Ĭ�ϵ�λ��
		currentIndex = 0;
		// ����Ϊ��ɫ����ѡ��״̬
		points[currentIndex].setEnabled(false);
	}

	/**
	 * ������״̬�ı�ʱ����
	 */
	@Override
	public void onPageScrollStateChanged(int position) {

	}

	/**
	 * ����ǰҳ�汻����ʱ����
	 */
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {

	}

	/**
	 * ���µ�ҳ�汻ѡ��ʱ����
	 */

	@Override
	public void onPageSelected(int position) {
		// ���õײ�С��ѡ��״̬
		setCurDot(position);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);

	}

	/**
	 * ���õ�ǰҳ���λ��
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= mListFragment.size()) {
			return;
		}
		vp_guide.setCurrentItem(position);
	}

	/**
	 * ���õ�ǰ��С���λ��
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > mListFragment.size() - 1
				|| currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

}
