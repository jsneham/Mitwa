package com.matriapp.mobile.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.utility.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PhotoPasswordFragment extends Fragment implements View.OnClickListener{
    private TextView lbl_photo_visi;
    private Common common;
    private SessionManager session;
    private Context context;
    private Button btn_submit;
    private RadioGroup grp_visi;
    private RelativeLayout loader;
    private CoordinatorLayout llContent;
    private boolean isfirst = true;
    private String tag = "";
    private TextView tvShowPremium, tvShowAll, tvHide;

    public PhotoPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void tabChanged(){
        getMyProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_password, container, false);

        context = getActivity();
        session = new SessionManager(context);
        common = new Common(getActivity());

        loader = view.findViewById(R.id.loader);
        lbl_photo_visi = view.findViewById(R.id.lbl_photo_visi);
        common.setDrawableLeftTextViewLeft(R.drawable.eye_pink, lbl_photo_visi);
        btn_submit = view.findViewById(R.id.btn_id);
        btn_submit.setOnClickListener(view1 -> changeVisiApi(tag));

//        grp_visi = view.findViewById(R.id.grp_visi);

        getMyProfile();

//        grp_visi.setOnCheckedChangeListener((radioGroup, i) -> {
//            if (!isfirst) {
//                RadioButton checkedRadioButton = radioGroup.findViewById(i);
//                String val = "";
//                if (checkedRadioButton.getText().toString().equals("Hide for All")) {
//                    val = "0";
//                } else if (checkedRadioButton.getText().toString().equals("Visible to All")) {
//                    val = "1";
//                } else if (checkedRadioButton.getText().toString().equals("Visible to only paid members")) {
//                    val = "2";
//                }
//                changeVisiApi(val);
//            }
//
//        });

        tvHide = view.findViewById(R.id.tvHide);
        tvShowAll = view.findViewById(R.id.tvShowAll);
        tvShowPremium = view.findViewById(R.id.tvShowPremium);
        tvHide.setOnClickListener(this);
        tvShowAll.setOnClickListener(this);
        tvShowPremium.setOnClickListener(this);

        return view;
    }
    
    private void changeVisiApi(String val) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("photo_view_status", val);
        param.put("action", "photo_view_status");

        common.makePostRequest(AppConstants.photo_visibility_status, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);


                if (object.getString("status").equals("success")) {
                    common.showToast(getString(R.string.photo_settings_updated),llContent);
                      getMyProfile();
                }
                else common.showToast(object.getString("errmessage"),llContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    String photo_view_status = data.getString("photo_view_status");
                    if (photo_view_status.equals("0")) {
                        setTextViewDrawableColor(tvHide,R.color.colorAccent);
                        setTextViewDrawableColor(tvShowAll,R.color.sub_tab_default_color);
                        setTextViewDrawableColor(tvShowPremium,R.color.sub_tab_default_color);
                    } else if (photo_view_status.equals("1")) {
                        setTextViewDrawableColor(tvHide,R.color.sub_tab_default_color);
                        setTextViewDrawableColor(tvShowAll,R.color.colorAccent);
                        setTextViewDrawableColor(tvShowPremium,R.color.sub_tab_default_color);
                    } else if (photo_view_status.equals("2")) {
                        setTextViewDrawableColor(tvHide,R.color.sub_tab_default_color);
                        setTextViewDrawableColor(tvShowAll,R.color.sub_tab_default_color);
                        setTextViewDrawableColor(tvShowPremium,R.color.colorAccent);
                    }
//                    if (photo_view_status.equals("0")) {
//                        ((RadioButton) grp_visi.getChildAt(0)).setChecked(true);
//                    } else if (photo_view_status.equals("1")) {
//                        ((RadioButton) grp_visi.getChildAt(1)).setChecked(true);
//                    } else if (photo_view_status.equals("2")) {
//                        ((RadioButton) grp_visi.getChildAt(2)).setChecked(true);
//                    }
//                    grp_visi.check(grp_visi.getCheckedRadioButtonId());
                    String photo_protect = data.getString("photo_protect");
                }
                isfirst = false;

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvHide:
                tag="0";
                setTextViewDrawableColor(tvHide,R.color.colorAccent);
                setTextViewDrawableColor(tvShowAll,R.color.sub_tab_default_color);
                setTextViewDrawableColor(tvShowPremium,R.color.sub_tab_default_color);
                break;
            case R.id.tvShowAll:
                tag="1";
                setTextViewDrawableColor(tvHide,R.color.sub_tab_default_color);
                setTextViewDrawableColor(tvShowAll,R.color.colorAccent);
                setTextViewDrawableColor(tvShowPremium,R.color.sub_tab_default_color);
                break;
            case R.id.tvShowPremium:
                tag="2";
                setTextViewDrawableColor(tvHide,R.color.sub_tab_default_color);
                setTextViewDrawableColor(tvShowAll,R.color.sub_tab_default_color);
                setTextViewDrawableColor(tvShowPremium,R.color.colorAccent);
                break;
        }
    }

    private void setTextViewDrawableColor(@NonNull TextView textView, @ColorRes int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this.context, color), PorterDuff.Mode.SRC_IN));
            }
        }
    }




}
