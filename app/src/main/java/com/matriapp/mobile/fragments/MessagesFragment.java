package com.matriapp.mobile.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.DashboardActivity;
import com.matriapp.mobile.activities.LoginActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.QuickMessageActivity;
import com.matriapp.mobile.adapter.LiveMessageAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.EndlessRecyclerViewScrollListener;
import com.matriapp.mobile.custom.RecyclerItemTouchHelper;
import com.matriapp.mobile.model.QuickItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MessagesFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private RecyclerView recycler_inbox, recycler_live;
    private EditText search_view;
    private RelativeLayout progressBar;
    private SwipeRefreshLayout swipe;
    private Common common;
    private SessionManager session;
    private boolean continue_request;
    private boolean isFirst = true;
    private List<QuickItem> list = new ArrayList<>();
    private List<QuickItem> liveList = new ArrayList<>();
    private MessageAdapter adapter;
    private LiveMessageAdapter liveMessageAdapter;
    private int page = 0;
    private TextView tv_no_data;
    private BroadcastReceiver receiver;
    private IntentFilter mIntentFilter = null;
    private Context context;
    private boolean paidFlag;
    private ConstraintLayout llView;


    public MessagesFragment() {

        this.setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.home, menu);
//        final MenuItem action_search = menu.findItem(R.id.action_search);
//        action_search.setVisible(false);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        context = getContext();
        session = new SessionManager(context);
        common = new Common(context);
        updateOnlineOfflineStatus("Online");
        ((DashboardActivity) getActivity()).setToolbarTitle("Messages");
//        ((DashboardActivity) getActivity()).hideToolbar(true);
        paidFlag=common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS));
        progressBar = view.findViewById(R.id.progressBar);
        swipe = view.findViewById(R.id.swipe);
        search_view = view.findViewById(R.id.search_view);
        recycler_inbox = view.findViewById(R.id.recycler_inbox);
        recycler_live = view.findViewById(R.id.recycler_live);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        llView = view.findViewById(R.id.llView);

        this.mIntentFilter = new IntentFilter(AppConstants.OUICK_TAG);

        Bundle b = getActivity().getIntent().getExtras();
        if (b != null && b.containsKey("body")) {
            Log.e("resp", b.getString("body") + "  ");
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = search_view.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, RecyclerView.HORIZONTAL,false);
        recycler_live.setLayoutManager(mLayoutManager1);
        liveMessageAdapter = new LiveMessageAdapter(context, liveList, paidFlag,llView);
        recycler_live.setAdapter(liveMessageAdapter);

        getOnlineMembers();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recycler_inbox.setLayoutManager(mLayoutManager);
        recycler_inbox.setItemAnimator(new DefaultItemAnimator());
        //  recycler_inbox.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.message_divider));
        recycler_inbox.addItemDecoration(itemDecoration);
        adapter = new MessageAdapter(context, list);
        recycler_inbox.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> {
            list.clear();
            page = 0;
            page = page + 1;
            getMessage(page);
        });

        page = page + 1;
        getMessage(page);

//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_inbox);

        recycler_inbox.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int pag, int totalItemsCount, RecyclerView view) {
                if (continue_request) {
                    page = page + 1;
                    getMessage(page);
                }
            }
        });


        view.findViewById(R.id.etRefresh).setOnClickListener(vm -> {
            list.clear();
            page = 0;
            page = page + 1;
            getMessage(page);
        });

        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            list.clear();
            page = 0;
            page = page + 1;
            getMessage(page);
        }
        session = new SessionManager(context);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, this.mIntentFilter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        super.onPause();
    }

    private void deleteMessge(final int position) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", list.get(position).getOtherID());

        common.makePostRequest(AppConstants.delete_user_message, param, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    adapter.removeItem(position);
                    common.showAlert("Delete", object.getString("message"), R.drawable.trash_red);
                    if (list.size() == 0) {
                        tv_no_data.setVisibility(View.VISIBLE);
                        recycler_inbox.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void getMessage(int page) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("page_number", String.valueOf(page));
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequestTime(AppConstants.newmessage_list, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            swipe.setRefreshing(false);
            isFirst = false;
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                continue_request = object.getBoolean("continue_request");
                int total_count = object.getInt("total_count");
                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    recycler_inbox.setVisibility(View.VISIBLE);
                    //lv_inbox.setVisibility(View.VISIBLE);
                    if (total_count != list.size()) {

                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            QuickItem item = new QuickItem();

                            item.setId(obj.getString("id"));
                            item.setOtherID(obj.getString("otherID"));
                            item.setContent(obj.getString("content"));
                            item.setSent_on(getDate(obj.getString("sent_on")));
                            item.setUnread_count(obj.getString("unread_count"));
                            item.setPhoto_url(obj.getString("photo_url"));
                            item.setUsername(obj.getString("username"));

                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    recycler_inbox.setVisibility(View.GONE);
                    //lv_inbox.setVisibil+ity(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            isFirst = false;
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private String getDate(String time) {
        String outputPattern = "dd MMM yyyy - hh:mm a";
        String inputPattern = "yyyy-MM-dd hh:mm:ss";
        String nowPatern = "hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        SimpleDateFormat nowFormat = new SimpleDateFormat(nowPatern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            if (DateUtils.isToday(date.getTime())) {
                str = nowFormat.format(date);//"Today "+
            } else {
                str = outputFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return str;
    }


    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> implements Filterable {
        private Context context;
        private List<QuickItem> list = null;
        private List<QuickItem> contactListFiltered;

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        contactListFiltered = list;
                    } else {
                        List<QuickItem> filteredList = new ArrayList<>();
                        for (QuickItem row : list) {
                            if (row.getOtherID().toLowerCase().contains(charString.toLowerCase()) || row.getContent().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }
                        contactListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    contactListFiltered = (ArrayList<QuickItem>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_name, tv_msg, tv_date, tv_count;
            public ImageView img_profile;
            public RelativeLayout viewBackground, viewForeground;

            public MyViewHolder(View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
                tv_msg = view.findViewById(R.id.tv_msg);
                tv_date = view.findViewById(R.id.tv_date);
                tv_count = view.findViewById(R.id.tv_count);
                img_profile = view.findViewById(R.id.img_profile);
                viewBackground = view.findViewById(R.id.view_background);
                viewForeground = view.findViewById(R.id.view_foreground);

                view.setOnClickListener(view1 -> {
                    tv_count.setVisibility(View.GONE);

                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, ConversationActivity.class);
                        i.putExtra("matri_id", list.get(getAdapterPosition()).getOtherID());
                        i.putExtra("username", list.get(getAdapterPosition()).getUsername());
                        startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to chat with this member.",llView);
                        startActivity(new Intent(context, PlanListActivity.class));
                    }

                });
            }
        }

        public MessageAdapter(Context context, List<QuickItem> list) {
            this.context = context;
            this.list = list;
            this.contactListFiltered = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final QuickItem item = contactListFiltered.get(position);
            int placeHolder = 0, photoProtectPlaceHolder = 0;

            if(common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))){
               String Name[]= item.getUsername().split(" ");
                if (Name.length == 3) {
                    holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]+ " " + Name[2]);
                }
                else if (Name.length == 2) {
                    holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                } else {
                    holder.tv_name.setText(Name[0].charAt(0));
                }
            }
            else
                holder.tv_name.setText(item.getUsername());

            if (!item.getUnread_count().equals("0"))
                holder.tv_count.setText(item.getUnread_count());
            else
                holder.tv_count.setVisibility(View.GONE);
            String msg = "";
            if (item.getContent().length() >= 30) {
                msg = item.getContent().substring(0, 30) + "...<font color=#a30412/>";
            } else {
                msg = item.getContent();
            }
            holder.tv_msg.setText(Html.fromHtml(msg));
            holder.tv_date.setText(item.getSent_on());
            if (item.getPhoto_url().equals(context.getString(R.string.female_pic)) || item.getPhoto_url().equals(context.getString(R.string.male_pic)))
            {
                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                    placeHolder = R.drawable.male;
                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                    placeHolder = R.drawable.female;
                }
//                holder.img_profile.setImageResource(R.drawable.placeholder);
                Picasso.get().load(placeHolder).
                        placeholder(placeHolder)
                        .error(placeHolder)
                        .into(holder.img_profile);
            }

            else
                Picasso.get().load(item.getPhoto_url()).into(holder.img_profile);
        }

        @Override
        public int getItemCount() {
            return contactListFiltered.size();
        }

        public void removeItem(int position) {
            contactListFiltered.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(QuickItem item, int position) {
            contactListFiltered.add(position, item);
            // notify item added by position
            notifyItemInserted(position);
        }
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof QuickMessageActivity.MessageAdapter.MyViewHolder) {

            // backup of removed item for undo purpose
            final QuickItem deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes", (dialogInterface, i) -> deleteMessge(deletedIndex));
            alert.setNegativeButton("No", (dialogInterface, i) -> {
                adapter.removeItem(viewHolder.getAdapterPosition());
                adapter.restoreItem(deletedItem, deletedIndex);
            });
            alert.show();
        }
    }

    private void getOnlineMembers() {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("page_number", String.valueOf(1));
        param.put("q", "");
        param.put("gender", session.getLoginData(SessionManager.KEY_GENDER));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.oneline_chat_list, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            isFirst = false;
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                continue_request = object.getBoolean("continue_request");
                int total_count = object.getInt("total_count");
                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    recycler_live.setVisibility(View.VISIBLE);
                    //lv_inbox.setVisibility(View.VISIBLE);
                    if (total_count != liveList.size()) {
                        JSONObject data = object.getJSONObject("data");
                        JSONArray results = data.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            QuickItem item = new QuickItem();

                            item.setId(obj.getString("id"));
                            item.setOtherID(obj.getString("matri_id"));
                            item.setContent(obj.getString("text"));
                            item.setPhoto_url(obj.getString("photo_url"));
                            item.setUsername(obj.getString("username"));
                            liveList.add(item);
                        }
                        liveMessageAdapter.notifyDataSetChanged();
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    recycler_live.setVisibility(View.GONE);
                    //lv_inbox.setVisibil+ity(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            isFirst = false;
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }


    private void updateOnlineOfflineStatus(String status) {
        AppDebugLog.print("updateOnlineOfflineStatus : "+status);
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("online_offline", status);

        common.makePostRequest(AppConstants.update_online_offline_status, param, response -> {

        }, error -> {

        },llView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateOnlineOfflineStatus("Offline");
    }
}