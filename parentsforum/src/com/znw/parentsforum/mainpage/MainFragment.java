package com.znw.parentsforum.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.znw.parentsforum.R;
import com.znw.parentsforum.entity.Quanzi;
import com.znw.parentsforum.entity.Tiezi;

public class MainFragment extends Fragment {
	private View view;
	private ViewPager advPager = null;
	private Context context;
	private ImageView[] imageViews;
	private ImageView imageView;
	protected boolean isContinue = true;
	private AtomicInteger what = new AtomicInteger(1);
	// 图片列表
	List<View> advPics = new ArrayList<View>();

	public int[] picArry = new int[] { R.drawable.advertising_default_1,
			R.drawable.advertising_default_2, R.drawable.advertising_default_3,
			R.drawable.advertising_default };
	// 新建一个ExpandableListView
	ExpandableListView expandableListView;
	// 用来标识是否设置二級item背景色为绿色,初始值为-1既为选中状态
	private int child_groupId = -1;
	private int child_childId = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.index, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViewPager();
	}

	private void initViewPager() {
		advPager = (ViewPager) view.findViewById(R.id.adv_pager);
		if (picArry != null && advPics.size() == 0) {
			// 无论是否多于1个，都要初始化第一个（index:0）
			ImageView firstimg = new ImageView(context);
			firstimg.setBackgroundResource(picArry[picArry.length - 1]);

			advPics.add(firstimg);
			// 注意，如果不只1个，mViews比mList多两个（头尾各多一个）
			// 假设：mList为mList[0~N-1], mViews为mViews[0~N+1]
			// mViews[0]放mList[N-1], mViews[i]放mList[i-1], mViews[N+1]放mList[0]
			// mViews[1~N]用于循环；首位之前的mViews[0]和末尾之后的mViews[N+1]用于跳转
			// 首位之前的mViews[0]，跳转到末尾（N）；末位之后的mViews[N+1]，跳转到首位（1）
			if (picArry.length > 1) { // 多于1个要循环
				for (int i = 0; i < picArry.length; i++) {
					ImageView imgview = new ImageView(context);
					imgview.setBackgroundResource(picArry[i]);
					advPics.add(imgview);
				}
				ImageView lastimg = new ImageView(context);
				lastimg.setBackgroundResource(picArry[0]);

				advPics.add(lastimg);
			}
		}

		// group是R.layou.mainview中的负责包裹小圆点的LinearLayout.
		ViewGroup group = (ViewGroup) view.findViewById(R.id.viewGroup);
		if (group.getChildCount() == 0) {
			imageViews = new ImageView[advPics.size()];
			for (int i = 0; i < advPics.size(); i++) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new LayoutParams(20, 20));
				imageView.setPadding(20, 0, 20, 0);
				if (i == 0 || i == (advPics.size() - 1)) {
					imageView.setVisibility(View.INVISIBLE);
				}
				imageViews[i] = imageView;
				if (i == 0) {
					// 默认选中第一张图片
					imageViews[i]
							.setBackgroundResource(R.drawable.banner_dian_focus);
				} else {
					imageViews[i]
							.setBackgroundResource(R.drawable.banner_dian_blur);
				}
				group.addView(imageViews[i]);
			}
		}
		advPager.setAdapter(new AdvAdapter(advPics));
		advPager.setCurrentItem(1);
		advPager.setOnPageChangeListener(new GuidePageChangeListener());
		advPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isContinue = false;
					break;
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				default:
					isContinue = true;
					break;
				}
				return false;
			}
		});
		// 定时滑动线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						viewHandler.sendEmptyMessage(what.get());
						whatOption();
					}
				}
			}

		}).start();

		// 新建一个ExpandableListView
		expandableListView = (ExpandableListView) view
				.findViewById(R.id.elv_quanzilist);
		// 设置默认图标为不显示状态
		expandableListView.setGroupIndicator(null);
		// 为列表绑定数据源
		expandableListView.setAdapter(new ExpandableListAdapter());

		// 设置一级item点击的监听器
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Intent intent=new Intent(context,TieziActivity.class);
				startActivity(intent);
				// 刷新界面
				// ((BaseExpandableListAdapter)
				// elaadapter).notifyDataSetChanged();
				return true;
			}
		});

		// 设置二级item点击的监听器
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent=new Intent(context,TieziActivity.class);
				startActivity(intent);
				// 将被点击的一丶二级标签的位置记录下来
				// child_groupId = groupPosition;
				// child_childId = childPosition;
				// 刷新界面
				// ((BaseExpandableListAdapter)
				// elaadapter).notifyDataSetChanged();
				return true;
			}
		});
		// 将所有圈子设置成默认展开
		int groupCount = expandableListView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandableListView.expandGroup(i);
		}
		;
	}

	/**
	 * 操作圆点轮换变背景
	 */
	private void whatOption() {
		what.incrementAndGet();
		if (what.get() > imageViews.length - 1) {
			what.getAndAdd(-(imageViews.length - 1));
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
	}

	/**
	 * 处理定时切换图片的句柄
	 */
	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			advPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	public class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[position]
						.setBackgroundResource(R.drawable.banner_dian_focus);
				if (position != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.banner_dian_blur);
				}
			}
			if (advPics.size() > 1) { // 多于1，才会循环跳转
				if (position < 1) { // 首位之前，跳转到末尾（N）
					position = advPics.size() - 2;
					advPager.setCurrentItem(position, false);
				} else if (position > picArry.length) { // 末位之后，跳转到首位（1）
					advPager.setCurrentItem(1, false); // false:不显示跳转过程的动画
					position = 1;
				}
			}

		}

	}

	/**
	 * 首页PaperView 图片适配器
	 */
	private final class AdvAdapter extends PagerAdapter {
		private List<View> views = null;

		public AdvAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(views.get(arg1), 0);
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	public static List<Quanzi> createQuanzi() {
		Tiezi tiezi1 = new Tiezi();
		tiezi1.setTid(1);
		tiezi1.setTname("tiezi1");
		tiezi1.setCreatetiem("6小时前");
		tiezi1.setBackcount(100);

		Tiezi tiezi2 = new Tiezi();
		tiezi2.setTid(2);
		tiezi2.setTname("tiezi2");
		tiezi2.setCreatetiem("8小时前");
		tiezi2.setBackcount(110);

		Tiezi tiezi3 = new Tiezi();
		tiezi3.setTid(2);
		tiezi3.setTname("tiezi3第三方");
		tiezi3.setCreatetiem("9小时前");
		tiezi3.setBackcount(210);

		Tiezi tiezi4 = new Tiezi();
		tiezi4.setTid(4);
		tiezi4.setTname("tiezi4dfa");
		tiezi4.setCreatetiem("18小时前");
		tiezi4.setBackcount(170);
		Tiezi tiezi5 = new Tiezi();
		tiezi5.setTid(2);
		tiezi5.setTname("帖子5aaaa");
		tiezi5.setCreatetiem("一天前");
		tiezi5.setBackcount(210);

		Tiezi tiezi6 = new Tiezi();
		tiezi6.setTid(4);
		tiezi6.setTname("帖子6dsd");
		tiezi6.setCreatetiem("三天前");
		tiezi6.setBackcount(170);

		List<Tiezi> tieziList = new ArrayList<Tiezi>();
		tieziList.add(tiezi1);
		tieziList.add(tiezi2);

		List<Tiezi> tieziList2 = new ArrayList<Tiezi>();
		tieziList2.add(tiezi3);
		tieziList2.add(tiezi4);
		
		List<Tiezi> tieziList3 = new ArrayList<Tiezi>();
		tieziList3.add(tiezi5);
		tieziList3.add(tiezi6);

		Quanzi quanzi1 = new Quanzi();
		quanzi1.setId(100);
		quanzi1.setName("圈子啊1");
		quanzi1.setPersoncount(252);
		quanzi1.setTiezilist(tieziList);

		Quanzi quanzi2 = new Quanzi();
		quanzi2.setId(200);
		quanzi2.setName("圈子");
		quanzi2.setPersoncount(88);
		quanzi2.setTiezilist(tieziList2);
		
		Quanzi quanzi3 = new Quanzi();
		quanzi3.setId(200);
		quanzi3.setName("圈子123");
		quanzi3.setPersoncount(128);
		quanzi3.setTiezilist(tieziList2);
		List<Quanzi> quanziList = new ArrayList<Quanzi>();

		quanziList.add(quanzi1);
		quanziList.add(quanzi2);
		quanziList.add(quanzi3);
		return quanziList;
	}

	List<Quanzi> readJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		List<Quanzi> quanzilist1 = createQuanzi();
		String quanziStr = gson.toJson(quanzilist1);

		List<Quanzi> quanziList2 = gson.fromJson(quanziStr,
				new TypeToken<List<Quanzi>>() {
				}.getType());
		System.out.println("----" + quanziList2);
		return quanziList2;

	}

	private final class ExpandableListAdapter extends BaseExpandableListAdapter {
		/**
		 * 数据源
		 */
		List<Quanzi> quanzilist = readJson();

		/**
		 * 获取一级标签总数
		 */
		@Override
		public int getGroupCount() {
			return quanzilist.size();
		}

		/**
		 * 获取一级标签内容
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return quanzilist.get(groupPosition);
		}

		/**
		 * 获取一级标签的ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 获取一级标签下二级标签的总数
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return quanzilist.get(groupPosition).getTiezilist().size();
		}

		/**
		 * 获取一级标签下二级标签的内容
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return quanzilist.get(groupPosition).getTiezilist()
					.get(childPosition);
		}

		/**
		 * 获取二级标签的ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 指定位置相应的组视图
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * 对一级标签进行设置
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 为视图对象指定布局
			convertView = (LinearLayout) LinearLayout.inflate(context,
					R.layout.group, null);
			/**
			 * 声明视图上要显示的控件
			 */
			// 新建一个ImageView对象，用来显示一级标签上的logo图片
			ImageView group_logo = (ImageView) convertView
					.findViewById(R.id.group_logo);
			// 新建一个TextView对象，用来显示一级标签上的标题信息
			TextView group_title = (TextView) convertView
					.findViewById(R.id.group_title);
			// 新建一个TextView对象，用来显示一级标签上的圈子人数的信息
			TextView group_text = (TextView) convertView
					.findViewById(R.id.group_count);
			/**
			 * 设置相应控件的内容
			 */
			// 设置要显示的图片
			group_logo.setBackgroundResource(R.drawable.quanzilogo);
			// 设置标题上的文本信息
			group_title.setText(quanzilist.get(groupPosition).getName());
			// 设置整体描述上的文本信息
			group_text.setText("圈子人数:"
					+ quanzilist.get(groupPosition).getPersoncount());

			// 返回一个布局对象
			return convertView;
		}

		/**
		 * 对一级标签下的二级标签进行设置
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// 为视图对象指定布局
			convertView = (LinearLayout) LinearLayout.inflate(context,
					R.layout.child, null);
			// 帖子标题
			TextView tv_tieziname = (TextView) convertView
					.findViewById(R.id.tv_tieziname);
			// 帖子创建时间
			TextView tv_tiezitime = (TextView) convertView
					.findViewById(R.id.tv_tiezitime);
			// 帖子回帖数量
			TextView tv_tiezibackcount = (TextView) convertView
					.findViewById(R.id.tv_tiezibackcount);

			// 设置要显示的文本信息
			tv_tieziname.setText(quanzilist.get(groupPosition).getTiezilist()
					.get(childPosition).getTname());
			tv_tiezitime.setText(quanzilist.get(groupPosition).getTiezilist()
					.get(childPosition).getCreatetiem());
			tv_tiezibackcount.setText(quanzilist.get(groupPosition)
					.getTiezilist().get(childPosition).getBackcount()
					+ "条回帖");
			// 返回一个布局对象
			return convertView;
		}

		/**
		 * 当选择子节点的时候，调用该方法
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};
}
