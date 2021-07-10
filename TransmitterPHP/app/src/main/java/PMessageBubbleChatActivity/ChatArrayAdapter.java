package PMessageBubbleChatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.virtus.transmitter.R;

import java.util.ArrayList;
import java.util.List;

// не работает (на стадии реализации)
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage>{

    private TextView chatText;
    private ArrayList<ChatMessage> chatMessageList = new ArrayList<>();
    private LinearLayout singleMessageContainer;

    public void setChatMessageList(ArrayList<ChatMessage> chatMessageList){
        this.chatMessageList = chatMessageList;
    }

    // добавление в список новый объект ChatMessage
    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }


    public void clearAdapter(){
        chatMessageList.clear();
    }

    // 1 контекст в котором должен работать адаптер 2 код слоя ячейки списка
    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    // получение всех элементов списка
    public int getCount() {
        return this.chatMessageList.size();
    }

    // получение ChatMessage по его индексу
    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    // получение объекта View из списка List<ChatMessage> chatMessageList
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        // получение LinearLayout от activityChatSingleMessage.xml
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        // получение объекта по его позиции
        ChatMessage chatMessageObj = getItem(position);
        // получение объекта и TextView из LinearLayout (singleMessage)
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        // установка в него текста
        //  chatText.setText(chatMessageObj.message);
        chatText.setText(chatMessageObj.getMessage());
        // установка заднего фона для текста
        // chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        chatText.setBackgroundResource(chatMessageObj.positionBubbleMess() ? R.drawable.rr : R.drawable.ll);
        // установить расположение в LinearLayout слева или справа
        // singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        singleMessageContainer.setGravity(chatMessageObj.positionBubbleMess() ? Gravity.RIGHT : Gravity.LEFT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
