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

	// 底部小点的图片
	private ImageView[] points;

	// 记录当前选中位置
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

		// 设置监听
		vp_guide.setOnPageChangeListener(this);

		// 初始化底部小点
		initPoint();

	}

	/**
	 * 初始化底部小点
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

		points = new ImageView[mListFragment.size()];

		// 循环取得小点图片
		for (int i = 0; i < mListFragment.size(); i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		points[currentIndex].setEnabled(false);
	}

	/**
	 * 当滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int position) {

	}

	/**
	 * 当当前页面被滑动时调用
	 */
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {

	}

	/**
	 * 当新的页面被选中时调用
	 */

	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);

	}

	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= mListFragment.size()) {
			return;
		}
		vp_guide.setCurrentItem(position);
	}

	/**
	 * 设置当前的小点的位置
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
