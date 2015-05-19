package im.actor.messenger.app.fragment.chat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import im.actor.messenger.R;
import im.actor.messenger.app.AppContext;
import im.actor.messenger.app.Intents;
import im.actor.messenger.app.base.BaseActivity;
import im.actor.messenger.app.emoji.SmileProcessor;
import im.actor.messenger.app.keyboard.KeyboardStatusListener;
import im.actor.messenger.app.keyboard.emoji.EmojiKeyboard;
import im.actor.messenger.app.util.RandomUtil;
import im.actor.messenger.app.util.Screen;
import im.actor.messenger.app.util.io.IOUtils;
import im.actor.messenger.app.view.AvatarView;
import im.actor.messenger.app.view.KeyboardHelper;
import im.actor.messenger.app.view.OnItemClickedListener;
import im.actor.messenger.app.view.TintImageView;
import im.actor.messenger.app.view.TypingDrawable;
import im.actor.model.Messenger;
import im.actor.model.entity.GroupMember;
import im.actor.model.entity.Peer;
import im.actor.model.entity.PeerType;
import im.actor.model.mvvm.ValueChangedListener;
import im.actor.model.mvvm.ValueModel;
import im.actor.model.viewmodel.GroupVM;
import im.actor.model.viewmodel.UserVM;

import static im.actor.messenger.app.Core.groups;
import static im.actor.messenger.app.Core.messenger;
import static im.actor.messenger.app.Core.users;
import static im.actor.messenger.app.emoji.SmileProcessor.emoji;

import static im.actor.messenger.app.view.ViewUtils.expandMentions;
import static im.actor.messenger.app.view.ViewUtils.goneView;


public class ChatActivity extends BaseActivity{

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_VIDEO = 2;
    private static final int REQUEST_DOC = 3;
    private static final int REQUEST_LOCATION = 4;

    private Peer peer;

    private Messenger messenger;

    private EditText messageBody;
    private TintImageView sendButton;
    private ImageButton attachButton;
    private View kicked;

    // Action bar
    private View barView;
    private AvatarView barAvatar;
    private TextView barTitle;
    private View barSubtitleContainer;
    private TextView barSubtitle;
    private View barTypingContainer;
    private ImageView barTypingIcon;
    private TextView barTyping;

    private String fileName;

    private KeyboardHelper keyboardUtils;

    private boolean isTypingDisabled = false;

    private boolean isCompose = false;
    private EmojiKeyboard emojiKeyboard;
    private boolean isMentionsVisible = false;
    //private BindedDisplayList<SearchEntity> mentionsDisplay;
    private MentionsAdapter mentionsAdapter;
    private ListView mentionsList;
    private FrameLayout mentionsContainer;
    private TextView mentionsEmptyView;
    private String mentionSearchString = "";
    private int mentionStart;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        if (saveInstance != null) {
            fileName = saveInstance.getString("pending_file_name", null);
        }

        keyboardUtils = new KeyboardHelper(this);

        peer = Peer.fromUniqueId(getIntent().getExtras().getLong(Intents.EXTRA_CHAT_PEER));

        isCompose = saveInstance == null && getIntent().getExtras().getBoolean(Intents.EXTRA_CHAT_COMPOSE, false);

        messenger = messenger();

        // Init action bar

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        // Action bar header

        barView = LayoutInflater.from(this).inflate(R.layout.bar_conversation, null);
        barTitle = (TextView) barView.findViewById(R.id.title);
        barSubtitleContainer = barView.findViewById(R.id.subtitleContainer);
        barTypingIcon = (ImageView) barView.findViewById(R.id.typingImage);
        barTypingIcon.setImageDrawable(new TypingDrawable());
        barTyping = (TextView) barView.findViewById(R.id.typing);
        barSubtitle = (TextView) barView.findViewById(R.id.subtitle);
        barTypingContainer = barView.findViewById(R.id.typingContainer);
        barTypingContainer.setVisibility(View.INVISIBLE);
        barAvatar = (AvatarView) barView.findViewById(R.id.avatarPreview);
        barAvatar.init(Screen.dp(32), 18);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setCustomView(barView, layout);
        barView.findViewById(R.id.titleContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (peer.getPeerType() == PeerType.PRIVATE) {
                    startActivity(Intents.openProfile(peer.getPeerId(), ChatActivity.this));
                } else if (peer.getPeerType() == PeerType.GROUP) {
                    startActivity(Intents.openGroup(peer.getPeerId(), ChatActivity.this));
                } else {
                    // Nothing to do
                }
            }
        });


        // Init view

        setContentView(R.layout.activity_dialog);

        getWindow().setBackgroundDrawable(null);

        if (saveInstance == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.messagesFragment, MessagesFragment.create(peer))
                    .commit();
        }

        messageBody = (EditText) findViewById(R.id.et_message);
        messageBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after > count && !isTypingDisabled) {
                    messenger.onTyping(peer);
                }
                if(isMentionsVisible && after<count && s.charAt(start) == '@'){
                    hideMentions();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                String firstPeace  = str.substring(0, start + count);


                if((count==1 && s.charAt(start) == '@') || (start>0 &&  s.charAt(start-1) == '@')) {
                    if(peer.getPeerType()==PeerType.GROUP)
                    showMentions();
                }


                if(s.length()!=count){
                    mentionStart = firstPeace.lastIndexOf("@");
                    if(firstPeace.contains("@") && mentionStart + 1 < firstPeace.length()){
                        mentionSearchString = firstPeace.substring(mentionStart + 1, firstPeace.length());
                    }else{
                        mentionSearchString = "";
                    }

                    if(mentionSearchString.equals(" ")){
                        hideMentions();
                    }else if(mentionsAdapter!=null){
                        //mentionsDisplay.initSearch(mentionSearchString, false);
                        mentionsAdapter.setQuery(mentionSearchString.trim().toLowerCase());
                    }

                }




            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendButton.setTint(getResources().getColor(R.color.conv_send_enabled));
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setTint(getResources().getColor(R.color.conv_send_disabled));
                    sendButton.setEnabled(false);
                }
            }
        });
        messageBody.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (messenger().isSendByEnterEnabled()) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER) {
                        sendMessage();
                        return true;
                    }
                }
                return false;
            }
        });
        messageBody.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                if (i == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                    return true;
                }
                if (messenger().isSendByEnterEnabled()) {
                    if (keyEvent != null && i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        sendMessage();
                        return true;
                    }
                }
                return false;
            }
        });

        kicked = findViewById(R.id.kickedFromChat);

        sendButton = (TintImageView) findViewById(R.id.ib_send);
        sendButton.setResource(R.drawable.conv_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        attachButton = (ImageButton) findViewById(R.id.ib_attach);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context wrapper = new ContextThemeWrapper(ChatActivity.this, R.style.AttachPopupTheme);
                PopupMenu popup = new PopupMenu(wrapper, findViewById(R.id.attachAnchor));

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                    .getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod(
                                    "setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.getMenuInflater().inflate(R.menu.attach_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.gallery) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/* video/*");
                            startActivityForResult(intent, REQUEST_GALLERY);
                            return true;
                        } else if (item.getItemId() == R.id.takePhoto) {
                            File externalFile = getExternalFilesDir(null);
                            if (externalFile == null) {
                                Toast.makeText(ChatActivity.this, R.string.toast_no_sdcard, Toast.LENGTH_LONG).show();
                                return true;
                            }
                            String externalPath = externalFile.getAbsolutePath();
                            new File(externalPath + "/actor/").mkdirs();

                            fileName = externalPath + "/actor/capture_" + RandomUtil.randomId() + ".jpg";
                            startActivityForResult(
                                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName))),
                                    REQUEST_PHOTO);
                        } else if (item.getItemId() == R.id.takeVideo) {

                            File externalFile = getExternalFilesDir(null);
                            if (externalFile == null) {
                                Toast.makeText(ChatActivity.this, R.string.toast_no_sdcard, Toast.LENGTH_LONG).show();
                                return true;
                            }
                            String externalPath = externalFile.getAbsolutePath();
                            new File(externalPath + "/actor/").mkdirs();

                            fileName = externalPath + "/actor/capture_" + RandomUtil.randomId() + ".mp4";

                            Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
                            startActivityForResult(i, REQUEST_VIDEO);
                            return true;
                        } else if (item.getItemId() == R.id.file) {
                            startActivityForResult(Intents.pickFile(ChatActivity.this), REQUEST_DOC);
                        } else if (item.getItemId() == R.id.location) {
                            startActivityForResult(com.droidkit.pickers.Intents.pickLocation(ChatActivity.this), REQUEST_LOCATION);
                        }
                        return false;
                    }
                });
                popup.show();

            }
        });




        final ImageView emojiButton = (ImageView) findViewById(R.id.ib_emoji);
        emojiKeyboard = new EmojiKeyboard(this);
//        emojiKeyboard.setOnStickerClickListener(new OnStickerClickListener() {
//            @Override
//            public void onStickerClick(Sticker sticker) {
//                messenger().sendPhoto(peer, getStickerProcessor().getStickerPath(sticker));
//            }
//        });




        emojiKeyboard.setKeyboardStatusListener(new KeyboardStatusListener() {


            @Override
            public void onDismiss() {
                emojiButton.setImageResource(R.drawable.ic_emoji);
            }

            @Override
            public void onShow() {
                emojiButton.setImageResource(R.drawable.ic_keyboard);
            }

        });

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboard.toggle(messageBody);
            }
        });

        // Mentions
        mentionsList = (ListView) findViewById(R.id.mentionsList);
        mentionsContainer = (FrameLayout) findViewById(R.id.mentionsContainer);
        mentionsEmptyView = (TextView) findViewById(R.id.mentionsEmpty);
        mentionsEmptyView.setVisibility(View.GONE);
        //mentionsList.setLayoutManager(new CustomLinearLayoutManager(this));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (peer.getPeerType() == PeerType.PRIVATE) {
            final UserVM user = users().get(peer.getPeerId());
            if (user == null) {
                finish();
                return;
            }

            bind(barAvatar, user.getId(), user.getAvatar(), user.getName());
            bind(barTitle, user.getName());
            bind(barSubtitle, barSubtitleContainer, user);
            bindPrivateTyping(barTyping, barTypingContainer, barSubtitle, messenger().getTyping(user.getId()));

            kicked.setVisibility(View.GONE);
        } else if (peer.getPeerType() == PeerType.GROUP) {
            GroupVM group = groups().get(peer.getPeerId());
            if (group == null) {
                finish();
                return;
            }

            bind(barAvatar, group.getId(), group.getAvatar(), group.getName());
            bind(barTitle, group.getName());
            // Subtitle is always visible for Groups
            // barSubtitleContainer.setVisibility(View.VISIBLE);
            bind(barSubtitle, barSubtitleContainer, group);
            bindGroupTyping(barTyping, barTypingContainer, barSubtitle, messenger().getGroupTyping(group.getId()));

            bind(messenger().getGroups().get(peer.getPeerId()).isMember(), new ValueChangedListener<Boolean>() {
                @Override
                public void onChanged(Boolean val, ValueModel<Boolean> valueModel) {
                    kicked.setVisibility(val ? View.GONE : View.VISIBLE);
                }
            });
        }

        if (isCompose) {
            messageBody.requestFocus();
            keyboardUtils.setImeVisibility(messageBody, true);
        }
        isCompose = false;

        isTypingDisabled = true;
        String text = messenger().loadDraft(peer);
        if (text != null) {
            messageBody.setText((emoji().processEmojiCompatMutable(text, SmileProcessor.CONFIGURATION_BUBBLES)));
        } else {
            messageBody.setText("");
        }
        isTypingDisabled = false;
    }

    private void sendMessage() {
        final String text = messageBody.getText().toString().trim();
        messageBody.setText("");
        if (text.length() == 0) {
            return;
        }

        // Hack for full screen mode
        if (getResources().getDisplayMetrics().heightPixels <=
                getResources().getDisplayMetrics().widthPixels) {
            keyboardUtils.setImeVisibility(messageBody, false);
        }

        messenger().sendMessage(peer, text);
        messenger().trackTextSend(peer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data.getData() != null) {
                    sendUri(data.getData());
                }
            } else if (requestCode == REQUEST_PHOTO) {
                messenger().sendPhoto(peer, fileName);
                messenger().trackPhotoSend(peer);
            } else if (requestCode == REQUEST_VIDEO) {
                messenger().sendVideo(peer, fileName);
                messenger().trackVideoSend(peer);
            } else if (requestCode == REQUEST_DOC) {
                if (data.getData() != null) {
                    sendUri(data.getData());
                } else if (data.hasExtra("picked")) {
                    ArrayList<String> files = data.getStringArrayListExtra("picked");
                    if (files != null) {
                        for (String s : files) {
                            messenger().sendDocument(peer, s);
                            messenger().trackDocumentSend(peer);
                        }
                    }
                }
            }
        }
    }

    private void sendUri(final Uri uri) {
        new AsyncTask<Void, Void, Void>() {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(ChatActivity.this);
                progressDialog.setMessage(getString(R.string.pick_downloading));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.MIME_TYPE,
                        MediaStore.Video.Media.TITLE};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                String mimeType = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
                String fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[2]));
                if (mimeType == null) {
                    mimeType = "?/?";
                }
                cursor.close();

                if (picturePath == null || !uri.getScheme().equals("file")) {
                    File externalFile = AppContext.getContext().getExternalFilesDir(null);
                    if (externalFile == null) {
                        return null;
                    }
                    String externalPath = externalFile.getAbsolutePath();

                    File dest = new File(externalPath + "/Actor/");
                    dest.mkdirs();

                    File outputFile = new File(dest, "upload_" + RandomUtil.randomId() + ".jpg");
                    picturePath = outputFile.getAbsolutePath();

                    try {
                        IOUtils.copy(getContentResolver().openInputStream(uri), new File(picturePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                if (fileName == null) {
                    fileName = picturePath;
                }

                if (mimeType.startsWith("video/")) {
                    messenger().sendVideo(peer, picturePath, fileName);
                    messenger().trackVideoSend(peer);
                } else if (mimeType.startsWith("image/")) {
                    messenger().sendPhoto(peer, picturePath, new File(fileName).getName());
                    messenger().trackPhotoSend(peer);
                } else {
                    messenger().sendDocument(peer, picturePath, new File(fileName).getName());
                    messenger().trackDocumentSend(peer);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.dismiss();
            }
        }.execute();
    }

    private void showMentions() {
        if (isMentionsVisible) {
            return;
        }
        isMentionsVisible = true;


        GroupVM groupInfo = groups().get(peer.getPeerId());
        mentionsAdapter = new MentionsAdapter(groupInfo.getMembers().get(), this, new OnItemClickedListener<GroupMember>(){

            @Override
            public void onClicked(GroupMember item) {
                String name = users().get(item.getUid()).getName().get();

                if(mentionStart!=-1  && mentionStart + mentionSearchString.length() <= messageBody.getText().length()){
                    messageBody.setText(messageBody.getText().replace(mentionStart, mentionStart + mentionSearchString.length() + 1, new String("@").concat(name)));
                    messageBody.setSelection(mentionStart + name.length() + 1);
                }
                hideMentions();
            }

            @Override
            public boolean onLongClicked(GroupMember item) {
                return false;
            }
        }, new MentionsAdapter.MentionsUpdatedCallback(){

            @Override
            public void onMentionsUpdated(int oldRowsCount, int newRowsCount) {
                onMentionsChanged(oldRowsCount, newRowsCount);
            }
        });



        mentionsList.setAdapter(mentionsAdapter);
        //mentionsList.setStackFromBottom(true);
        onMentionsChanged(0, mentionsAdapter.getCount());
        goneView(mentionsEmptyView, false);

    }

    private void hideMentions() {
        if (!isMentionsVisible) {
            return;
        }
        isMentionsVisible = false;

        expandMentions(mentionsList,mentionsAdapter.getCount(),0);
        mentionsAdapter = null;
        mentionsList.setAdapter(null);

        //goneView(mentionsContainer);

    }

    private void onMentionsChanged(int oldRowsCount, int newRowsCount) {
        if(mentionsAdapter!=null)

            expandMentions(mentionsList, oldRowsCount, newRowsCount);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        if (peer.getPeerType() == PeerType.PRIVATE) {
            menu.findItem(R.id.contact).setVisible(true);
        } else {
            menu.findItem(R.id.contact).setVisible(false);
        }

        if (peer.getPeerType() == PeerType.GROUP) {
            if (groups().get(peer.getPeerId()).isMember().get()) {
                menu.findItem(R.id.leaveGroup).setVisible(true);
                menu.findItem(R.id.groupInfo).setVisible(true);
            } else {
                menu.findItem(R.id.leaveGroup).setVisible(false);
                menu.findItem(R.id.groupInfo).setVisible(false);
            }

        } else {
            menu.findItem(R.id.groupInfo).setVisible(false);
            menu.findItem(R.id.leaveGroup).setVisible(false);
        }

        menu.findItem(R.id.files).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (isMentionsVisible) {
            hideMentions();
        } else if (emojiKeyboard.isShowing()) {
            emojiKeyboard.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.clear:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.alert_delete_all_messages_text)
                        .setPositiveButton(R.string.alert_delete_all_messages_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                execute(messenger().clearChat(peer));
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show()
                        .setCanceledOnTouchOutside(true);
                break;
            case R.id.leaveGroup:
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.alert_leave_group_message)
                                .replace("{0}", groups().get(peer.getPeerId()).getName().get()))
                        .setPositiveButton(R.string.alert_leave_group_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog2, int which) {
                                execute(messenger().leaveGroup(peer.getPeerId()));
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show()
                        .setCanceledOnTouchOutside(true);
                break;
            case R.id.contact:
                startActivity(Intents.openProfile(peer.getPeerId(), ChatActivity.this));
                break;
            case R.id.groupInfo:
                startActivity(Intents.openGroup(peer.getPeerId(), ChatActivity.this));
                break;
            case R.id.files:
                // startActivity(Intents.openDocs(chatType, chatId, ChatActivity.this));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (fileName != null) {
            outState.putString("pending_file_name", fileName);
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileName = savedInstanceState.getString("pending_file_name", null);
    }

    @Override
    public void onPause() {
        super.onPause();
        emojiKeyboard.destroy();
        messenger.saveDraft(peer, messageBody.getText().toString());
    }
}
