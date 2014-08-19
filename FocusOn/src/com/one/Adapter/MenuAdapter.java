package com.one.Adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.facebook.Session;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.one.Activity.R;
import com.one.Activity.FacebookLoginActivity.SessionStatusCallback;
import com.one.Entities.Category;
import com.one.Entities.FaceBookUser;
import com.one.Entities.Item;
import com.one.util.SharedpreferencesUtil;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;



public class MenuAdapter extends BaseAdapter {

	private static final String FACEBOOK_BASE_URL  		= "http://graph.facebook.com/";
	private static final String PICTURE_TYPE			= "/picture?type=normal";

	public interface MenuListener {

		void onActiveViewChanged(View v);
	}

	private Context mContext;

	private List<Object> mItems;

	private MenuListener mListener;

	private int mActivePosition = -1;

	private DisplayImageOptions 		options;

	private ImageLoader 				imageLoader;

	private SharedpreferencesUtil mSharedpreferencesUtil;

	public MenuAdapter(Context context, List<Object> items) {
		mContext = context;
		mItems = items;
		mSharedpreferencesUtil = new SharedpreferencesUtil(mContext);
		imageLoderInit();
	}

	public void setListener(MenuListener listener) {
		mListener = listener;
	}

	public void setActivePosition(int activePosition) {
		mActivePosition = activePosition;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof Item ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position) instanceof Item;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	private void imageLoderInit() {
		options = new DisplayImageOptions.Builder()
		.showImageOnFail(Color.TRANSPARENT) 
		.displayer(new RoundedBitmapDisplayer(1000))
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs()
		.build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Object item = getItem(position);

		if (item instanceof Category) {
			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(R.layout.menu_row_category, parent, false);
			}

			((TextView) v).setText(((Category) item).mTitle);
		
		}else if(position == 1 && !mSharedpreferencesUtil.getValue("ACCESS_TOKEN", "").equals("")){

			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(R.layout.menu_item_profile, parent, false);

				ImageView img = (ImageView)v.findViewById(R.id.user_imgView);
				String url = FACEBOOK_BASE_URL+mSharedpreferencesUtil.getValue("user_id", "")+PICTURE_TYPE;
				imageLoader.displayImage(url, img, options, null);

				TextView tv = (TextView)v.findViewById(R.id.user_nameView);
				tv.setText(((Item) item).mTitle);
			}

		} else {
			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(R.layout.menu_row_item, parent, false);
			}

			TextView tv = (TextView) v;
			tv.setText(((Item) item).mTitle);
			tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				tv.setCompoundDrawablesRelativeWithIntrinsicBounds(((Item) item).mIconRes, 0, 0, 0);
			} else {
				tv.setCompoundDrawablesWithIntrinsicBounds(((Item) item).mIconRes, 0, 0, 0);
			}
		}

		v.setTag(R.id.mdActiveViewPosition, position);


		return v;
	}
}
