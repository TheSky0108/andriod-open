package com.gs.im.model.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import com.gs.im.common.model.Author;

import java.util.Date;
import java.util.Objects;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

/**
 * 包括 好友，陌生人，自己
 */
@Table(database = AppDatabase.class)
public class User extends BaseDbModel<User> implements Author, Comparable<User> {
    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    public static final int ROLE_FRIEND = 1;     //好友
    public static final int ROLE_STRANGER = 2;   //陌生人
    public static final int ROLE_SELF = 3;       //自己

    // 主键
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private int role;  //角色，好友，陌生人，自己
    @Column
    private String phone;
    @Column
    private String portrait;
    @Column
    private String localPortrait;  //本地头像
    @Column
    private String desc;
    @Column
    private int sex = 0;

    // 我对某人的备注信息，也应该写入到数据库中
    @Column
    private String alias;

    // 用户关注人的数量
    @Column
    private int follows;

    // 用户粉丝的数量
    @Column
    private int following;

    // 我与当前User的关系状态，是否已经关注了这个人
    @Column
    private boolean isFollow;

    // 时间字段
    @Column
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public String getLocalPortrait() {
        return localPortrait;
    }

    public void setLocalPortrait(String localPortrait) {
        this.localPortrait = localPortrait;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sex == user.sex
                && follows == user.follows
                && following == user.following
                && isFollow == user.isFollow
                && role == user.role
                && Objects.equals(id, user.id)
                && Objects.equals(name, user.name)
                && Objects.equals(phone, user.phone)
                && Objects.equals(portrait, user.portrait)
                && Objects.equals(desc, user.desc)
                && Objects.equals(alias, user.alias)
                && Objects.equals(modifyAt, user.modifyAt)
                && Objects.equals(localPortrait, user.localPortrait);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(User old) {
        // 主要关注Id即可
        return this == old || Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(User old) {
        // 显示的内容是否一样，主要判断 名字，头像，性别，是否已经关注
        return this == old || (
                Objects.equals(name, old.name)
                        && Objects.equals(portrait, old.portrait)
                        && Objects.equals(sex, old.sex)
                        && Objects.equals(isFollow, old.isFollow)
        );
    }

    @Override
    public int compareTo(@NonNull User user) {
        return this.getName().compareTo(user.getName());
    }
}
