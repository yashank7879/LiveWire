package com.livewire.ui.fragments;

import android.content.Context;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livewire.R;
import com.livewire.adapter.ChatHistoryAdapter;
import com.livewire.databinding.FragmentChatClientBinding;
import com.livewire.databinding.FragmentChatWorkerBinding;
import com.livewire.model.Chat;
import com.livewire.model.UserInfoFcm;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ChatWorkerFragment extends Fragment {
    FragmentChatWorkerBinding binding;
    private ChatHistoryAdapter adapter;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String myUid="";
    private ArrayList<Chat> historyList;
    private ArrayList<UserInfoFcm> userList;
    private Map<String, Chat> mapList;

    public ChatWorkerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_chat_worker, container, false);

        return binding.getRoot();
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_chat_worker, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyList = new ArrayList<>();
        userList = new ArrayList<>();
        mapList = new HashMap<>();

        adapter = new ChatHistoryAdapter(mContext,historyList);
        binding.recyclerView.setAdapter(adapter);
        progressDialog = new ProgressDialog(mContext);
        myUid = PreferenceConnector.readString(mContext,PreferenceConnector.MY_USER_ID,"");

        binding.tvNoData.setVisibility(View.VISIBLE);
        if (Constant.isNetworkAvailable(mContext, binding.chatLayout)) {
            getChatHistoryList();
        } else Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onResume() {
        super.onResume();
        if (Constant.isNetworkAvailable(mContext, binding.chatLayout)) {
            getChatHistoryList();
        } else Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show();

    }

    private void getChatHistoryList() {
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(Chat.class) != null){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    gettingDataFromUserTable(dataSnapshot.getKey(), chat);

                    binding.tvNoData.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(Chat.class) != null){

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    gettingDataFromUserTable(dataSnapshot.getKey(), chat);

                    binding.tvNoData.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for(int i = 0 ; i<historyList.size();i++){
                    if(historyList.get(i).uid.equals(dataSnapshot.getKey())){
                        historyList.remove(i);
                    }
                }


               /* for(Chat chat:historyList){
                    if(chat.uid.equals(dataSnapshot.getKey())){
                        historyList.remove(chat);
                    }
                }*/

                if(historyList.size() == 0){
                    binding.tvNoData.setVisibility(View.VISIBLE);
                }else{
                    binding.tvNoData.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void gettingDataFromUserTable(final String key, final Chat chat) {
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(key).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(UserInfoFcm.class) != null){
                            UserInfoFcm infoFCM = dataSnapshot.getValue(UserInfoFcm.class);
                            userList.add(infoFCM);

                            for (UserInfoFcm userInfoFCM : userList) {
                                if (userInfoFCM.uid.equals(key)) {

                                    chat.profilePic = userInfoFCM.profilePic;
                                    chat.name = userInfoFCM.name;
                                    chat.firebaseToken = userInfoFCM.firebaseToken;
                                    chat.uid = key;
                                }
                            }
                            mapList.put(chat.uid, chat);
                            historyList.clear();
                            Collection<Chat> values = mapList.values();
                            historyList.addAll(values);
                            shortList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void shortList() {
        Collections.sort(historyList,new Comparator<Chat>(){

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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

}
