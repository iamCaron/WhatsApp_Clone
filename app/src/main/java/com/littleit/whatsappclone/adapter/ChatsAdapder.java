package com.littleit.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.Contacts;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.littleit.whatsappclone.R;
import com.littleit.whatsappclone.common.Common;
import com.littleit.whatsappclone.model.chat.Chats;
import com.littleit.whatsappclone.tools.AudioService;
import com.littleit.whatsappclone.view.activities.chats.ChatsActivity;
import com.littleit.whatsappclone.view.activities.display.ViewImageActivity;


import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsAdapder extends RecyclerView.Adapter<ChatsAdapder.ViewHolder> {
    private List<Chats> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;
    private ImageButton tmpBtnPlay;
    private AudioService audioService;
    private ChatsActivity chatsActivity;



    public ChatsAdapder(List<Chats> list, Context context,ChatsActivity activity) {
        chatsActivity=activity;
        this.list = list;
        this.context = context;
        this.audioService = new AudioService(context);
    }

    public void setList(List<Chats> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(list.get(position));
        holder.imageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to open the image in viewImage activity
                holder.imageMessage.invalidate();
                Drawable dr = holder.imageMessage.getDrawable();
                Common.IMAGE_BITMAP = ((GlideBitmapDrawable)dr.getCurrent()).getBitmap();
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("senderId",list.get(position).getSender());
                context.startActivity(intent);

            }
        });
        holder.imageMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               updateHashSet(holder,position);
                return true;
            }
        });
        holder.textMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               updateHashSet(holder, position);
                return true;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               updateHashSet(holder, position);
                return true;
            }
        });

        if(Common.deleteMessageSet.contains(list.get(position).getId())){
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_item));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        private TextView timestampTextView,timestampImageView;
        private LinearLayout layoutText, layoutVoice;
        private RelativeLayout  layoutImage;
        private ImageView imageMessage;
        private ImageButton btnPlay;
        private ViewHolder tmpHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.tv_text_message);
            layoutImage = itemView.findViewById(R.id.layout_image);
            layoutText = itemView.findViewById(R.id.layout_text);
            imageMessage = itemView.findViewById(R.id.image_chat);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            btnPlay = itemView.findViewById(R.id.btn_play_chat);
            timestampImageView=itemView.findViewById(R.id.timestamp_image_view);
            timestampTextView=itemView.findViewById(R.id.timestamp_text);
        }
        void bind(final Chats chats){
            //Check chat type..

            switch (chats.getType()){
                case "TEXT" :
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);

                    textMessage.setText(chats.getTextMessage());
                    timestampTextView.setText(chats.getDateTime());
                    break;
                case "IMAGE" :
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutVoice.setVisibility(View.GONE);
                    timestampImageView.setText(chats.getDateTime());
                    Glide.with(context).load(chats.getUrl()).into(imageMessage);
                    break;
                case "VOICE" :
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.VISIBLE);

                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnPlay!=null){
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            }

                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                }
                            });

                            tmpBtnPlay = btnPlay;

                        }
                    });

                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else
        {
            return MSG_TYPE_LEFT;
        }
    }

    private void updateHashSet( final ViewHolder holder, final int position){


        if(Common.deleteMessageSet==null){
            Common.deleteMessageSet=new HashSet<String>();
        }
        if(Common.deleteMessageSet.contains(list.get(position).getId())){
           Common.deleteMessageSet.remove(list.get(position).getId());
            holder.itemView.setBackgroundColor(View.INVISIBLE);

            chatsActivity.updateMenu();

        }else {
           Common.deleteMessageSet.add(list.get(position).getId());
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_item));
         chatsActivity.updateMenu();
        }


    }
}
