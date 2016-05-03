/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zuo.biao.library.base;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.R;
import zuo.biao.library.interfaces.OnFinishListener;
import zuo.biao.library.ui.TopTabView;
import zuo.biao.library.ui.TopTabView.OnTabSelectedListener;
import zuo.biao.library.util.StringUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**基础带标签的FragmentActivity
 * 目前只有顶部tab这一种形式，以后将增加底部tab
 * @author Lemon
 * @warn 不要在子类重复这个类中onCreate中的代码
 * @use extends BaseTabActivity, 具体参考.DemoTabActivity
 * @must 在子类onCreate中调用initView();initData();initListener();
 */
public abstract class BaseTabActivity extends BaseFragmentActivity implements OnClickListener
, OnTabSelectedListener, OnFinishListener {
	private static final String TAG = "BaseTabActivity";

	/**
	 * tab被选中监听类的实例
	 */
	private OnTabSelectedListener onTabSelectedListener;
	/**设置tab被选中监听
	 * @warn 在子类super.initListener();前使用才有效
	 * @param onTabSelectedListener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
		this.onTabSelectedListener = onTabSelectedListener;
	}




	/**
	 * @param savedInstanceState
	 * @return
	 * @must 1.不要在子类重复这个类中onCreate中的代码;
	 *       2.在子类onCreate中super.onCreate(savedInstanceState);
	 *       initView();initData();initListener();
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onCreate(savedInstanceState, 0);
	}
	/**
	 * @param savedInstanceState
	 * @param layoutResID activity全局视图view的布局资源id，默认值为R.layout.base_tab_activity
	 * @return
	 * @must 1.不要在子类重复这个类中onCreate中的代码;
	 *       2.在子类onCreate中super.onCreate(savedInstanceState, layoutResID);
	 *       initView();initData();initListener();
	 */
	protected void onCreate(Bundle savedInstanceState, int layoutResID) {
		onCreate(savedInstanceState, layoutResID, null);
	}
	/**
	 * @param savedInstanceState
	 * @param listener this - 滑动返回 ; null - 没有滑动返回
	 * @return
	 * @must 1.不要在子类重复这个类中onCreate中的代码;
	 *       2.在子类onCreate中super.onCreate(savedInstanceState, listener);
	 *       initView();initData();initListener();
	 */
	protected void onCreate(Bundle savedInstanceState, OnFinishListener listener) {
		onCreate(savedInstanceState, 0, listener);
	}
	/**
	 * 该界面底层容器
	 */
	protected ViewGroup view = null;
	/**
	 * @param savedInstanceState
	 * @param layoutResID activity全局视图view的布局资源id，默认值为R.layout.base_tab_activity
	 * @param listener this - 滑动返回 ; null - 没有滑动返回
	 * @return
	 * @must 1.不要在子类重复这个类中onCreate中的代码;
	 *       2.在子类onCreate中super.onCreate(savedInstanceState, layoutResID, listener);
	 *       initView();initData();initListener();
	 */
	protected void onCreate(Bundle savedInstanceState, int layoutResID, OnFinishListener listener) {
		super.onCreate(savedInstanceState);
		super.setContentView(layoutResID <= 0 ? R.layout.base_tab_activity : layoutResID, listener);
		//类相关初始化，必须使用<<<<<<<<<<<<<<<<
		context = this;
		isAlive = true;
		fragmentManager = getSupportFragmentManager();
		//类相关初始化，必须使用>>>>>>>>>>>>>>>>

		view = (ViewGroup) findViewById(R.id.llBaseTabRootView);
	}

	//防止子类中setContentView <<<<<<<<<<<<<<<<<<<<<<<<
	@Override
	public void setContentView(int layoutResID) {
	}
	@Override
	public void setContentView(View view) {
	}
	@Override
	public void setContentView(View view, LayoutParams params) {
	}
	//防止子类中setContentView >>>>>>>>>>>>>>>>>>>>>>>>>



	// UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Nullable
	private TextView tvBaseTabTitle;

	@Nullable
	private View ivBaseTabReturn;
	@Nullable
	private TextView tvBaseTabReturn;

	@Nullable
	private ViewGroup llBaseTabTopRightButtonContainer;

	private ViewGroup llBaseTabTabContainer;
	private TopTabView topTabView;
	/**
	 * 如果在子类中调用(即super.initView());则view必须含有initView中初始化用到的id(非@Nullable标记)且id对应的View的类型全部相同；
	 * 否则必须在子类initView中重写这个类中initView内的代码(所有id替换成可用id)
	 */
	@Override
	public void initView() {// 必须调用

		tvBaseTabTitle = (TextView) findViewById(R.id.tvBaseTabTitle);

		ivBaseTabReturn = findViewById(R.id.ivBaseTabReturn);
		tvBaseTabReturn = (TextView) findViewById(R.id.tvBaseTabReturn);

		llBaseTabTopRightButtonContainer = (ViewGroup)
				findViewById(R.id.llBaseTabTopRightButtonContainer);

		llBaseTabTabContainer = (ViewGroup) findViewById(R.id.llBaseTabTabContainer);

	}

	/**
	 * 每次点击相应tab都加载，调用getFragment方法重新对点击的tab对应的fragment赋值。
	 * 如果不希望重载，可以setOnTabSelectedListener，然后在onTabSelected内重写点击tab事件。
	 */
	protected boolean needReload = false;
	/**选择并显示fragment
	 * @param position
	 */
	public void selectFragment(int position) {
		if (currentPosition == position && needReload == false) {
			Log.i(TAG, "onSelectFragment currentPosition == position && needReload == false >>> return;	");
			return;
		}
		if (needReload || fragments[position] == null) {
			fragments[position] = getFragment(position);
		}

		//全局的fragmentTransaction因为already committed 崩溃
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(fragments[currentPosition]);
		if (!fragments[position].isAdded()) {
			fragmentTransaction.add(R.id.flBaseTabFragmentContainer, fragments[position]);
		}
		fragmentTransaction.show(fragments[position]).commit();

		this.currentPosition = position;
	};



	// UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	// data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private String topReturnButtonName;

	private Fragment[] fragments;
	@Override
	public void initData() {// 必须调用

		if (tvBaseTabTitle != null) {
			tvBaseTabTitle.setVisibility(StringUtil.isNotEmpty(getTitleName(), true) ? View.VISIBLE : View.GONE);
			tvBaseTabTitle.setText(StringUtil.getTrimedString(getTitleName()));
		}

		if (ivBaseTabReturn != null) {
			topReturnButtonName = getTopReturnButtonName();

			if (topReturnButtonName == null) {
				ivBaseTabReturn.setVisibility(View.GONE);
				tvBaseTabReturn.setVisibility(View.GONE);
			} else {
				boolean isReturnButtonHasName = StringUtil.isNotEmpty(topReturnButtonName, true);
				ivBaseTabReturn.setVisibility(isReturnButtonHasName ? View.GONE : View.VISIBLE);
				tvBaseTabReturn.setVisibility(isReturnButtonHasName ? View.VISIBLE : View.GONE);
				tvBaseTabReturn.setText(StringUtil.getTrimedString(topReturnButtonName));
			}
		}

		if (llBaseTabTopRightButtonContainer != null
				&& topRightButtonList != null && topRightButtonList.size() > 0) {

			llBaseTabTopRightButtonContainer.removeAllViews();
			for (View btn : topRightButtonList) {
				llBaseTabTopRightButtonContainer.addView(btn);
			}
		}

		topTabView = new TopTabView(context, context.getLayoutInflater());
		llBaseTabTabContainer.removeAllViews();
		llBaseTabTabContainer.addView(topTabView.getView());
		topTabView.setCurrentPosition(currentPosition);
		topTabView.setView(getTabNames());


		// fragmentActivity子界面初始化<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

		fragments = new Fragment[getCount()];
		fragments[currentPosition] = getFragment(currentPosition);
		fragmentManager
		.beginTransaction()
		.add(R.id.flBaseTabFragmentContainer, fragments[currentPosition])
		.show(fragments[currentPosition])
		.commit();

		// fragmentActivity子界面初始化>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	}

	/**获取导航栏标题名
	 * @return null - View.GONE; "" - View.GONE; "xxx" - "xxx"
	 */
	@Nullable
	protected abstract String getTitleName();

	/**获取导航栏返回按钮名
	 * @return null - View.GONE; "" - <; "xxx" - "xxx"
	 */
	@Nullable
	protected abstract String getTopReturnButtonName();

	
	//top right button <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	
	@Nullable
	private List<View> topRightButtonList = new ArrayList<>();
	/**添加右上方导航栏按钮
	 * @must 在super.initData前调用
	 * @param topRightButton 不会在这个类设置监听,需要自行设置
	 */
	public <V extends View> V addTopRightButton(V topRightButton) {
		if (topRightButton != null) {
			topRightButtonList.add(topRightButton);
		}
		return topRightButton;
	}
	/**新建右上方导航栏按钮
	 * @param context
	 * @param name
	 * @return
	 */
	@SuppressLint({ "NewApi", "InflateParams" })
	public TextView newTopRightButton(Context context, String name) {
		TextView topRightButton = (TextView) LayoutInflater.from(context).inflate(R.layout.top_right_button, null);
		topRightButton.setText(name);
		return topRightButton;
	}
	
	//top right button >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	
	/**获取标签名称数组
	 * @return
	 */
	protected abstract String[] getTabNames();

	/**获取新的Fragment
	 * @param position
	 * @return
	 */
	protected abstract Fragment getFragment(int position);


	/**获取Tab(或Fragment)的数量
	 * @return
	 */
	public int getCount() {
		return topTabView == null ? 0 : topTabView.getCount();
	}

	/**获取当前Tab(或Fragment)的位置
	 * @return
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

	public TextView getCurrentTab() {
		return topTabView == null ? null : topTabView.getCurrentTab();
	};

	public Fragment getCurrentFragment() {
		return fragments[currentPosition];
	};



	// data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	// listener事件监听区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	protected int currentPosition;
	@Override
	public void initListener() {// 必须调用

		if (ivBaseTabReturn != null) {
			ivBaseTabReturn.setOnClickListener(this);
		}
		if (tvBaseTabReturn != null) {
			tvBaseTabReturn.setOnClickListener(this);
		}

		topTabView.setOnTabSelectedListener(onTabSelectedListener != null ? onTabSelectedListener : this);
	}

	@Override
	public void onTabSelected(TextView tvTab, int position, int id) {
		selectFragment(position);
	}

	// 系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ivBaseTabReturn) {
			finish();
		} else if (v.getId() == R.id.tvBaseTabReturn) {
			finish();
		}
	}


	// 类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	// 类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	// listener事件监听区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



	// 内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	// 内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}