package com.matriapp.mobile.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.SearchResultActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class IdSearchFragment extends Fragment {
    private EditText et_matri_id;
    private Common common;
    private SessionManager session;
    private Context context;
    private Button btn_save_search, btn_search;
    private RelativeLayout loader,llView;

    public IdSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_id_search, container, false);

        loader = view.findViewById(R.id.loader);
        llView = view.findViewById(R.id.llView);
        et_matri_id = view.findViewById(R.id.et_keyword);
        btn_search = view.findViewById(R.id.btn_search);
        btn_save_search = view.findViewById(R.id.btn_save_search);
        btn_save_search.setOnClickListener(view1 ->{
            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                showAlert();
            }
            else premiumMemebrSheet();
        } );

        btn_search.setOnClickListener(view12 -> searchData());

        return view;
    }

    private void searchData() {
        final String matri = et_matri_id.getText().toString().trim();
        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {

            if (matri.equals("")) {
                et_matri_id.setError("Please enter profile id");
                return;
            }
            HashMap<String, String> param = new HashMap<>();
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            param.put("txt_id_search", matri);

            if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                param.put("gender", "Male");
            } else {
                param.put("gender", "Female");
            }

            Intent i = new Intent(context, SearchResultActivity.class);
            i.putExtra("searchData", Common.getJsonStringFromObject(param));
            startActivity(i);
        }
        else premiumMemebrSheet();
    }

    private void showAlert() {
        final String matri = et_matri_id.getText().toString().trim();
        if (matri.equals("")) {
            et_matri_id.setError("Matri id require");
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_search, null);
        dialogBuilder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.editText);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog alertDialog = dialogBuilder.create();

        btnCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnSave.setOnClickListener(view -> {

            if (editText.getText().length() > 0) {
                alertDialog.dismiss();
                Validadvance(editText.getText().toString().trim(), matri);
            } else
                editText.setError("Please enter title");


        });

        alertDialog.show();
    }

    private void Validadvance(String name, String matri) {
        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
            HashMap<String, String> param = new HashMap<>();
            param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
            param.put("txt_id_search", matri);

            param.put("save_search", name);
            param.put("search_page_nm", AppConstants.TYPE_SEARCH_ID);
            param.put("gender", session.getLoginData(SessionManager.KEY_GENDER));



            add_save(param);
        }
        else premiumMemebrSheet();
    }

    private void add_save(HashMap<String, String> param) {
        common.showProgressRelativeLayout(loader);
        common.makePostRequest(AppConstants.save_search, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errormessage"),llView);
                if (object.getString("status").equals("success")) {
                    et_matri_id.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> common.hideProgressRelativeLayout(loader),llView);
    }


    public void premiumMemebrSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.premiummemebrsheet);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        Button btnUpgrade = bottomSheetDialog.findViewById(R.id.btnUpgrade);
        TextView tv = bottomSheetDialog.findViewById(R.id.tv);


        close.setOnClickListener(v->{
            bottomSheetDialog.dismiss();
        });

        btnUpgrade.setOnClickListener(v->{
            bottomSheetDialog.dismiss();
            startActivity(new Intent(context, PlanListActivity.class));

        });
        bottomSheetDialog.show();
    }
}
