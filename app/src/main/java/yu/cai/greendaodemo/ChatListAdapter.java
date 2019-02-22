package yu.cai.greendaodemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bean.ChatList;

/**
 * Created by admin on 2017-7-7.
 */

public class ChatListAdapter extends BaseAdapter {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(Context context, List<ChatList> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.chat_list_layout, null);
            viewHold = new ViewHold(convertView);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        viewHold.friendName.setText(list.get(position).getFriendName());
        viewHold.icon.setImageResource(list.get(position).getResourseId());
        viewHold.currentTime.setText(list.get(position).getCurrentTime());
        viewHold.msg.setText(list.get(position).getMessage());
        return convertView;
    }

    class ViewHold {
        private ImageView icon;
        private TextView friendName;
        private TextView currentTime;
        private TextView msg;

        public ViewHold(View view) {
            icon = (ImageView) view.findViewById(R.id.icon);
            friendName = (TextView) view.findViewById(R.id.friendName);
            currentTime = (TextView) view.findViewById(R.id.currentTime);
            msg = (TextView) view.findViewById(R.id.msg);
        }

    }
}
