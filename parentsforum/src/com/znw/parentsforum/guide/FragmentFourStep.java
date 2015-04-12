package com.znw.parentsforum.guide;

import com.znw.parentsforum.R;
import com.znw.parentsforum.login.LoginActivity;
import com.znw.parentsforum.util.AnimationUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FragmentFourStep extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_four, container, false);
		view.findViewById(R.id.tvInNew).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(getActivity(),
								LoginActivity.class));
						AnimationUtil.finishActivityAnimation(getActivity());
					}
				});
		return view;
	}

}
