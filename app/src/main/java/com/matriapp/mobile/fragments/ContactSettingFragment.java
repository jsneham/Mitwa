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

import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ContactSettingFragment extends Fragment implements View.OnClickListener{
    private TextView lbl_contact_visi,tvShowPaid,tvShowOnly;
    private Common common;
    private RadioGroup grp_visi;
    private SessionManager session;
    private Context context;
    private Button btn_submit;
    private RelativeLayout loader;
    private CoordinatorLayout llView;
    private String tag="";
    public ContactSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_setting, container, false);

        common = new Common(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();

        loader = view.findViewById(R.id.loader);
        lbl_contact_visi = view.findViewById(R.id.lbl_contact_visi);
//        common.setDrawableLeftTextViewLeft(R.drawable.eye_pink, lbl_contact_visi);
        btn_submit = view.findViewById(R.id.btn_id);
//        grp_visi = view.findViewById(R.id.grp_visi);

        tvShowOnly = view.findViewById(R.id.tvShowOnly);
        llView = view.findViewById(R.id.llView);
        tvShowPaid = view.findViewById(R.id.tvShowPaid);

        btn_submit.setOnClickListener(view1 -> changeContact());
        tvShowOnly.setOnClickListener(this);
        tvShowPaid.setOnClickListener(this);
        getMyProfile();

        return view;
    }

    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            //  Log.d("resp",response);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    String contact_view_security = data.getString("contact_view_security");
                    if (contact_view_security.equals("0")) {
                        // ((RadioButton) grp_visi.getChildAt(1)).setChecked(true);
                        setTextViewDrawableColor(tvShowOnly,R.color.colorAccent);
                        setTextViewDrawableColor(tvShowPaid,R.color.sub_tab_default_color);

                    } else if (contact_view_security.equals("1")) {
                        // ((RadioButton) grp_visi.getChildAt(0)).setChecked(true);

                        setTextViewDrawableColor(tvShowPaid,R.color.colorAccent);
                        setTextViewDrawableColor(tvShowOnly,R.color.sub_tab_default_color);


                    }
//                    if (contact_view_security.equals("0")) {
//                        ((RadioButton) grp_visi.getChildAt(1)).setChecked(true);
//                    } else if (contact_view_security.equals("1")) {
//                        ((RadioButton) grp_visi.getChildAt(0)).setChecked(true);
//                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> common.hideProgressRelativeLayout(loader),llView);
    }

    private void changeContact() {
//        int pos = grp_visi.getCheckedRadioButtonId();
//        if (pos == -1) {
//            common.showToast("Please select contact visibility");
//            return;
//        }
        if (tag.equals("")) {
            common.showToast("Please select contact visibility",llView);
            return;
        }
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
//        RadioButton btn = grp_visi.findViewById(grp_visi.getCheckedRadioButtonId());

//        if (btn.getText().toString().equals("Show to all paid members")) {
//            param.put("contact_view_security", "1");
//        } else if (btn.getText().toString().equals("Show to only express interest accepted and paid members")) {
//            param.put("contact_view_security", "0");
//        }

        if (tag.equals("Show to all premium members")) {
            param.put("contact_view_security", "1");
        } else if (tag.equals("Show to all premium members and also show to members whose Interest is accepted")) {
            param.put("contact_view_security", "0");
        }
        common.makePostRequest(AppConstants.contact_setting, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("status").equals("success")) {
                    common.showToast("Contact Settings updated",llView);
                }
                else {
                    common.showToast(object.getString("errmessage"),llView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> common.hideProgressRelativeLayout(loader),llView);

    }

    private void setTextViewDrawableColor(@NonNull TextView textView, @ColorRes int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(this.context, color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvShowPaid:
                tag="Show to all premium members";
                setTextViewDrawableColor(tvShowPaid,R.color.colorAccent);
                setTextViewDrawableColor(tvShowOnly,R.color.sub_tab_default_color);

                break;
            case R.id.tvShowOnly:
                tag="Show to all premium members and also show to members whose Interest is accepted";
                setTextViewDrawableColor(tvShowOnly,R.color.colorAccent);
                setTextViewDrawableColor(tvShowPaid,R.color.sub_tab_default_color);

                break;

        }
    }
}
