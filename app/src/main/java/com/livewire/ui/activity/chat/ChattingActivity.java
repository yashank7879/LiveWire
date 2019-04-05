package com.livewire.ui.activity.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.livewire.R;
import com.livewire.adapter.ChattingAdapter;
import com.livewire.databinding.ActivityChattingBinding;
import com.livewire.model.Chat;
import com.livewire.model.UserInfoFcm;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.PreferenceConnector;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.UUID;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.Constant.MY_UID;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChattingActivity.class.getName();
    ActivityChattingBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private ChattingAdapter chattingAdapter;
    private ArrayList<Chat> chatList;
    private String otherprofilePic = "";
    private String otherUID = "";
    private UserInfoFcm otherUserInfo;
    private String myUid = "";
    private Map<String, Chat> map;
    private int mPostsPerPage = 20;
    private ArrayList<String> keyList;
    ChildEventListener eventListener;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition;
    private boolean mIsLoading = false;
    private String chatNode = "";
    private int Count = 0;
    private String myName = "";
    private String myProfileImage = "";
    private Uri tmpUri;
    private String holdKeyForImage = "";
    private Uri image_FirebaseURL;
    private boolean isImageLoaded = false;
    private String blockedId = "";
    private boolean isNotification = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting);
        firebaseDatabase = FirebaseDatabase.getInstance();
        map = new HashMap<>();
        keyList = new ArrayList<>();
        Bundle extra = getIntent().getExtras();
        if (getIntent().getStringExtra("otherUID") != null) {
            otherprofilePic = getIntent().getStringExtra("profilePic");
            String titleName = getIntent().getStringExtra("titleName");
            binding.titleName.setText(titleName);

            otherUID = getIntent().getStringExtra("otherUID");
            MY_UID  = getIntent().getStringExtra("otherUID");
            if (!otherprofilePic.isEmpty()) {
                Picasso.with(binding.headerImage.getContext()).load(otherprofilePic)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user).fit().into(binding.headerImage);
            }
            gettingDataFromUserTable(otherUID);
            // IsGetNotificationValue = otherUID;
        }
        final TextView tv_days_status = findViewById(R.id.tv_days_status);
        myUid = PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, "");
        myName = PreferenceConnector.readString(this, PreferenceConnector.Name, "");
        myProfileImage = PreferenceConnector.readString(this, PreferenceConnector.PROFILE_IMG, "");

        chatNode = gettingNotes();
        chatList = new ArrayList<>();

        chattingAdapter = new ChattingAdapter(this, chatList, myUid, new ChattingAdapter.GetDateStatus() {
            @Override
            public void currentDateStatus(Object timestamp) {

            }
        }, true);



        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.sendMsgButton.setOnClickListener(this);
        binding.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // ly_popup_menu.setVisibility(View.GONE);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    tv_days_status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findFirstVisibleItemPosition() != -1) {

                    tv_days_status.setText(chatList.get(linearLayoutManager.findFirstVisibleItemPosition()).banner_date);
                    tv_days_status.setVisibility(View.VISIBLE);
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


        binding.ivBack.setOnClickListener(this);
        binding.ivPopupMenu.setOnClickListener(this);
        binding.cameraBtn.setOnClickListener(this);
        binding.btnBlockUser.setOnClickListener(this);
        binding.lyDeleteChat.setOnClickListener(this);

        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    Count = chat.unreadCount;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        getChat();
        getBlockUserData();
        isNotification();

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
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //Log.d(TAG,ds.toString());
                    }
                    //Chat chat = dataSnapshot.getValue(Chat.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).addChildEventListener(eventListener);
    }

    private void getChatDataInmap(String key, Chat chat) {
        if (chat != null) {
            if (chat.deleteby != null) {

                if (chat.deleteby.equals(myUid)) {
                    return;
                } else {
                    chat.banner_date = getDateBanner(chat.timestamp);

                    map.put(key, chat);
                    chatList.clear();
                    Collection<Chat> values = map.values();
                    chatList.addAll(values);
                    binding.recyclerView.scrollToPosition(map.size() - 1);
                    chattingAdapter.notifyDataSetChanged();
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
        Constant.MY_UID="";


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


    private void sendMessage() {

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

        otherChat.name = binding.titleName.getText().toString();
        otherChat.profilePic = otherprofilePic;
        otherChat.timestamp = ServerValue.TIMESTAMP;
        otherChat.uid = otherUID;
        otherChat.lastMsg = myUid;
        otherChat.unreadCount = 0;
        otherChat.type = "user";


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
        myChat.type = "user";
        myChat.unreadCount = ++Count;

      /*  if (isOtherUserOnline) {
            myChat.isMsgReadTick = 1;
        } else {
            myChat.isMsgReadTick = 0;
        }*/


        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).child(pushkey).setValue(myChat);
        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).child(pushkey).setValue(myChat);

        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).setValue(otherChat);
        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).setValue(myChat);


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

        binding.edMessage.setText("");
        image_FirebaseURL = null;
//        loading_view.setVisibility(View.GONE);
    }


    private void sendPushNotificationToReceiver(String title, String message, String username, String uid, String firebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(title)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(otherUserInfo.firebaseToken).send();
    }


    private void isNotification() {
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(otherUID).child(Constant.isNotification).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) != null) {
                    String isNotificationValue = dataSnapshot.getValue(String.class);
                    assert isNotificationValue != null;
                    if (isNotificationValue.equals("1")) {
                        isNotification = true;
                    } else {
                        isNotification = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //"""""" open image dialog """"""""""""//
    private void showSetProfileImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);

        builder.setTitle("Add Photo");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //***** Ensure that there's a camera activity to handle the intent ****//
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        //** Create the File where the photo should go *****//
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        if (photoFile != null) {
                            tmpUri = FileProvider.getUriForFile(ChattingActivity.this,
                                    getApplicationContext().getPackageName() + ".fileprovider",
                                    getTemporalFile(ChattingActivity.this));
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
                            startActivityForResult(takePictureIntent, Constant.CAMERA);
                        }
                    }

                } else if (options[which].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.GALLERY);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            //isCamera = false;
            tmpUri = data.getData();
            if (tmpUri != null) { // it will go to the CropImageActivity
                creatFirebaseProfilePicUrl(tmpUri);
                image_FirebaseURL = tmpUri;

                isImageLoaded = true;
                binding.sendMsgButton.callOnClick();
            }
        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                // isCamera = true;
                Bitmap bm = null;
                File imageFile = getTemporalFile(this);
                Uri photoURI = Uri.fromFile(imageFile);

                bm = Constant.getImageResized(this, photoURI); ///Image resizer
                int rotation = ImageRotator.getRotation(this, photoURI, true);
                bm = ImageRotator.rotate(bm, rotation);

                File profileImagefile = new File(this.getExternalCacheDir(), UUID.randomUUID() + ".jpg");
                tmpUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", profileImagefile);

                try {
                    OutputStream outStream = null;
                    outStream = new FileOutputStream(profileImagefile);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                if (tmpUri != null) { // it will go to the CropImageActivity
                    creatFirebaseProfilePicUrl(tmpUri);
                    image_FirebaseURL = tmpUri;

                    isImageLoaded = true;
                    binding.sendMsgButton.callOnClick();
                }
            }
        }
    }

    private void creatFirebaseProfilePicUrl(Uri selectedImageUri) {
        StorageReference storageRef;
        FirebaseStorage storage;
        FirebaseApp app;

        app = FirebaseApp.getInstance();
        assert app != null;
        storage = FirebaseStorage.getInstance(app);

        if (BASE_URL.equals("https://livewire.work/apiv2/")){
            storageRef = storage.getReference("chat_photos_LiveWire");

        }else {
            storageRef = storage.getReference("chat_photos_LiveWire_Dev");
        }

        StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        /*image_FirebaseURL = uri;
                        send_msg_button.callOnClick();*/
                        //holdKeyForImage

                        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).child(holdKeyForImage).child("imageUrl").setValue(uri.toString());
                        firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(otherUID).child(myUid).child(holdKeyForImage).child("imageUrl").setValue(uri.toString());

                        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).child("imageUrl").setValue(uri.toString());
                        firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(otherUID).child(myUid).child("imageUrl").setValue(uri.toString());

                        keyList.add(holdKeyForImage);
                        holdKeyForImage = "";
                    }
                });

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("sasa", e + "");
            }
        });

    }

    //***********Create an image file name*******************//
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir     /* directory */
        );
        return image;
    }

    //"""""" temp file genrate"""""""//
    private File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "tmp.jpg");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_msg_button:
                binding.lyPopupMenu.setVisibility(View.GONE);
                if (blockedId.equals(myUid)) {
                    binding.edMessage.setText("");
                    Constant.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals(otherUID)) {
                    binding.edMessage.setText("");
                    Constant.openAlertDialog(this, "You are blocked by " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals("Both")) {
                    binding.edMessage.setText("");
                    Constant.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else {
                    sendMessage();
                }
                break;
            case R.id.iv_back:
                MY_UID = "";
                onBackPressed();
                break;
            case R.id.iv_popup_menu: {
                if (binding.lyPopupMenu.getVisibility() == View.VISIBLE) {
                    binding.lyPopupMenu.setVisibility(View.GONE);
                } else {
                    binding.lyPopupMenu.setVisibility(View.VISIBLE);
                }
            }
            break;
            case R.id.camera_btn: {
                if (blockedId.equals(myUid)) {
                    Constant.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals(otherUID)) {
                    Constant.openAlertDialog(this, "You are blocked by " + otherUserInfo.name + ". Can't send any message.");
                } else if (blockedId.equals("Both")) {
                    Constant.openAlertDialog(this, "You block " + otherUserInfo.name + ". Can't send any message.");
                } else {
                    showSetProfileImageDialog();
                }
            }
            break;

            case R.id.btn_block_user: {

                if (blockedId.equals("")) {
                    blockChatDialog("Are you want to block ");
                } else if (blockedId.equals(myUid)) {
                    blockChatDialog("Are you want to unblock ");
                } else if (blockedId.equals(otherUID)) {
                    blockChatDialog("Are you want to block ");
                } else if (blockedId.equals("Both")) {
                    blockChatDialog("Are you want to unblock ");
                }
                binding.lyPopupMenu.setVisibility(View.GONE);
                break;
            }
            case R.id.ly_delete_chat: {
                binding.lyPopupMenu.setVisibility(View.GONE);
                deleteChatDialog("Are you sure you want to delete conversation with");
                break;
            }

            default:

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.hideSoftKeyBoard(this,binding.edMessage);
        MY_UID = "";
    }

    private void getBlockUserData() {
        firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class) != null) {
                            blockedId = dataSnapshot.getValue(String.class);

                            if (blockedId.equals("Both")) {
                                binding.tvBlockMsg.setText("Unblock user");
                            } else if (blockedId.equals("")) {
                                binding.tvBlockMsg.setText("Block user");
                            } else if (blockedId.equals(otherUID)) {
                                binding.tvBlockMsg.setText("Block user");
                            } else if (blockedId.equals(myUid)) {
                                binding.tvBlockMsg.setText("Unblock user");
                            }

                        } else {
                            blockedId = "";
                            binding.tvBlockMsg.setText("Block user");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void blockChatDialog(String msg) {
        final Dialog _dialog = new Dialog(ChattingActivity.this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_dialog_txt.setText(msg);
        tv_name.setText(otherUserInfo.name + " ?");
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockedId.equals("Both")) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(otherUID);
                } else if (blockedId.equals("")) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(myUid);
                } else if (blockedId.equals(otherUID)) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue("Both");
                } else if (blockedId.equals(myUid)) {
                    firebaseDatabase.getReference().child(Constant.BlockUsers).child(chatNode).child(Constant.blockedBy).setValue(null);
                }
                _dialog.dismiss();
            }
        });

        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialog.dismiss();
            }
        });
        _dialog.show();
    }

    private void deleteChatDialog(String msg) {
        final Dialog _dialog = new Dialog(ChattingActivity.this);
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        _dialog.setContentView(R.layout.unfriend_dialog_layout);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        TextView tv_name = _dialog.findViewById(R.id.tv_name);
        TextView tv_yes = _dialog.findViewById(R.id.tv_yes);
        TextView tv_dialog_txt = _dialog.findViewById(R.id.tv_dialog_txt);
        ImageView iv_closeDialog = _dialog.findViewById(R.id.iv_closeDialog);

        tv_dialog_txt.setText(msg);
        tv_name.setText(otherUserInfo.name+" ?");
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase.getReference().child(Constant.ARG_HISTORY).child(myUid).child(otherUID).setValue(null);
                firebaseDatabase.getReference().child(Constant.ARG_CHAT_ROOMS).child(myUid).child(otherUID).setValue(null);

                map.clear();
                chatList.clear();
                chattingAdapter.notifyDataSetChanged();
                _dialog.dismiss();
            }
        });

        iv_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dialog.dismiss();
            }
        });
        _dialog.show();
    }
}

/*

DataSnapshot { key = 1,
        value =
        {2={image=1, uid=2,
        firebaseToken=cm27WPZOfHI:APA91bFLlXcZZQ3DaNACxJSZbRY04FOwUJxMffTKje9-_zOHhuGexmJDClq7gmVZIjyZHOhErTWX4A9lynDyADuNLNh1oR-YaaBPZNLaH0lXdilPSqPbWH9VCRstipSYMTAKDWLIABRh,
        firebaseId=, imageUrl=,
        profilePic=https://livewire.work/./uploads/profile/thumb/gPwJVabWFyAu7eMN.jpeg,
// name=Purva Choudhary, deleteby=, unreadCount=0, lastMsg=1, message=, timestamp=1554184819261},
// 3={image=0, uid=3,
// firebaseToken=easDOGG1Dqw:APA91bEm5EBqTBwHKxHPHHVyhBrl7tQoivjBRv8QEoZM_CoiKva0t_hn4l0ayTMXYxO_lUEVcBVZBDSu_S_sfVE1UToyBC_q-ElUUyXJ_f_R9qQg4uMUVwxO4KCTJKpuj_i-xKZVYxRe,
// firebaseId=, imageUrl=,
// profilePic=http://graph.facebook.com/2228493370494816/picture?width=500&height=500,
// name=Arvind Patidar, deleteby=, unreadCount=0, lastMsg=1,
// message=Hello just a, timestamp=1554183224154}} }*/
