package com.gs.open.ui.presenter;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gs.factory.common.data.DataSource;
import com.gs.factory.data.helper.MessageHelper;
import com.gs.factory.data.message.MessageGroupRepository;
import com.gs.factory.data.message.MessageRepository;
import com.gs.factory.model.api.message.MsgCreateModel;
import com.gs.factory.persistence.Account;
import com.gs.open.temp.Conversation;
import com.gs.open.temp.Message;
import com.gs.open.temp.MessageContent;
import com.gs.open.temp.TextMessage;
import com.gs.open.ui.adapter.SessionAdapter;
import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.IAudioPlayListener;
import com.gs.open.R;
import com.gs.open.db.DBManager;
import com.gs.open.db.model.GroupMember;
import com.gs.open.model.cache.UserCache;
import com.gs.open.model.data.LocationData;
import com.gs.open.ui.activity.SessionActivity;
import com.gs.open.ui.activity.ShowBigImageActivity;
import com.gs.open.ui.base.BaseFragmentActivity;
import com.gs.open.ui.base.BaseFragmentPresenter;
import com.gs.open.ui.view.ISessionAtView;
import com.gs.open.util.FileOpenUtils;
import com.gs.open.util.LogUtils;
import com.gs.open.util.MediaFileUtils;

import com.gs.open.util.UIUtils;
import com.gs.open.widget.CustomDialog;


import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SessionAtPresenter extends BaseFragmentPresenter<ISessionAtView> {

    public Conversation.ConversationType mConversationType;
    private String mSessionId;  // 接受用户的ID，可能是个人，也可能是组
    private String mPushCotent = "";//接收方离线时需要显示的push消息内容。
    private String mPushData = "";//接收方离线时需要在push消息中携带的非显示内容。
    private int mMessageCount = 5;//一次获取历史消息的最大数量

    private List<Message> mData = new ArrayList<>();   // 存放消息的。
    private SessionAdapter mAdapter;
    private CustomDialog mSessionMenuDialog;
    private MessageRepository messageRepository; // gs-add
    private MessageGroupRepository messageGroupRepository;  //群

    public SessionAtPresenter(BaseFragmentActivity context, String sessionId, Conversation.ConversationType conversationType) {
        super(context);
        mSessionId = sessionId;
        mConversationType = conversationType;
        //数据库的监听操作。
        if(mConversationType  == Conversation.ConversationType.PRIVATE){
            messageRepository = new MessageRepository(sessionId);
        }
        else{
            messageGroupRepository = new MessageGroupRepository(sessionId);
        }
    }

    // 初始化，导入message。
    public void loadMessage() {
        loadData();
        setAdapter();
    }

    private void loadData() {
        getLocalHistoryMessage();
        setAdapter();
    }

    public void loadMore() {
        getLocalHistoryMessage();
        mAdapter.notifyDataSetChangedWrapper();
    }

    public void receiveNewMessage(Message message) {
        mData.add(message);
        setAdapter();
//        RongIMClient.getInstance().clearMessagesUnreadStatus(mConversationType, mSessionId);
    }

    // 恢复草稿。
    public void resetDraft() {
//        Observable.just(RongIMClient.getInstance().getTextMessageDraft(mConversationType, mSessionId))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> {
//                    if (!TextUtils.isEmpty(s)) {
//                        getView().getEtContent().setText(s);
//                        RongIMClient.getInstance().clearTextMessageDraft(mConversationType, mSessionId);
//                    }
//                }, this::loadError);
    }

    // 保存草稿，比如输入的文字还没来得及发送，将其保存
    public void saveDraft() {
        String draft = getView().getEtContent().getText().toString();
        if (!TextUtils.isEmpty(draft)) {
//            RongIMClient.getInstance().saveTextMessageDraft(mConversationType, mSessionId, draft);
        }
    }

    public void setAdapter() {
        if (mAdapter == null) {
//            LinearLayoutManager linearLayout = new LinearLayoutManager(mContext);
//            getView().getRvMsg().setLayoutManager(linearLayout);
            mAdapter = new SessionAdapter(mContext, mData, this);
//            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
//                Message message = mData.get(position);
//                MessageContent content = message.getContent();
//                if (content instanceof ImageMessage) {
//                    ImageMessage imageMessage = (ImageMessage) content;
//                    Intent intent = new Intent(mContext, ShowBigImageActivity.class);
//                    intent.putExtra("url", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri().toString() : imageMessage.getLocalUri().toString());
//                    mContext.jumpToActivity(intent);
//                } else if (content instanceof FileMessage) {
//                    FileMessage fileMessage = (FileMessage) content;
//                    if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
//                        helper.getView(R.id.bivPic).setOnClickListener(v -> {
//                            boolean isSend = message.getMessageDirection() == Message.MessageDirection.SEND ? true : false;
//                            if (isSend) {
//                                if (fileMessage.getLocalPath() != null && new File(fileMessage.getLocalPath().getPath()).exists()) {
//                                    FileOpenUtils.openFile(mContext, fileMessage.getLocalPath().getPath());
//                                } else {
//                                    downloadMediaMessage(message);
//                                }
//                            } else {
//                                Message.ReceivedStatus receivedStatus = message.getReceivedStatus();
//                                if (receivedStatus.isDownload() || receivedStatus.isRetrieved()) {
//                                    if (fileMessage.getLocalPath() != null) {
//                                        FileOpenUtils.openFile(mContext, fileMessage.getLocalPath().getPath());
//                                    } else {
//                                        UIUtils.showToast(UIUtils.getString(R.string.file_out_of_date));
//                                    }
//                                } else {
//                                    downloadMediaMessage(message);
//                                }
//                            }
//                        });
//                    }
//                } else if (content instanceof VoiceMessage) {
//                    VoiceMessage voiceMessage = (VoiceMessage) content;
//                    final ImageView ivAudio = helper.getView(R.id.ivAudio);
//                    AudioPlayManager.getInstance().startPlay(mContext, voiceMessage.getUri(), new IAudioPlayListener() {
//                        @Override
//                        public void onStart(Uri var1) {
//                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
//                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
//                                animation.start();
//                            }
//                        }
//
//                        @Override
//                        public void onStop(Uri var1) {
//                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
//                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
//                                animation.stop();
//                                animation.selectDrawable(0);
//                            }
//
//                        }
//
//                        @Override
//                        public void onComplete(Uri var1) {
//                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
//                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
//                                animation.stop();
//                                animation.selectDrawable(0);
//                            }
//                        }
//                    });
//                } else if (content instanceof RedPacketMessage) {
//                    RedPacketMessage redPacketMessage = (RedPacketMessage) content;
//                    int chatType = mConversationType == Conversation.ConversationType.PRIVATE ? RPConstant.RP_ITEM_TYPE_SINGLE : RPConstant.RP_ITEM_TYPE_GROUP;
//                    String redPacketId = redPacketMessage.getBribery_ID();
//                    String redPacketType = redPacketMessage.getBribery_Message();
//                    String receiverId = UserCache.getId();
//                    String direct = RPConstant.MESSAGE_DIRECT_RECEIVE;
//                    RedPacketUtil.openRedPacket(((SessionActivity) mContext), chatType, redPacketId, redPacketType, receiverId, direct);
//                }
//            });
            getView().getRvMsg().setAdapter(mAdapter);
//            mAdapter.setOnItemLongClickListener((helper, viewGroup, view, position) -> {
//                View sessionMenuView = View.inflate(mContext, R.layout.dialog_session_menu, null);
//                mSessionMenuDialog = new CustomDialog(mContext, sessionMenuView, R.style.MyDialog);
//                TextView tvReCall = (TextView) sessionMenuView.findViewById(R.id.tvReCall);
//                TextView tvDelete = (TextView) sessionMenuView.findViewById(R.id.tvDelete);
//
//                //根据消息类型控制显隐
//                Message message = mData.get(position);
//                MessageContent content = message.getContent();
//                if (content instanceof GroupNotificationMessage || content instanceof RecallNotificationMessage) {
//                    return false;
//                }
//                if (content instanceof RedPacketMessage || !message.getSenderUserId().equalsIgnoreCase(UserCache.getId())) {
//                    tvReCall.setVisibility(View.GONE);
//                }
//
////                tvReCall.setOnClickListener(v -> RongIMClient.getInstance().recallMessage(message, "", new RongIMClient.ResultCallback<RecallNotificationMessage>() {
////                    @Override
////                    public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
////                        UIUtils.postTaskSafely(() -> {
////                            recallMessageAndInsertMessage(recallNotificationMessage, position);
////                            mSessionMenuDialog.dismiss();
////                            mSessionMenuDialog = null;
////                            UIUtils.showToast(UIUtils.getString(R.string.recall_success));
////                        });
////                    }
////
////                    @Override
////                    public void onError(RongIMClient.ErrorCode errorCode) {
////                        UIUtils.postTaskSafely(() -> {
////                            mSessionMenuDialog.dismiss();
////                            mSessionMenuDialog = null;
////                            UIUtils.showToast(UIUtils.getString(R.string.recall_fail) + ":" + errorCode.getValue());
////                        });
////                    }
////                }));
//
////                tvDelete.setOnClickListener(v -> RongIMClient.getInstance().deleteMessages(new int[]{message.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
////                    @Override
////                    public void onSuccess(Boolean aBoolean) {
////                        UIUtils.postTaskSafely(() -> {
////                            mSessionMenuDialog.dismiss();
////                            mSessionMenuDialog = null;
////                            mData.remove(position);
////                            mAdapter.notifyDataSetChangedWrapper();
////                            UIUtils.showToast(UIUtils.getString(R.string.delete_success));
////                        });
////                    }
////
////                    @Override
////                    public void onError(RongIMClient.ErrorCode errorCode) {
////                        UIUtils.postTaskSafely(() -> {
////                            mSessionMenuDialog.dismiss();
////                            mSessionMenuDialog = null;
////                            UIUtils.showToast(UIUtils.getString(R.string.delete_fail) + ":" + errorCode.getValue());
////                        });
////                    }
////                }));
//                mSessionMenuDialog.show();
//                return false;
//            });
            UIUtils.postTaskDelay(() -> getView().getRvMsg().smoothMoveToPosition(mData.size() - 1), 200);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
            if (getView() != null && getView().getRvMsg() != null)
                rvMoveToBottom();
        }
    }

    private void rvMoveToBottom() {
        getView().getRvMsg().smoothMoveToPosition(mData.size() - 1);
    }

    private void updateMessageStatus(Message message) {
        for (int i = 0; i < mData.size(); i++) {
            Message msg = mData.get(i);
            if (msg.getMessageId() == message.getMessageId()) {
                mData.remove(i);
                mData.add(i, message);
                mAdapter.notifyDataSetChangedWrapper();
                break;
            }
        }
    }

    /**
     * 更新消息的状态
     * @param messageId
     */
    private void updateMessageStatus(int messageId) {
//        RongIMClient.getInstance().getMessage(messageId, new RongIMClient.ResultCallback<Message>() {
//            @Override
//            public void onSuccess(Message message) {
//                for (int i = 0; i < mData.size(); i++) {
//                    Message msg = mData.get(i);
//                    if (msg.getMessageId() == message.getMessageId()) {
//                        mData.remove(i);
//                        mData.add(i, message);
//                        mAdapter.notifyDataSetChangedWrapper();
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//
//            }
//        });
    }

    public void sendTextMsg() {
        String content = getView().getEtContent().getText().toString();
        getView().getEtContent().setText("");

        int type = mConversationType == Conversation.ConversationType.PRIVATE ?
                com.gs.factory.model.db.Message.RECEIVER_TYPE_NONE :
                com.gs.factory.model.db.Message.RECEIVER_TYPE_GROUP;

        // 构建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mSessionId, type)
                .content(content, com.gs.factory.model.db.Message.TYPE_STR)
                .build();

        // 进行网络发送
        MessageHelper.push(model);
    }

    public void sendTextMsg(String content) {
        LogUtils.d("content :" + content);
//        RongIMClient.getInstance().sendMessage(mConversationType, mSessionId, TextMessage.obtain(content), mPushCotent, mPushData,
//                new RongIMClient.SendMessageCallback() {// 发送消息的回调
//                    @Override
//                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
//                        updateMessageStatus(integer);
//                    }
//
//                    @Override
//                    public void onSuccess(Integer integer) {
//                        updateMessageStatus(integer);
//                    }
//                }, new RongIMClient.ResultCallback<Message>() {//消息存库的回调，可用于获取消息实体
//                    @Override
//                    public void onSuccess(Message message) {
//                        mAdapter.addLastItem(message);
//                        rvMoveToBottom();
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//                        LogUtils.d("errorCode:" + errorCode);
//
//                    }
//                });
    }

    public void sendImgMsg(Uri imageFileThumbUri, Uri imageFileSourceUri) {
//        ImageMessage imgMsg = ImageMessage.obtain(imageFileThumbUri, imageFileSourceUri);
//        RongIMClient.getInstance().sendImageMessage(mConversationType, mSessionId, imgMsg, mPushCotent, mPushData,
//                new RongIMClient.SendImageMessageCallback() {
//                    @Override
//                    public void onAttached(Message message) {
//                        //保存数据库成功
//                        mAdapter.addLastItem(message);
//                        rvMoveToBottom();
//                    }
//
//                    @Override
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                        //发送失败
//                        updateMessageStatus(message);
//                    }
//
//                    @Override
//                    public void onSuccess(Message message) {
//                        //发送成功
//                        updateMessageStatus(message);
//                    }
//
//                    @Override
//                    public void onProgress(Message message, int progress) {
//                        //发送进度
//                        message.setExtra(progress + "");
//                        updateMessageStatus(message);
//                    }
//                });
    }

    public void sendImgMsg(File imageFileThumb, File imageFileSource) {
        Uri imageFileThumbUri = Uri.fromFile(imageFileThumb);
        Uri imageFileSourceUri = Uri.fromFile(imageFileSource);
        sendImgMsg(imageFileThumbUri, imageFileSourceUri);
    }

    public void sendFileMsg(File file) {
//        Message fileMessage = Message.obtain(mSessionId, mConversationType, FileMessage.obtain(Uri.fromFile(file)));
//        RongIMClient.getInstance().sendMediaMessage(fileMessage, mPushCotent, mPushData, new IRongCallback.ISendMediaMessageCallback() {
//            @Override
//            public void onProgress(Message message, int progress) {
//                //发送进度
//                message.setExtra(progress + "");
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onCanceled(Message message) {
//
//            }
//
//            @Override
//            public void onAttached(Message message) {
//                //保存数据库成功
//                mAdapter.addLastItem(message);
//                rvMoveToBottom();
//            }
//
//            @Override
//            public void onSuccess(Message message) {
//                //发送成功
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                //发送失败
//                updateMessageStatus(message);
//            }
//        });
    }

    public void sendLocationMessage(LocationData locationData) {
//        LocationMessage message = LocationMessage.obtain(locationData.getLat(), locationData.getLng(), locationData.getPoi(), Uri.parse(locationData.getImgUrl()));
//        RongIMClient.getInstance().sendLocationMessage(Message.obtain(mSessionId, mConversationType, message), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
//            @Override
//            public void onAttached(Message message) {
//                //保存数据库成功
//                mAdapter.addLastItem(message);
//                rvMoveToBottom();
//            }
//
//            @Override
//            public void onSuccess(Message message) {
//                //发送成功
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                //发送失败
//                updateMessageStatus(message);
//            }
//        });
    }

    public void sendAudioFile(Uri audioPath, int duration) {
//        if (audioPath != null) {
//            File file = new File(audioPath.getPath());
//            if (!file.exists() || file.length() == 0L) {
//                LogUtils.sf(UIUtils.getString(R.string.send_audio_fail));
//                return;
//            }
//            VoiceMessage voiceMessage = VoiceMessage.obtain(audioPath, duration);
//            RongIMClient.getInstance().sendMessage(Message.obtain(mSessionId, mConversationType, voiceMessage), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
//                @Override
//                public void onAttached(Message message) {
//                    //保存数据库成功
//                    mAdapter.addLastItem(message);
//                    rvMoveToBottom();
//                }
//
//                @Override
//                public void onSuccess(Message message) {
//                    //发送成功
//                    updateMessageStatus(message);
//                }
//
//                @Override
//                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    //发送失败
//                    updateMessageStatus(message);
//                }
//            });
//        }
    }

    public void sendRedPacketMsg() {
//        if (mConversationType == Conversation.ConversationType.PRIVATE) {
//            UserInfo userInfo = DBManager.getInstance().getUserInfo(mSessionId);
//            if (userInfo != null)
//                RedPacketUtil.startRedPacket(mContext, userInfo, RPSendPacketCallback);
//        } else {
//            List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(mSessionId);
//            if (groupMembers != null)
//                RedPacketUtil.startRedPacket(mContext, mSessionId, groupMembers.size(), RPSendPacketCallback);
//        }
    }

//    RPSendPacketCallback RPSendPacketCallback = new RPSendPacketCallback() {
//        @Override
//        public void onGenerateRedPacketId(String redPacketId) {
//
//        }
//
//        @Override
//        public void onSendPacketSuccess(RedPacketInfo redPacketInfo) {
//            RedPacketMessage rpMsg = RedPacketMessage.obtain(redPacketInfo.redPacketId, redPacketInfo.fromNickName, redPacketInfo.redPacketType, redPacketInfo.redPacketGreeting);
//            RongIMClient.getInstance().sendMessage(Message.obtain(mSessionId, mConversationType, rpMsg), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
//                @Override
//                public void onAttached(Message message) {
//                    //保存数据库成功
//                    mAdapter.addLastItem(message);
//                    rvMoveToBottom();
//                }
//
//                @Override
//                public void onSuccess(Message message) {
//                    //发送成功
//                    updateMessageStatus(message);
//                }
//
//                @Override
//                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    //发送失败
//                    updateMessageStatus(message);
//                }
//            });
//        }
//    };

    public void downloadMediaMessage(Message message) {
//        RongIMClient.getInstance().downloadMediaMessage(message, new IRongCallback.IDownloadMediaMessageCallback() {
//            @Override
//            public void onSuccess(Message message) {
//                message.getReceivedStatus().setDownload();
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onProgress(Message message, int progress) {
//                //发送进度
//                message.setExtra(progress + "");
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                updateMessageStatus(message);
//            }
//
//            @Override
//            public void onCanceled(Message message) {
//                updateMessageStatus(message);
//            }
//        });
    }


    public Message toMessageUI(com.gs.factory.model.db.Message message){
        Message messageUI = new Message();
        if(message.getType()==com.gs.factory.model.db.Message.TYPE_STR){
            TextMessage textMessage = new TextMessage(message.getContent());
            messageUI.setContent(textMessage);
        }
        messageUI.setConversationType((message.getReceiver() == null && message.getGroup() != null)?
                Conversation.ConversationType.GROUP :
                Conversation.ConversationType.PRIVATE);
        if(mSessionId.equals(message.getSender().getId()) &&
                Account.getUserId().equals(message.getReceiver().getId())){
            //接受
            messageUI.setMessageDirection(Message.MessageDirection.RECEIVE);
            messageUI.setReceivedStatus( new Message.ReceivedStatus(1) );
            messageUI.setReceivedTime(message.getCreateAt().getTime());
        }else if(mSessionId.equals(message.getReceiver().getId()) &&
                (Account.getUserId().equals(message.getSender().getId()) )){
            //发送
            messageUI.setMessageDirection(Message.MessageDirection.SEND);
            messageUI.setSentStatus( Message.SentStatus.SENT );
            messageUI.setSentTime(message.getCreateAt().getTime());
        }else{
            return null;
        }
        messageUI.setSenderUserId(message.getSender().getId());
//        messageUI.setMessageId(message.getReceiver() == null ? message.getGroup().getId() : message.getReceiver().getId());
        return messageUI;
    }

    //取消监听
    public void dispose() {
        if(mConversationType  == Conversation.ConversationType.PRIVATE) {
            messageRepository.dispose();
        }else {
            messageGroupRepository.dispose();
        }
    }


    //获取会话中，从指定消息之前、指定数量的最新消息实体
    public void getLocalHistoryMessage() {
        LogUtils.d("getLocalHistoryMessage:1 thread: " + Thread.currentThread().getName());
        if(mConversationType  == Conversation.ConversationType.PRIVATE) {
            messageRepository.load(new DataSource.SucceedCallback<List<com.gs.factory.model.db.Message>>() {
                //此时是在子线程中执行的。
                @Override
                public void onDataLoaded(List<com.gs.factory.model.db.Message> messages) {
                    LogUtils.d("getLocalHistoryMessage:2 thread: " + Thread.currentThread().getName());
                    //根据message的类型，判断是什么类型的message，然后再放到data中。
                    if (messages != null && messages.size() > 0) {
                        List<Message> messageList = new ArrayList<>();
                        for (com.gs.factory.model.db.Message message : messages) {
                            Message messageUI = toMessageUI(message);
                            if(messageUI != null)
                                messageList.add(messageUI);
                        }

                        if(messageList.isEmpty())
                            return ;

                        Run.onUiAsync(new Action() {
                            @Override
                            public void call() {
                                // 这里是主线程运行时
                                updateAllData(messageList);
                            }
                        });
                    }
                }
            });
        }else{
            messageGroupRepository.load(new DataSource.SucceedCallback<List<com.gs.factory.model.db.Message>>() {
                @Override
                public void onDataLoaded(List<com.gs.factory.model.db.Message> messages) {
                    LogUtils.d("getLocalHistoryMessage:2 thread: " + Thread.currentThread().getName());
                    //根据message的类型，判断是什么类型的message，然后再放到data中。
                    if (messages != null && messages.size() > 0) {
                        List<Message> messageList = new ArrayList<>();
                        for (com.gs.factory.model.db.Message message : messages) {
                            Message messageUI = toMessageUI(message);
                            messageList.add(messageUI);
                        }

                        Run.onUiAsync(new Action() {
                            @Override
                            public void call() {
                                // 这里是主线程运行时
                                updateAllData(messageList);
                            }
                        });
                    }
                }
            });
        }

    }

    //单聊、群聊、讨论组、客服的历史消息从远端获取
    public void getRemoteHistoryMessages() {
        //消息中的 sentTime；第一次可传 0，获取最新 count 条。
        long dateTime = 0;
        if (mData.size() > 0) {
            dateTime = mData.get(0).getSentTime();
        } else {
            dateTime = 0;
        }

//        RongIMClient.getInstance().getRemoteHistoryMessages(mConversationType, mSessionId, dateTime, mMessageCount,
//                new RongIMClient.ResultCallback<List<Message>>() {
//                    @Override
//                    public void onSuccess(List<Message> messages) {
//                        saveHistoryMsg(messages);
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//                        loadMessageError(errorCode);
//                    }
//                });
    }

    private void updateAllData(List<Message> messages){
        mData.clear();
        mData.addAll(messages);
        getView().getRvMsg().moveToPosition(messages.size() - 1);
    }

    private void saveHistoryMsg(List<Message> messages) {
        //messages的时间顺序从新到旧排列，所以必须反过来加入到mData中
        if (messages != null && messages.size() > 0) {
            for (Message msg : messages) {
                mData.add(0, msg);
            }
            getView().getRvMsg().moveToPosition(messages.size() - 1);
        }
    }

//    private void loadMessageError(RongIMClient.ErrorCode errorCode) {
//        LogUtils.sf("拉取历史消息失败，errorCode = " + errorCode);
//    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    /**
     * 消息撤回监听
     * @param messageId
     * @param recallNotificationMessage
     */
//    public void recallMessageFromListener(int messageId, RecallNotificationMessage recallNotificationMessage) {
//        for (int i = 0; i < mData.size(); i++) {
//            Message message = mData.get(i);
//            if (message.getMessageId() == messageId) {
//                recallMessageAndInsertMessage(recallNotificationMessage, i);
//                break;
//            }
//        }
//    }

//    private void recallMessageAndInsertMessage(RecallNotificationMessage recallNotificationMessage, int position) {
//        RongIMClient.getInstance().insertMessage(mConversationType, mSessionId, UserCache.getId(), recallNotificationMessage);
//        mData.remove(position);
//        mData.add(Message.obtain(mSessionId, mConversationType, recallNotificationMessage));
//        mAdapter.notifyDataSetChangedWrapper();
//    }
}