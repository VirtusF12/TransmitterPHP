package PMessageGroupActivity;


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

public final class CustomeAdapterGroup extends BaseAdapter{

    private static final String TEG_CustomeAdapterGroup = "TEG_CustomeAdapterGroup";
    private LayoutInflater layoutInflater;
    private ArrayList<NewItemGroup> listItemGroup;

    public CustomeAdapterGroup(Context context, ArrayList<NewItemGroup> newItemGroups){
        this.layoutInflater = LayoutInflater.from(context);
        this.listItemGroup = newItemGroups;
    }

    @Override
    public int getCount() {
        return listItemGroup.size();
    }

    @Override
    public Object getItem(int i) {
        return listItemGroup.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null){
            view = layoutInflater.inflate(R.layout.item_group_message_activity, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvLoginUserG);
            viewHolder.tvMessage = (TextView) view.findViewById(R.id.tvMessG);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.tvDateG);
            viewHolder.ivPhoto = (ImageView) view.findViewById(R.id.imViewUser);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = listItemGroup.get(i).getName();
        String message = listItemGroup.get(i).getMessage();
        String time = listItemGroup.get(i).getTime();

        viewHolder.tvName.setText(name);
        viewHolder.tvMessage.setText(message);
        viewHolder.tvTime.setText(time);
        viewHolder.ivPhoto.setImageResource(R.drawable.a);

        Log.d(TEG_CustomeAdapterGroup, name + " "  +  message + " " + time);

        return view;
    }

    static class ViewHolder{

        TextView tvName;
        TextView tvTime;
        TextView tvMessage;
        ImageView ivPhoto;
    }
}
