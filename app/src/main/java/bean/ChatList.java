package bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import yu.cai.greendao.gen.ChatListDao;
import yu.cai.greendao.gen.DaoSession;
import yu.cai.greendao.gen.MessageListDao;

/**
 * Created by admin on 2017-7-7.
 */

@Entity(indexes = {
        @Index(value = "friendId DESC", unique = true)
})
public class ChatList {
    @Id
    private Long id;

    private String currentTime; //消息时间

    private String message; //最后一条消息

    private String friendName; //  朋友名字

    private  Long friendId; //朋友ID（唯一）

    private int ResourseId ;//朋友头像

    @ToMany(referencedJoinProperty = "messageId")

    private List<MessageList> messageLists; //每个聊天item 对应一个聊天详情（包含多条消息）

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;

/** Used for active entity operations. */
@Generated(hash = 845789030)
private transient ChatListDao myDao;

@Generated(hash = 1503891834)
public ChatList(Long id, String currentTime, String message, String friendName,
        Long friendId, int ResourseId) {
    this.id = id;
    this.currentTime = currentTime;
    this.message = message;
    this.friendName = friendName;
    this.friendId = friendId;
    this.ResourseId = ResourseId;
}

@Generated(hash = 406825685)
public ChatList() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getCurrentTime() {
    return this.currentTime;
}

public void setCurrentTime(String currentTime) {
    this.currentTime = currentTime;
}

public String getMessage() {
    return this.message;
}

public void setMessage(String message) {
    this.message = message;
}

public String getFriendName() {
    return this.friendName;
}

public void setFriendName(String friendName) {
    this.friendName = friendName;
}

public Long getFriendId() {
    return this.friendId;
}

public void setFriendId(Long friendId) {
    this.friendId = friendId;
}

public int getResourseId() {
    return this.ResourseId;
}

public void setResourseId(int ResourseId) {
    this.ResourseId = ResourseId;
}

/**
 * To-many relationship, resolved on first access (and after reset).
 * Changes to to-many relations are not persisted, make changes to the target entity.
 */
@Generated(hash = 669167183)
public List<MessageList> getMessageLists() {
    if (messageLists == null) {
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        MessageListDao targetDao = daoSession.getMessageListDao();
        List<MessageList> messageListsNew = targetDao
                ._queryChatList_MessageLists(id);
        synchronized (this) {
            if (messageLists == null) {
                messageLists = messageListsNew;
            }
        }
    }
    return messageLists;
}

/** Resets a to-many relationship, making the next get call to query for a fresh result. */
@Generated(hash = 106701125)
public synchronized void resetMessageLists() {
    messageLists = null;
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}

/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}

/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 876516360)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getChatListDao() : null;
}


    @Override
    public String toString() {
        return "ChatList{" +
                "id=" + id +
                ", currentTime='" + currentTime + '\'' +
                ", message='" + message + '\'' +
                ", friendName='" + friendName + '\'' +
                ", friendId=" + friendId +
                ", ResourseId=" + ResourseId +
                ", messageLists=" + messageLists +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                '}';
    }
}
