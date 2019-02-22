package bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import yu.cai.greendao.gen.DaoSession;
import yu.cai.greendao.gen.ChatListDao;
import yu.cai.greendao.gen.MessageListDao;

/**
 * Created by admin on 2017-7-7.
 */
@Entity
public class MessageList {
    @Id(autoincrement = true)
    private Long id;

    public boolean isMyMsg;

    private String msg; //发送的消息

    private Long messageId;

    @ToOne(joinProperty = "messageId")

    private ChatList chatList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1759660196)
    private transient MessageListDao myDao;

    @Generated(hash = 2064698799)
    public MessageList(Long id, boolean isMyMsg, String msg, Long messageId) {
        this.id = id;
        this.isMyMsg = isMyMsg;
        this.msg = msg;
        this.messageId = messageId;
    }

    @Generated(hash = 1974901781)
    public MessageList() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsMyMsg() {
        return this.isMyMsg;
    }

    public void setIsMyMsg(boolean isMyMsg) {
        this.isMyMsg = isMyMsg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getMessageId() {
        return this.messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @Generated(hash = 1344993409)
    private transient Long chatList__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 613623810)
    public ChatList getChatList() {
        Long __key = this.messageId;
        if (chatList__resolvedKey == null || !chatList__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatListDao targetDao = daoSession.getChatListDao();
            ChatList chatListNew = targetDao.load(__key);
            synchronized (this) {
                chatList = chatListNew;
                chatList__resolvedKey = __key;
            }
        }
        return chatList;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 807908144)
    public void setChatList(ChatList chatList) {
        synchronized (this) {
            this.chatList = chatList;
            messageId = chatList == null ? null : chatList.getId();
            chatList__resolvedKey = messageId;
        }
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
    @Generated(hash = 857466230)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageListDao() : null;
    }

}