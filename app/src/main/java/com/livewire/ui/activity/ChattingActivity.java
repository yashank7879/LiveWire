package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.livewire.R;
import com.livewire.adapter.ChattingAdapter;
import com.livewire.databinding.ActivityChattingBinding;
import com.livewire.model.Chat;
import com.livewire.model.UserInfoFcm;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity  implements View.OnClickListener{
    ActivityChattingBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private ChattingAdapter chattingAdapter;
    private ArrayList<Chat> chatList;
    private String otherprofilePic="";
    private String otherUID="";
    private UserInfoFcm otherUserInfo;
    private String myUid="";
    private Map<String, Chat> map;
    private int mPostsPerPage = 20;
    private ArrayList<String> keyList;
    ChildEventListener eventListener;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private String chatNode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_chatting);
        firebaseDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();
        keyList = new ArrayList<>();
        if (getIntent().getStringExtra("otherUID") != null) {
            otherprofilePic = getIntent().getStringExtra("profilePic");
            String titleName = getIntent().getStringExtra("titleName");
            binding.titleName.setText(titleName);
            otherUID = getIntent().getStringExtra("otherUID");
            Picasso.with(binding.headerImage.getContext()).load(otherprofilePic).fit().into(binding.headerImage);
            gettingDataFromUserTable(otherUID);
           // IsGetNotificationValue = otherUID;
             myUid = PreferenceConnector.readString(this,PreferenceConnector.MY_USER_ID,"");
        }

        chatNode = gettingNotes();
        chatList = new ArrayList<>();

        chattingAdapter = new ChattingAdapter(this, chatList, myUid);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
               // ly_popup_menu.setVisibility(View.GONE);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                   // tv_days_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findFirstVisibleItemPosition() != -1) {
                   // tv_days_status.setText(chatList.get(linearLayoutManager.findFirstVisibleItemPosition()).banner_date);
                   // tv_days_status.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = linearLayoutManager.getItemCount();
                mLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition + mPostsPerPage)) {
                    //getChat(chattingAdapter.getLastItemId());
                    mIsLoading = true;
                }

            }
        });
        binding.recyclerView.setAdapter(chattingAdapter);
        getChat();


    }


        private String gettingNotes() {
            //create note for chatroom
            int myUid_ = Integer.parseInt(myUid);
            int otherUID_ = Integer.parseInt(otherUID);

            if (myUid_ < otherUID_) {
                chatNode = myUid + "_" + otherUID;
            } else {
                chatNode = otherUID + "_" + myUid;
            }

            return chatNode;
        }


    private void gettingDataFromUserTable(final String otherUID) {
        firebaseDatabase.getReference().child(Constant.ARG_USERS).child(otherUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(UserInfoFcm.class) != null) {
                    otherUserInfo = dataSnapshot.getValue(UserInfoFcm.class);
                    binding.titleName.setText(otherUserInfo.name + "");
                    if (!otherUserInfo.profilePic.equals("")) {
                        otherprofilePic = otherUserInfo.profilePic;
                        if (getApplicationContext() != null)
                            Picasso.with(binding.headerImage.getContext()).load(otherprofilePic).fit().into(binding.headerImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).
                child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(otherUID)) {
                    //firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).child("unreadCount").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    //Count = chat.unreadCount;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getChat() {
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).orderByKey()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        getChatDataInmap(dataSnapshot.getKey(), chat);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        if (!keyList.contains(dataSnapshot.getKey())) {
                            getChatDataInmap(dataSnapshot.getKey(), chat);
                        } else {
                            updategetChatDataInmap(dataSnapshot.getKey(), chat);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //Todo create by hemant
        eventListener = firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).orderByKey()
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) tickAllOtherStatus2(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) tickAllOtherStatus2(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getChatDataInmap(String key, Chat chat) {
        if (chat != null) {
            if (chat.deleteby != null){

                if (chat.deleteby.equals(myUid)) {
                    return;
                } else {
                    chat.banner_date = getDateBanner(chat.timestamp);

                    map.put(key, chat);
                    chatList.clear();
                    Collection<Chat> values = map.values();
                    chatList.addAll(values);
                    binding.recyclerView.scrollToPosition(map.size() - 1);
                    // chattingAdapter.notifyDataSetChanged();
                }
            }

        }
        shortList();
    }

    private void tickAllOtherStatus2(String key) {
        //  if (isOnChatScreen)
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).child(key).child("isMsgReadTick").setValue(2);
    }



    private void updategetChatDataInmap(String key, Chat chat) {
        if (chat != null) {
            if (chat.deleteby.equals(myUid)) {
                return;
            } else {
                chat.banner_date = getDateBanner(chat.timestamp);
                chat.imageUrl = map.get(key).imageUrl;

                map.put(key, chat);
                chatList.clear();
                Collection<Chat> values = map.values();
                chatList.addAll(values);
                binding.recyclerView.scrollToPosition(map.size() - 1);
                // chattingAdapter.notifyDataSetChanged();
            }
        }
        shortList();
    }

    private void shortList() {
        Collections.sort(chatList, new Comparator<Chat>() {

            @Override
            public int compare(Chat a1, Chat a2) {

                if (a1.timestamp == null || a2.timestamp == null)
                    return -1;
                else {
                    Long long1 = Long.valueOf(String.valueOf(a1.timestamp));
                    Long long2 = Long.valueOf(String.valueOf(a2.timestamp));
                    return long1.compareTo(long2);
                }
            }
        });
        chattingAdapter.notifyDataSetChanged();
    }
    private String getDateBanner(Object timeStamp) {
        String banner_date = "";
        SimpleDateFormat sim = new SimpleDateFormat(" d MMMM yyyy", Locale.US);
        try {
            String date_str = sim.format(new Date((Long) timeStamp)).trim();
            String currentDate = sim.format(Calendar.getInstance().getTime()).trim();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String yesterdayDate = sim.format(calendar.getTime()).trim();

            if (date_str.equals(currentDate)) {
                banner_date = "Today";
            } else if (date_str.equals(yesterdayDate)) {
                banner_date = "Yesterday";
            } else {
                banner_date = date_str.trim();
            }

            return banner_date;
        } catch (Exception e) {
            e.printStackTrace();
            return banner_date;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //IsGetNotificationValue = "";
        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(otherUID)) {
                    firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).child("unreadCount").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).removeEventListener(eventListener);
    }


   /* private void sendMessage() {

        String pushkey = firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).push().getKey();
        String msg = binding.edMessage.getText().toString().trim();
        String firebase_id = "";
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        if (msg.equals("") && image_FirebaseURL != null) {

        } else if (msg.equals("")) {
            return;
        }

        Chat otherChat = new Chat();
        otherChat.deleteby = "";
        otherChat.firebaseId = firebase_id;
        otherChat.firebaseToken = firebaseToken;

        if (image_FirebaseURL != null) {
            otherChat.imageUrl = image_FirebaseURL.toString();
            otherChat.message = "";
            otherChat.image = 1;
        } else {
            otherChat.imageUrl = "";
            otherChat.message = msg;
            otherChat.image = 0;
        }

        otherChat.name = title_name.getText().toString();
        otherChat.profilePic = otherprofilePic;
        otherChat.timestamp = ServerValue.TIMESTAMP;
        otherChat.uid = otherUID;
        otherChat.lastMsg = myUid;
        otherChat.unreadCount = 0;


        Chat myChat = new Chat();
        myChat.deleteby = "";
        myChat.firebaseId = firebase_id;
        myChat.firebaseToken = firebaseToken;

        if (image_FirebaseURL != null) {
            myChat.imageUrl = image_FirebaseURL.toString();
            myChat.message = "";
            myChat.image = 1;
            holdKeyForImage = pushkey;
        } else {
            myChat.imageUrl = "";
            myChat.message = msg;
            myChat.image = 0;
        }

        myChat.name = myName;
        myChat.profilePic = myProfileImage;
        myChat.timestamp = ServerValue.TIMESTAMP;
        myChat.uid = myUid;
        myChat.lastMsg = myUid;
        myChat.unreadCount = ++Count;

     *//*   if (isOtherUserOnline) {
            myChat.isMsgReadTick = 1;
        } else {
            myChat.isMsgReadTick = 0;
        }
*//*

        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).child(pushkey).setValue(myChat);
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).child(pushkey).setValue(myChat);


        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).setValue(otherChat);
        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).setValue(myChat);

*//*
        if (isNotification) {
            if (image_FirebaseURL != null) {
                if (firebaseToken != null && otherUserInfo != null) {
                    sendPushNotificationToReceiver(myName, "Image", myName, myUid, firebaseToken);
                }
            } else {
                if (firebaseToken != null && otherUserInfo != null)
                    sendPushNotificationToReceiver(myName, msg, myName, myUid, firebaseToken);
            }
        }

        ed_message.setText("");
        image_FirebaseURL = null;
        loading_view.setVisibility(View.GONE);*//*
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_msg_button:
                //sendMessage();
                break;
                default:

        }
    }
}
