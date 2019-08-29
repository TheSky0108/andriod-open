package com.gs.open.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gs.open.api.base.BaseApiRetrofit;
import com.gs.open.model.request.AddGroupMemberRequest;
import com.gs.open.model.request.AddToBlackListRequest;
import com.gs.open.model.request.AgreeFriendsRequest;
import com.gs.open.model.request.ChangePasswordRequest;
import com.gs.open.model.request.CheckPhoneRequest;
import com.gs.open.model.request.CreateGroupRequest;
import com.gs.open.model.request.DeleteFriendRequest;
import com.gs.open.model.request.DeleteGroupMemberRequest;
import com.gs.open.model.request.DismissGroupRequest;
import com.gs.open.model.request.FriendInvitationRequest;
import com.gs.open.model.request.JoinGroupRequest;
import com.gs.open.model.request.LoginRequest;
import com.gs.open.model.request.QuitGroupRequest;
import com.gs.open.model.request.RegisterRequest;
import com.gs.open.model.request.RemoveFromBlacklistRequest;
import com.gs.open.model.request.RestPasswordRequest;
import com.gs.open.model.request.SendCodeRequest;
import com.gs.open.model.request.SetFriendDisplayNameRequest;
import com.gs.open.model.request.SetGroupDisplayNameRequest;
import com.gs.open.model.request.SetGroupNameRequest;
import com.gs.open.model.request.SetGroupPortraitRequest;
import com.gs.open.model.request.SetNameRequest;
import com.gs.open.model.request.SetPortraitRequest;
import com.gs.open.model.request.VerifyCodeRequest;
import com.gs.open.model.response.AddGroupMemberResponse;
import com.gs.open.model.response.AddToBlackListResponse;
import com.gs.open.model.response.AgreeFriendsResponse;
import com.gs.open.model.response.ChangePasswordResponse;
import com.gs.open.model.response.CheckPhoneResponse;
import com.gs.open.model.response.CreateGroupResponse;
import com.gs.open.model.response.DefaultConversationResponse;
import com.gs.open.model.response.DeleteFriendResponse;
import com.gs.open.model.response.DeleteGroupMemberResponse;
import com.gs.open.model.response.FriendInvitationResponse;
import com.gs.open.model.response.GetBlackListResponse;
import com.gs.open.model.response.GetFriendInfoByIDResponse;
import com.gs.open.model.response.GetGroupInfoResponse;
import com.gs.open.model.response.GetGroupMemberResponse;
import com.gs.open.model.response.GetGroupResponse;
import com.gs.open.model.response.GetTokenResponse;
import com.gs.open.model.response.GetUserInfoByIdResponse;
import com.gs.open.model.response.GetUserInfoByPhoneResponse;
import com.gs.open.model.response.GetUserInfosResponse;
import com.gs.open.model.response.JoinGroupResponse;
import com.gs.open.model.response.LoginResponse;
import com.gs.open.model.response.QiNiuTokenResponse;
import com.gs.open.model.response.QuitGroupResponse;
import com.gs.open.model.response.RegisterResponse;
import com.gs.open.model.response.RemoveFromBlackListResponse;
import com.gs.open.model.response.RestPasswordResponse;
import com.gs.open.model.response.SendCodeResponse;
import com.gs.open.model.response.SetFriendDisplayNameResponse;
import com.gs.open.model.response.SetGroupDisplayNameResponse;
import com.gs.open.model.response.SetGroupNameResponse;
import com.gs.open.model.response.SetGroupPortraitResponse;
import com.gs.open.model.response.SetNameResponse;
import com.gs.open.model.response.SetPortraitResponse;
import com.gs.open.model.response.SyncTotalDataResponse;
import com.gs.open.model.response.UserRelationshipResponse;
import com.gs.open.model.response.VerifyCodeResponse;
import com.gs.open.model.response.VersionResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @创建者 CSDN_LQR
 * @描述 使用Retrofit对网络请求进行配置
 */
public class ApiRetrofit extends BaseApiRetrofit {

    public MyApi mApi;
    public static ApiRetrofit mInstance;

    private ApiRetrofit() {
        super();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //在构造方法中完成对Retrofit接口的初始化
        mApi = new Retrofit.Builder()
                .baseUrl(MyApi.BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyApi.class);
    }

    public static ApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private RequestBody getRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    //登录
    public Observable<LoginResponse> login(String region, String phone, String password) {
        return mApi.login(getRequestBody(new LoginRequest(region, phone, password)));
    }

    //注册
    public Observable<CheckPhoneResponse> checkPhoneAvailable(String region, String phone) {
        return mApi.checkPhoneAvailable(getRequestBody(new CheckPhoneRequest(phone, region)));
    }

    public Observable<SendCodeResponse> sendCode(String region, String phone) {
        return mApi.sendCode(getRequestBody(new SendCodeRequest(region, phone)));
    }

    public Observable<VerifyCodeResponse> verifyCode(String region, String phone, String code) {
        return mApi.verifyCode(getRequestBody(new VerifyCodeRequest(region, phone, code)));
    }

    public Observable<RegisterResponse> register(String nickname, String password, String verification_token) {
        return mApi.register(getRequestBody(new RegisterRequest(nickname, password, verification_token)));
    }

    public Observable<GetTokenResponse> getToken() {
        return mApi.getToken();
    }

    //个人信息
    public Observable<SetNameResponse> setName(String nickName) {
        return mApi.setName(getRequestBody(new SetNameRequest(nickName)));
    }

    public Observable<SetPortraitResponse> setPortrait(String portraitUri) {
        return mApi.setPortrait(getRequestBody(new SetPortraitRequest(portraitUri)));
    }

    public Observable<ChangePasswordResponse> changePassword(String oldPassword, String newPassword) {
        return mApi.changePassword(getRequestBody(new ChangePasswordRequest(oldPassword, newPassword)));
    }

    /**
     * @param password           密码，6 到 20 个字节，不能包含空格
     * @param verification_token 调用 /user/verify_code 成功后返回的 activation_token
     */
    public Observable<RestPasswordResponse> restPassword(String password, String verification_token) {
        return mApi.restPassword(getRequestBody(new RestPasswordRequest(password, verification_token)));
    }

    //查询
    public Observable<GetUserInfoByIdResponse> getUserInfoById(String userid) {
        return mApi.getUserInfoById(userid);
    }

    public Observable<GetUserInfoByPhoneResponse> getUserInfoFromPhone(String region, String phone) {
        return mApi.getUserInfoFromPhone(region, phone);
    }

    //好友
    public Observable<FriendInvitationResponse> sendFriendInvitation(String userid, String addFriendMessage) {
        return mApi.sendFriendInvitation(getRequestBody(new FriendInvitationRequest(userid, addFriendMessage)));
    }

    public Observable<UserRelationshipResponse> getAllUserRelationship() {
        return mApi.getAllUserRelationship();
    }

    public Observable<GetFriendInfoByIDResponse> getFriendInfoByID(String userid) {
        return mApi.getFriendInfoByID(userid);
    }

    public Observable<AgreeFriendsResponse> agreeFriends(String friendId) {
        return mApi.agreeFriends(getRequestBody(new AgreeFriendsRequest(friendId)));
    }

    public Observable<DeleteFriendResponse> deleteFriend(String friendId) {
        return mApi.deleteFriend(getRequestBody(new DeleteFriendRequest(friendId)));
    }

    public Observable<SetFriendDisplayNameResponse> setFriendDisplayName(String friendId, String displayName) {
        return mApi.setFriendDisplayName(getRequestBody(new SetFriendDisplayNameRequest(friendId, displayName)));
    }

    public Observable<GetBlackListResponse> getBlackList() {
        return mApi.getBlackList();
    }

    public Observable<AddToBlackListResponse> addToBlackList(String friendId) {
        return mApi.addToBlackList(getRequestBody(new AddToBlackListRequest(friendId)));
    }

    public Observable<RemoveFromBlackListResponse> removeFromBlackList(String friendId) {
        return mApi.removeFromBlackList(getRequestBody(new RemoveFromBlacklistRequest(friendId)));
    }


    //群组
    public Observable<CreateGroupResponse> createGroup(String name, List<String> memberIds) {
        return mApi.createGroup(getRequestBody(new CreateGroupRequest(name, memberIds)));
    }

    public Observable<SetGroupPortraitResponse> setGroupPortrait(String groupId, String portraitUri) {
        return mApi.setGroupPortrait(getRequestBody(new SetGroupPortraitRequest(groupId, portraitUri)));
    }

    public Observable<GetGroupResponse> getGroups() {
        return mApi.getGroups();
    }

    public Observable<GetGroupInfoResponse> getGroupInfo(String groupId) {
        return mApi.getGroupInfo(groupId);
    }

    public Observable<GetGroupMemberResponse> getGroupMember(String groupId) {
        return mApi.getGroupMember(groupId);
    }

    public Observable<AddGroupMemberResponse> addGroupMember(String groupId, List<String> memberIds) {
        return mApi.addGroupMember(getRequestBody(new AddGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<DeleteGroupMemberResponse> deleGroupMember(String groupId, List<String> memberIds) {
        return mApi.deleGroupMember(getRequestBody(new DeleteGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<SetGroupNameResponse> setGroupName(String groupId, String name) {
        return mApi.setGroupName(getRequestBody(new SetGroupNameRequest(groupId, name)));
    }

    public Observable<QuitGroupResponse> quitGroup(String groupId) {
        return mApi.quitGroup(getRequestBody(new QuitGroupRequest(groupId)));
    }

    public Observable<QuitGroupResponse> dissmissGroup(String groupId) {
        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
    }
//    public Observable<DismissGroupResponse> dissmissGroup(String groupId) {
//        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
//    }

    public Observable<SetGroupDisplayNameResponse> setGroupDisplayName(String groupId, String displayName) {
        return mApi.setGroupDisplayName(getRequestBody(new SetGroupDisplayNameRequest(groupId, displayName)));
    }

    public Observable<JoinGroupResponse> JoinGroup(String groupId) {
        return mApi.JoinGroup(getRequestBody(new JoinGroupRequest(groupId)));
    }

    public Observable<DefaultConversationResponse> getDefaultConversation() {
        return mApi.getDefaultConversation();
    }

    public Observable<GetUserInfosResponse> getUserInfos(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        return mApi.getUserInfos(stringRequest);
    }

    //其他
    public Observable<QiNiuTokenResponse> getQiNiuToken() {
        return mApi.getQiNiuToken();
    }

    public Observable<VersionResponse> getClientVersion() {
        return mApi.getClientVersion();
    }

    public Observable<SyncTotalDataResponse> syncTotalData(String version) {
        return mApi.syncTotalData(version);
    }

}
