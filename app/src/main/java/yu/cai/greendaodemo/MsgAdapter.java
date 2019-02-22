package yu.cai.greendaodemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bean.MessageList;

/**
 * Created by admin on 2017-7-17.
 */

public class MsgAdapter extends BaseAdapter {

    private Context context;
    private List<MessageList> messageLists;

    public MsgAdapter(Context context, List<MessageList> messageLists) {
        this.context = context;
        this.messageLists = messageLists;
    }

    @Override
    public int getCount() {
        return messageLists.size();
    }

    @Override
    public Object getItem(int position) {
        return messageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        if (messageLists.get(position).isMyMsg) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftViewHolder leftViewHolder = null;
        RightViewHolder rightViewHolder = null;

        switch (getItemViewType(position)) {

            case 1:
                if (convertView == null) {
                    convertView = View.inflate(context,R.layout.message_left_layout,null);
                    leftViewHolder = new LeftViewHolder(convertView);
                    convertView.setTag(leftViewHolder);
                } else {
                    leftViewHolder = (LeftViewHolder) convertView.getTag();
                }

                leftViewHolder.leftText.setText(messageLists.get(position).getMsg());

                break;

            case 0:
                if (convertView == null) {
                    convertView = View.inflate(context,R.layout.message_right_layout,null);
                    rightViewHolder = new RightViewHolder(convertView);
                    convertView.setTag(rightViewHolder);
                } else {
                    rightViewHolder = (RightViewHolder) convertView.getTag();
                }

                rightViewHolder.textRight.setText(messageLists.get(position).getMsg());
                break;

        }

        return convertView;
    }

    class LeftViewHolder{
        TextView leftText;

        public  LeftViewHolder(View view){
            leftText = (TextView) view.findViewById(R.id.textLeft);
        }
    }

    class RightViewHolder{
        TextView textRight;

        public  RightViewHolder(View view){
            textRight = (TextView) view.findViewById(R.id.textRight);
        }
    }
}
