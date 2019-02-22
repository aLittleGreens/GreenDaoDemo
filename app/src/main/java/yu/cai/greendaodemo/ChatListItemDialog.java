package yu.cai.greendaodemo;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.App;
import base.BaseDialog;
import bean.ChatList;
import bean.Event;
import bean.MessageList;
import yu.cai.greendao.gen.ChatListDao;
import yu.cai.greendao.gen.DaoSession;
import yu.cai.greendao.gen.MessageListDao;

/**
 * Created by admin on 2017-7-10.
 */

public class ChatListItemDialog extends BaseDialog {
    private static final String TAG = "ChatListItemDialog";
    private Context mContext;
    private ChatList chatList;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh_item_detail;
    private ListView lv_chat_list_item_detail;
    private TextView iv_no_data;
    private Long friendId;
    private Context applicationContext;
    private MessageListDao messageListDao;
    private List<MessageList> messageLists;
    private MsgAdapter msgAdapter;
    private EditText editText;
    private ChatListDao chatListDao;
    private List<MessageList> list;
    private String message;


    public ChatListItemDialog(Context context, ChatList chatList) {
        super(context);
        mContext = context;
        applicationContext = mContext.getApplicationContext();
        this.chatList = chatList;
        friendId = chatList.getFriendId();
        EventBus.getDefault().register(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().unregister(this);
    }

    protected ChatListItemDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.chat_list_item_detail;
    }

    @Override
    public void initView(View contentView) {
        toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.icon_back_press);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setTitle("消息详情");
        lv_chat_list_item_detail = (ListView) contentView.findViewById(R.id.lv_chat_list_item_detail);
        swipeRefresh_item_detail = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefresh_item_detail);
        iv_no_data = (TextView) contentView.findViewById(R.id.iv_no_data);
        swipeRefresh_item_detail.setColorSchemeResources(R.color.colorPrimary);

        editText = (EditText) contentView.findViewById(R.id.editText);
        contentView.findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    return;
                }
                MessageList messageList = new MessageList();
                messageList.isMyMsg = true;
                messageList.setMsg(editText.getText().toString());
                messageList.setMessageId(friendId);
                messageListDao.insert(messageList);

                chatList = chatListDao.queryBuilder().where(ChatListDao.Properties.FriendId.eq(friendId)).build().unique();
                chatList.setMessage(editText.getText().toString());
                chatListDao.update(chatList);
                updateData();
                editText.setText("");
//                if (CallBackHelp.getMessage() != null) {
//                    CallBackHelp.getMessage().sendMsg(chatList);
//                }
                JSONObject json = new JSONObject();
                try {
                    json.put("message",chatList.getMessage());
                    json.put("friendId",chatList.getFriendId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new Event.SendEvent(json.toString()));
            }
        });


    }

    @Override
    protected void getData() {
        getDaoData();

        messageLists = chatList.getMessageLists();
        msgAdapter = new MsgAdapter(mContext, messageLists);
        lv_chat_list_item_detail.setAdapter(msgAdapter);
    }

    private void updateData() {
        messageLists.clear();
        list = messageListDao.queryBuilder().where(MessageListDao.Properties.MessageId.eq(friendId)).list();
        Log.e(TAG, "list:" + list.size());
        messageLists.addAll(list);
        msgAdapter.notifyDataSetChanged();
//        lv_chat_list_item_detail.setSelection(messageLists.size());
    }

    private void getDaoData() {
        DaoSession daoSession = ((App) applicationContext).getDaoSession();
        messageListDao = daoSession.getMessageListDao();
        chatListDao = daoSession.getChatListDao();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  receiveMsg(Event.ReceiveEvent receiveEvent){

        try {
            JSONObject json = new JSONObject(receiveEvent.msg);
            String message = json.getString("message");
            Long friendId = json.getLong("friendId");

            MessageList messageList = new MessageList();
            messageList.isMyMsg = false;
            messageList.setMsg(message);
            messageList.setMessageId(friendId);
            messageListDao.insert(messageList);

            chatList = chatListDao.queryBuilder().where(ChatListDao.Properties.FriendId.eq(friendId)).build().unique();
            chatList.setMessage(message);
            chatListDao.update(chatList);
            updateData();

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
