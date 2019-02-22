package yu.cai.greendaodemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bean.Book;

/**
 * Created by admin on 2017-6-27.
 */

public class MyAdapter extends BaseAdapter{
    private Context context;
    private List<Book> list;


    public MyAdapter(List<Book> list, Context context){
        this.list = list;
        this.context = context;
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
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.list_item_layout,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
         Book book = list.get(position);
        viewHolder.name.setText(book.getName());
        viewHolder.num.setText(book.getId()+"");
        viewHolder.price.setText(book.getPrice()+"");
        viewHolder.time.setText(book.getAddress());
        return convertView;
    }

     class ViewHolder{
        TextView num,price,name,time;

       public ViewHolder(View view){
           num = (TextView) view.findViewById(R.id.num);
           name = (TextView) view.findViewById(R.id.name);
           price = (TextView)view. findViewById(R.id.price);
           time = (TextView)view. findViewById(R.id.time);
       }
    }
}
