package PMessageActivity;

import android.widget.BaseAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.virtus.transmitter.R;

import java.util.ArrayList;

public final class CustomeAdapter extends BaseAdapter {

    private static final String TEG_CustomeAdapter = "TEG_CustomeAdapter";
    private LayoutInflater layoutInflater;
    private ArrayList<NewItem> listItem;

    public CustomeAdapter(Context context, ArrayList<NewItem> listItem){
        this.layoutInflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null){
            view = layoutInflater.inflate(R.layout.item_message_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvLoginMessage);
            viewHolder.tvMessage = (TextView) view.findViewById(R.id.tvMessMessage);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tvDateMessage);
            viewHolder.ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = listItem.get(i).getName();
        String message = listItem.get(i).getMessage();
        String time = listItem.get(i).getTime();

        viewHolder.tvName.setText(name);
        viewHolder.tvMessage.setText(message);
        viewHolder.tvTime.setText(time);
        viewHolder.ivPhoto.setImageResource(R.drawable.a);

        Log.d(TEG_CustomeAdapter, name + " "  +  message + " " + time);

        return view;
    }

    static class ViewHolder{

        TextView tvName;
        TextView tvTime;
        TextView tvMessage;
        ImageView ivPhoto;
    }
}
