package com.gs.factory.data.message;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gs.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import com.gs.factory.data.BaseDbRepository;
import com.gs.factory.model.db.Message;
import com.gs.factory.model.db.Message_Table;

import java.util.Collections;
import java.util.List;

/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发给这个人的，或者是他发送给我的
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    // 聊天的对象Id
    private String receiverId;
    private String currentId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
        this.currentId = Account.getUserId();
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        Log.d("111", "receiverId:"+receiverId +"currentId:"+currentId );

        // 接受个人 (sender_id == receiverId and group_id == null and message_receiver_id == currentId)
        // 接受群 or (sender_id == receiverId) and group_id != null
        // 发送 or (sender_id == currentId) and  message_receiver_id == receiverId
        //(sender_id == receiverId and group_id == null)
        // or (receiver_id==receiverId)
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull())
                        .and(Message_Table.receiver_id.eq(currentId)))
                .or(OperatorGroup.clause().and(Message_Table.receiver_id.eq(receiverId))
                        .and(Message_Table.receiver_id.eq(receiverId)))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        // receiverId 如果是发送者，那么Group==null情况下一定是发送给我的消息
        // 如果消息的接收者不为空，那么一定是发送给某个人的，这个人只能是我或者是某个人
        // 如果这个"某个人"就是receiverId，那么就是我需要关注的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId())
        );
    }

    // 重写了查询结果回调的函数
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {

        // 反转返回的集合
        Collections.reverse(tResult);
        // 然后再调度
        super.onListQueryResult(transaction, tResult);
    }
}