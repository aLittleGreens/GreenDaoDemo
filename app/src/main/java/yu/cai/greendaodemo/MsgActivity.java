package yu.cai.greendaodemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.App;
import bean.ChatList;
import bean.Event;
import bean.MessageList;
import yu.cai.greendao.gen.ChatListDao;
import yu.cai.greendao.gen.DaoSession;
import yu.cai.greendao.gen.MessageListDao;

public class MsgActivity extends AppCompatActivity {
    private static final String TAG = "MsgActivity";
    private DaoSession daoSession;
    private ChatListDao chatListDao;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private TextView emptyText;
    private List<ChatList> chatLists;
    private ChatListAdapter chatListAdapter;

    long i = 0;
    private MessageListDao messageListDao;
    private String subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        EventBus.getDefault().register(this);
        daoSession = ((App) getApplication()).getDaoSession();
        chatListDao = daoSession.getChatListDao();
        messageListDao = daoSession.getMessageListDao();
        subscribe = getSharedPreferences("msg_db",
                 Activity.MODE_PRIVATE) .getString("subscribe","cyk");

        initData();//模拟数据
        getDaoData();
        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        chatLists.clear();
//                        chatLists.addAll(chatListDao.queryBuilder().list());
//                        if(chatLists.size()>0){
//                            emptyText.setVisibility(View.GONE);
//                        }else {
//                            emptyText.setVisibility(View.VISIBLE);
//                        }
                        initData();
                        chatLists.clear();
                        chatLists.addAll(chatListDao.queryBuilder().list());
                        chatListAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },2000);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,"position:"+position+",id:"+position);
                ChatListItemDialog messageDialog = new ChatListItemDialog(MsgActivity.this,chatLists.get(position));
                messageDialog.show();
            }
        });
    }

    private void getDaoData() {
        chatLists = chatListDao.queryBuilder().list();
    }

    private void initData() {
        ChatList charList = new ChatList(i,i+"分钟前","你好啊"+i,subscribe,i,R.mipmap.ic_launcher);
        ChatList chatListNew = chatListDao.queryBuilder().where(ChatListDao.Properties.FriendId.eq(charList.getFriendId())).build().unique();
        if(chatListNew != null){
            chatListDao.update(charList);

        }else{
            chatListDao.insert(charList);
        }
        i++;
        messageListDao.insert(new MessageList(null,false,charList.getMessage(),charList.getFriendId()));
    }

    private void initView() {

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        listView = (ListView) findViewById(R.id.listView);
        emptyText = (TextView) findViewById(R.id.empty_text);
//        if(chatLists.size()>0){
//            emptyText.setVisibility(View.GONE);
//        }else {
//            emptyText.setVisibility(View.VISIBLE);
//        }
        chatListAdapter = new ChatListAdapter(this,chatLists);
        listView.setAdapter(chatListAdapter);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  receiveMsg(Event.ReceiveEvent receiveEvent){

//        try {
//            JSONObject json = new JSONObject(receiveEvent.msg);
//            String message = json.getString("message");
//            Long friendId = json.getLong("friendId");
//
//            MessageList messageList = new MessageList();
//            messageList.isMyMsg = false;
//            messageList.setMsg(message);
//            messageList.setMessageId(friendId);
//            messageListDao.insert(messageList);
//
//            ChatList chatList = chatListDao.queryBuilder().where(ChatListDao.Properties.FriendId.eq(friendId)).build().unique();
//
//            chatList.setMessage(message);
//            chatListDao.update(chatList);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }


}
