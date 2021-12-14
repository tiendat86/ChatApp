/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import static chat.server.ServerProcess.mapClients;
import commons.enums.StatusCode;
import commons.request.AllFriendRequest;
import commons.request.ChangeNameRequest;
import commons.request.FriendInvitationRequest;
import commons.request.LoginRequest;
import commons.request.MessagePrivatedRequest;
import commons.request.MessageRequest;
import commons.request.Request;
import commons.request.SendMessageAllRequest;
import commons.request.UpdateMessagePrivatedRequest;
import commons.response.AllFriendResponse;
import commons.response.FriendInvitationResponse;
import commons.response.LoginResponse;
import commons.response.MessagePrivatedResponse;
import commons.response.MessageResponse;
import commons.response.ReceiveOwnMessageResponse;
import commons.response.Response;
import commons.response.UpdateMessagePrivatedResponse;
import commons.response.UsersOnlineResponse;
import controller.FriendIO;
import controller.MessageIO;
import controller.UserIO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import model.Message;
import model.User;
import org.apache.commons.lang3.ObjectUtils;

/**
 *
 * @author dangt
 */
@Getter
public class ClientHandler extends Thread {

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User user;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void response(Response response) throws IOException {
        this.out.writeObject(response);
        this.out.flush();
    }

    // Phản hồi yêu cầu thông tin tất cả user đang online
    private void responseAllUsers(ClientHandler clientHandler) {
        try {
            clientHandler.response(UsersOnlineResponse.builder()
                    .statusCode(StatusCode.OK)
                    .users(mapClients.keySet().stream().collect(Collectors.toList()))
                    .build());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Lấy thông tin user qua nickname
    private User getUserWithNickname(String nickname) {
        List<User> list = mapClients.keySet().stream().collect(Collectors.toList());
        for (User i : list) {
            if (i.getNickname().equals(nickname)) {
                return i;
            }
        }
        return null;
    }

    // Xác định trạng thái cho các bạn bè của user
    private void allFriendRequest(List<User> list, ClientHandler clientHandler) {
        Map<User, String> sendUs = new HashMap<>();
        for (User i : list) {
            String status = "Offline";
            if (mapClients.containsKey(i)) {
                status = "Online";
            }
            sendUs.put(i, status);
        }
        try {
            clientHandler.response(AllFriendResponse.builder()
                    .statusCode(StatusCode.OK)
                    .users(sendUs).build());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Xác định trạng thái cho các lời mời kết bạn
    private void friendInvitation(List<User> list, ClientHandler clientHandler) {
        Map<User, String> sendUs = new HashMap<>();
        for (User i : list) {
            String status = "Offline";
            if (mapClients.containsKey(i)) {
                status = "Online";
            }
            sendUs.put(i, status);
        }
        try {
            clientHandler.response(FriendInvitationResponse.builder()
                    .statusCode(StatusCode.OK)
                    .users(sendUs).build());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object input = in.readObject();
                if (ObjectUtils.isNotEmpty(input)) {
                    Request request = (Request) input;
                    switch (request.getAction()) {
                        case LOGIN: {
                            UserIO userIO = new UserIO();
                            User userLogin = userIO.login(((LoginRequest) request).getUsername(),
                                    ((LoginRequest) request).getPassword());
                            if (userLogin == null) {
                                this.response(LoginResponse.builder()
                                        .statusCode(StatusCode.BAD_REQUEST)
                                        .build());
                            } else {
                                this.response(LoginResponse.builder()
                                        .user(userLogin)
                                        .statusCode(StatusCode.OK)
                                        .build());
                                this.user = userLogin;
                                mapClients.put(user, this);
                                for (User key : mapClients.keySet()) {
                                    responseAllUsers(mapClients.get(key));
                                    if (key != user) {
                                        allFriendRequest(new FriendIO().getAllFriends(key.getNickname()),
                                                mapClients.get(key));
                                    }
                                }

                            }
                            break;
                        }
                        case ALL_USERS_ONLINE: {
                            responseAllUsers(this);
                            break;
                        }
                        case SEND_MESSAGE: {
                            User sendUser = getUserWithNickname(((MessageRequest) request).getNickname());
                            if (sendUser == null) {
                                this.response(MessageResponse.builder()
                                        .statusCode(StatusCode.BAD_REQUEST)
                                        .build());
                            } else {
                                mapClients.get(sendUser).response(MessageResponse.builder()
                                        .message("(privated): " + ((MessageRequest) (request)).getMessage())
                                        .nickname(this.getUser().getNickname())
                                        .statusCode(StatusCode.OK)
                                        .build());
                                this.response(MessageResponse.builder()
                                        .message("(privated) to " + sendUser.getNickname() + ": " + ((MessageRequest) (request)).getMessage())
                                        .nickname("You")
                                        .statusCode(StatusCode.OK)
                                        .build());
                            }
                            break;
                        }
                        case SEND_MESSAGE_ALL: {
                            SendMessageAllRequest sendMessageAllRequest = (SendMessageAllRequest) request;
                            for (User i : mapClients.keySet()) {
                                ClientHandler clientHandler = mapClients.get(i);
                                if (clientHandler != null) {
                                    clientHandler.response(MessageResponse.builder()
                                            .message("(all): " + sendMessageAllRequest.getMessage())
                                            .nickname(user.getNickname())
                                            .statusCode(StatusCode.OK)
                                            .build());
                                }
                            }
                            break;
                        }
                        case CHANGE_NICKNAME: {
                            new UserIO().updateNickname(((ChangeNameRequest) request).getUser(),
                                    ((ChangeNameRequest) request).getNickname());
                            ClientHandler change = mapClients.remove(this.user);
                            this.user.setNickname(((ChangeNameRequest) request).getNickname());
                            mapClients.put(this.user, change);
                            mapClients.keySet().forEach(i -> responseAllUsers(mapClients.get(i)));
                            break;
                        }
                        case ALL_FRIEND: {
                            AllFriendRequest allFriendRequest = (AllFriendRequest) request;
                            List<User> list = new FriendIO().getAllFriends(allFriendRequest.getNickname());
                            allFriendRequest(list, this);
                            break;
                        }
                        case FRIEND_INVITATION: {
                            FriendInvitationRequest friendInvitationRequest = (FriendInvitationRequest) request;
                            List<User> list = new FriendIO().sendFriendInvitation(friendInvitationRequest.getNickname());
                            friendInvitation(list, this);
                            for (User key : mapClients.keySet()) {
                                allFriendRequest(new FriendIO().getAllFriends(key.getNickname()), mapClients.get(key));
                            }
                            break;
                        }
                        case SEND_MESSAGE_PRIVATE: {
                            MessagePrivatedRequest messagePrivatedRequest = (MessagePrivatedRequest) request;
                            UserIO userIO = new UserIO();
                            User user2 = userIO.getUserByNickname(messagePrivatedRequest.getNickname());
                            Message message = Message.builder().user1(user)
                                    .user2(user2).time(new Date())
                                    .message(messagePrivatedRequest.getMessage())
                                    .build();
                            new MessageIO().saveMessasge(message);
                            
                            this.response(ReceiveOwnMessageResponse.builder()
                                    .message(message).statusCode(StatusCode.OK)
                                    .build());
                            ClientHandler received = mapClients.get(user2);
                            if(received != null) {
                                mapClients.get(user2).response(MessagePrivatedResponse.builder()
                                    .message(message).statusCode(StatusCode.OK)
                                    .build());
                            }  
                            break;
                        }
                        case UPDATE_MESSAGE_ALL: {
                            UpdateMessagePrivatedRequest updateMessagePrivatedRequest = (UpdateMessagePrivatedRequest) request;
                            MessageIO messageIO = new MessageIO();
                            List<Message> send = messageIO.getMessageSend(updateMessagePrivatedRequest.getUser1()
                                    , updateMessagePrivatedRequest.getUser2());
                            List<Message> rec = messageIO.getMessageRec(updateMessagePrivatedRequest.getUser1()
                                    , updateMessagePrivatedRequest.getUser2());
                            this.response(UpdateMessagePrivatedResponse.builder()
                                    .send(send).rec(rec)
                                    .statusCode(StatusCode.OK)
                                    .build());
                            break;
                        }
                        case LOG_OUT: {
                            mapClients.remove(this.user);
                            for (User key : mapClients.keySet()) {
                                if (key != user) {
                                    allFriendRequest(new FriendIO().getAllFriends(key.getNickname()),
                                            mapClients.get(key));
                                }
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
