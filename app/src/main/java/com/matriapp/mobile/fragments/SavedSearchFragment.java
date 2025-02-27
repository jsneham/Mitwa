package com.matriapp.mobile.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.SearchResultActivity;
import com.matriapp.mobile.model.SavedItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedSearchFragment extends Fragment {

    private View view;
    private ListView lv_saved;
    private List<SavedItem> list = new ArrayList<>();
    private SavedAdapter adapter;
    private Common common;
    private SessionManager session;
    //private ProgressDialog pd;
    private boolean continue_request;
    private int page = 0;
    private TextView tv_no_data;
    private RelativeLayout progressBar,llView;
    public Context  context;
    private SwipeRefreshLayout swipe;

    public SavedSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        adapter = new SavedAdapter(context, list, llView);
        lv_saved.setAdapter(adapter);
        page = 0;
        page = page + 1;
        getListdata(page);

        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {
            list.clear();
            page=0;
            page = page + 1;
            getListdata(page);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        view= inflater.inflate(R.layout.fragment_saved_search, container, false);
        context=getContext();
        common = new Common(context);

        session = new SessionManager(context);

        llView = view.findViewById(R.id.llView);
        tv_no_data = view.findViewById(R.id.tv_no_data);

//        page = page + 1;
//        getListdata(page);

        lv_saved = view.findViewById(R.id.lv_saved);


        lv_saved.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        common.hideProgressRelativeLayout(progressBar);
                        page = page + 1;
                        getListdata(page);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });
        return view;
    }


    private void getListdata(final int page) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.saved_search + page, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            swipe.setRefreshing(false);

            AppDebugLog.print("response : " + response);
            try {
                JSONObject object = new JSONObject(response);
                int total_count = object.getInt("total_count");
                continue_request = object.getBoolean("continue_request");
                if (total_count != 0) {
                    lv_saved.setVisibility(View.VISIBLE);
                    tv_no_data.setVisibility(View.GONE);
                    if (total_count != list.size()) {
                        JSONArray data = object.getJSONArray("data");
                        AppDebugLog.print("data count : : " + data.length());
                        AppDebugLog.print("response : " + response);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            SavedItem item = new SavedItem();
                            item.setName(obj.getString("search_name"));
                            String phot;
                            if (obj.getString("with_photo").equals("photo_search")) {
                                phot = "With Photo";
                            } else {
                                phot = "";
                            }

                            item.setId(obj.getString("id"));
                            String detail = checkAge(obj.getString("from_age"), obj.getString("to_age")) +
                                    checkheight(common.calculateHeight(obj.getString("from_height")), common.calculateHeight(obj.getString("to_height"))) +
                                    checkNull(obj.getString("marital_status")) + checkNull(obj.getString("religion_str")) +
                                    checkNull(obj.getString("caste_str")) + checkNull(obj.getString("mother_tongue_str")) +
                                    checkNull(obj.getString("country_str")) + checkNull(obj.getString("state_str")) +
                                    checkNull(obj.getString("city_str")) + checkNull(obj.getString("education_str")) +
                                    checkNull(obj.getString("occupation_str")) + checkNull(obj.getString("employee_in")) +
                                    checkNull(obj.getString("income").replace(",", "").replace("Rs", " Rs")) + checkNull(obj.getString("diet")) +
                                    checkNull(obj.getString("drink")) + checkNull(obj.getString("smoking")) +
                                    checkNull(obj.getString("complexion")) + checkNull(obj.getString("bodytype")) +
                                    checkNull(obj.getString("keyword")) + checkNull(obj.getString("id_search")) + checkNull(phot);

                            detail = detail.trim();
                            item.setSearch_data(detail.substring(0, detail.length() - 1));
                            String search_page_nm = obj.getString("search_page_nm");
                            item.setSearch_name(search_page_nm);
                            if (search_page_nm.equals(AppConstants.TYPE_SEARCH_QUICK)) {
                                HashMap<String, String> param1 = new HashMap<>();
                                param1.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                param1.put("from_age", checkNull1(obj.getString("from_age")));
                                param1.put("to_age", checkNull1(obj.getString("to_age")));
                                param1.put("from_height", checkNull1(obj.getString("from_height")));
                                param1.put("to_height", checkNull1(obj.getString("to_height")));
                                param1.put("looking_for", checkNull1(obj.getString("marital_status")));
                                param1.put("religion", checkNull1(obj.getString("religion")));
                                param1.put("caste", checkNull1(obj.getString("caste")));
                                param1.put("mothertongue", checkNull1(obj.getString("mother_tongue")));
                                param1.put("country", checkNull1(obj.getString("country")));
                                param1.put("state", checkNull1(obj.getString("state")));
                                param1.put("city", checkNull1(obj.getString("city")));
                                param1.put("education", checkNull1(obj.getString("education")));
                                param1.put("photo_search", checkNull1(obj.getString("with_photo")));
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    param1.put("gender", "Male");
                                } else {
                                    param1.put("gender", "Female");
                                }
                                item.setSearchData(param1);
                            } else if (search_page_nm.equals(AppConstants.TYPE_SEARCH_ADVANCE)) {
                                HashMap<String, String> param1 = new HashMap<>();
                                param1.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                param1.put("from_age", checkNull1(obj.getString("from_age")));
                                param1.put("to_age", checkNull1(obj.getString("to_age")));
                                param1.put("from_height", checkNull1(obj.getString("from_height")));
                                param1.put("to_height", checkNull1(obj.getString("to_height")));
                                param1.put("looking_for", checkNull1(obj.getString("marital_status")));
                                param1.put("religion", checkNull1(obj.getString("religion")));
                                param1.put("caste", checkNull1(obj.getString("caste")));
                                param1.put("mothertongue", checkNull1(obj.getString("mother_tongue")));
                                param1.put("country", checkNull1(obj.getString("country")));
                                param1.put("state", checkNull1(obj.getString("state")));
                                param1.put("city", checkNull1(obj.getString("city")));
                                param1.put("education", checkNull1(obj.getString("education")));
                                param1.put("photo_search", checkNull1(obj.getString("with_photo")));

                                param1.put("occupation", checkNull1(obj.getString("occupation")));
                                param1.put("employee_in", checkNull1(obj.getString("employee_in")));
                                param1.put("income", checkNull1(obj.getString("income")));
                                param1.put("diet", checkNull1(obj.getString("diet")));
                                param1.put("drink", checkNull1(obj.getString("drink")));
                                param1.put("smoking", checkNull1(obj.getString("smoking")));
                                param1.put("complexion", checkNull1(obj.getString("complexion")));
                                param1.put("bodytype", checkNull1(obj.getString("bodytype")));
                                //  param1.put("star", checkNull1(obj.getString("star")));
                                //  param1.put("manglik", checkNull1(obj.getString("manglik")));
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    param1.put("gender", "Male");
                                } else {
                                    param1.put("gender", "Female");
                                }
                                item.setSearchData(param1);
                            } else if (search_page_nm.equals(AppConstants.TYPE_SEARCH_ID)) {
                                HashMap<String, String> param1 = new HashMap<>();
                                param1.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                param1.put("txt_id_search", obj.getString("id_search"));

                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    param1.put("gender", "Male");
                                } else {
                                    param1.put("gender", "Female");
                                }
                                item.setSearchData(param1);
                            } else if (search_page_nm.equals(AppConstants.TYPE_SEARCH_KEYWORD)) {
                                HashMap<String, String> param1 = new HashMap<>();
                                param1.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                param1.put("keyword", obj.getString("keyword"));
                                param1.put("photo_search", obj.getString("with_photo"));
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    param1.put("gender", "Male");
                                } else {
                                    param1.put("gender", "Female");
                                }
                                item.setSearchData(param1);
                            }
                            list.add(item);

                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    lv_saved.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
//                common.showToast(getString(R.string.err_msg_try_again_later));
            }

        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
//                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
            }
        },llView);

    }

    private String checkNull(String text) {
        if (text != null && !text.equals("") && !text.equals("null")) {
            return text + ", ";
        }
        return "";
    }

    private String checkNull1(String text) {
        if (text != null && !text.equals("") && !text.equals("null")) {
            return text;
        }
        return "";
    }

    private String checkheight(String from, String to) {
        if (from != null && to != null && !from.equals("") && !to.equals("") && !from.equals("null") && !to.equals("null")) {
            return from + " to " + to + ", ";
        } else if (from != null && !from.equals("") && !from.equals("null")) {
            return from + ", ";
        } else if (to != null && !to.equals("") && !to.equals("null")) {
            return to + ", ";
        }
        return "";
    }

    private String checkAge(String from, String to) {
        if (from == null && to == null) {
            return "";
        }

        if (from != null && to != null && !from.equals("") && !to.equals("") && !from.equals("null") && !to.equals("null")) {
            return from + " to " + to + " Year, ";
        } else if (from != null && !from.equals("") && !from.equals("null")) {
            return from + " Year, ";
        } else if (to != null && !to.equals("") && !to.equals("null")) {
            return to + " Year, ";
        }
        return "";
    }

    public class SavedAdapter extends ArrayAdapter<SavedItem> {
        Context context;
        RelativeLayout llView;
        List<SavedItem> list;

        public SavedAdapter(Context context, List<SavedItem> list, RelativeLayout llView) {
            super(context, R.layout.saved_item, list);
            this.context = context;
            this.list = list;
            this.llView = llView;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.saved_item, null, true);

            TextView tv_name = rowView.findViewById(R.id.tv_name);
            TextView tv_search_name = rowView.findViewById(R.id.tv_search_name);
            TextView tv_detail = rowView.findViewById(R.id.tv_detail);
            ImageButton img_delete = rowView.findViewById(R.id.img_delete);
            ImageButton img_Show = rowView.findViewById(R.id.img_Show);

            final SavedItem item = list.get(position);
            tv_name.setText(item.getName());
            tv_detail.setText(item.getSearch_data().replace(",", " | "));
            tv_search_name.setText("Save from " + item.getSearch_name());

            img_delete.setOnClickListener(view12 -> deleteAlert(item.getId(), position));
            img_Show.setOnClickListener(view12 -> showData(item));

            rowView.setOnClickListener(view1 -> {
                Intent i = new Intent(context, SearchResultActivity.class);
                AppDebugLog.print("param in SaveSearchActivity : " + item.getSearchData());
                i.putExtra("searchData", Common.getJsonStringFromObject(item.getSearchData()));
                startActivity(i);
            });

            return rowView;

        }


        private void showData(SavedItem item) {
            Intent i = new Intent(context, SearchResultActivity.class);
            AppDebugLog.print("param in SaveSearchActivity : " + item.getSearchData());
            i.putExtra("searchData", Common.getJsonStringFromObject(item.getSearchData()));
            startActivity(i);
        }

        private void deleteAlert(final String id, final int position) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("Are you sure you want to delete this search?");
            alert.setTitle("Delete Search");
            alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.setPositiveButton("Yes", (dialogInterface, i) -> deleteApi(id, position));
            alert.show();
        }

        private void deleteApi(String id, final int position) {
            common.showProgressRelativeLayout(progressBar);

            HashMap<String, String> param = new HashMap<>();
            param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
            param.put("saved_search_id", id);

            common.makePostRequest(AppConstants.delete_saved_search, param, response -> {
                common.hideProgressRelativeLayout(progressBar);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errormessage"), llView);
                    if (object.getString("status").equals("success")) {
                        list.remove(position);
                        if (list.size() == 0) {
                            lv_saved.setVisibility(View.GONE);
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later), llView);
                }
            }, error -> {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode), llView);
                }
            },llView);

        }
    }
}