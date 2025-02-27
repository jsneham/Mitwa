package com.matriapp.mobile.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matriapp.mobile.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener  {

    private RecyclerView recycler_inbox;
    private EditText search_view;
    private RelativeLayout loader;
    private SwipeRefreshLayout swipe;
    private Common common;
    private SessionManager session;
    private boolean continue_request;
    private boolean isFirst = true;
    private List<QuickItem> list = new ArrayList<>();
    private MessageAdapter adapter;
    private int page = 0;
    private TextView tv_no_data;
    private BroadcastReceiver receiver;
    private IntentFilter mIntentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat");
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        });

        session = new SessionManager(this);
        common = new Common(this);

        updateOnlineOfflineStatus("Online");

        loader = findViewById(R.id.loader);
        swipe = findViewById(R.id.swipe);
        search_view = findViewById(R.id.search_view);
        recycler_inbox = findViewById(R.id.recycler_inbox);
        tv_no_data = findViewById(R.id.tv_no_data);

        this.mIntentFilter = new IntentFilter(AppConstants.OUICK_TAG);

        Bundle b = getIntent().getExtras();
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

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search_view.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_inbox.setLayoutManager(mLayoutManager);
        recycler_inbox.setItemAnimator(new DefaultItemAnimator());
        recycler_inbox.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(getApplicationContext(), list);
        recycler_inbox.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> {
//            if (search_view.getText().length() > 0) {
//                list.clear();
//                page = 1;
//                getOnlineMembers(search_view.getText().toString());
//            } else {
            list.clear();
            page = 1;
            getOnlineMembers(page);
//            }
        });

        page = 1;
        getOnlineMembers(page);

//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_inbox);

        recycler_inbox.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int pag, int totalItemsCount, RecyclerView view) {
                if (continue_request) {
                    page = 1;
                    getOnlineMembers(page);
                }
            }
        });

        findViewById(R.id.etRefresh).setOnClickListener(vm -> {
            list.clear();
            page = 0;
            page = page + 1;
            getOnlineMembers(page);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            list.clear();
            page = 0;
            page = page + 1;
            getMessage(page);
        }
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, this.mIntentFilter);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        updateOnlineOfflineStatus("Offline");
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void updateOnlineOfflineStatus(String status) {
        AppDebugLog.print("updateOnlineOfflineStatus : "+status);
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("online_offline", status);

        common.makePostRequest(AppConstants.update_online_offline_status, param, response -> {

        }, error -> {

        },recycler_inbox);
    }

    private void deleteMessge(final int position) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("other_id", list.get(position).getId());

        common.makePostRequest(AppConstants.delete_user_chat, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),recycler_inbox);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),recycler_inbox);
            }
        },recycler_inbox);
    }

    private void getOnlineMembers(int page) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("page_number", String.valueOf(page));
        param.put("q", "");
        param.put("gender", session.getLoginData(SessionManager.KEY_GENDER));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.oneline_chat_list, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),recycler_inbox);
            }
        }, error -> {
            isFirst = false;
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),recycler_inbox);
            }
        },recycler_inbox);
    }

    private void getMessage(int page) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("page_number", String.valueOf(page));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.chat_list, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                            item.setPhoto_url(obj.getString("photo_url"));
                            item.setUsername(obj.getString("username"));
                            item.setUnread_count(obj.getString("unread_count"));
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
                common.showToast(getString(R.string.err_msg_try_again_later),recycler_inbox);
            }
        }, error -> {
            isFirst = false;
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),recycler_inbox);
            }
        },recycler_inbox);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();
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
                    Intent i = new Intent(context, ChatConversationActivity.class);
                    i.putExtra("matri_id", list.get(getAdapterPosition()).getId());
                    i.putExtra("username", list.get(getAdapterPosition()).getUsername());
                    i.putExtra("profile_img", list.get(getAdapterPosition()).getPhoto_url());
                    startActivity(i);
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_chat, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final QuickItem item = contactListFiltered.get(position);

            holder.tv_name.setText(item.getOtherID());
            if (item.getUnread_count()!= null &&   !item.getUnread_count().equals("0"))
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
            if (!item.getPhoto_url().equals(""))
                Picasso.get().load(item.getPhoto_url()).into(holder.img_profile);
            else
                holder.img_profile.setImageResource(R.drawable.placeholder);
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
        if (viewHolder instanceof MessageAdapter.MyViewHolder) {

            // backup of removed item for undo purpose
            final QuickItem deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
            alert.setMessage("Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes", (dialogInterface, i) -> deleteMessge(deletedIndex));
            alert.setNegativeButton("No", (dialogInterface, i) -> {
                adapter.removeItem(viewHolder.getAdapterPosition());
                adapter.restoreItem(deletedItem, deletedIndex);
            });
            alert.show();
        }
    }
}