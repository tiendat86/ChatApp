/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client.socket;

import chat.client.view.ChatView;
import chat.client.view.FriendView;
import chat.client.view.LoginView;
import chat.client.view.MessagePrivateView;
import chat.client.view.RegisterView;
import chat.client.view.RequestFriendView;
import commons.enums.Action;
import commons.enums.StatusCode;
import commons.request.AllFriendRequest;
import commons.request.ChangeNameRequest;
import commons.request.FriendInvitationRequest;
import commons.request.LogOutRequest;
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
import commons.response.UpdateMessagePrivatedResponse;
import commons.response.UsersOnlineResponse;
import controller.FriendIO;
import controller.UserIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import model.Message;
import model.User;
import org.apache.commons.lang3.ObjectUtils;

/**
 *
 * @author dangt
 */
@Data
public class ClientProcess {

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    private User user;
    private LoginView loginView;
    private RegisterView registerView;
    private ChatView chatView;
    private FriendView friendView;
    private MessagePrivateView messagePrivateView;
    private RequestFriendView requestFriendView;

    public ClientProcess() {
        this.loginView = new LoginView();
        loginView.setVisible(true);
        this.chatView = new ChatView();
    }

    // Yêu cầu lấy tất cả bạn bè của user - giúp làm mới trạng thái khi có user khác đăng nhập
    public void updateFriendRequest() {
        try {
            sendRequest(AllFriendRequest.builder()
                    .action(Action.ALL_FRIEND)
                    .nickname(user.getNickname())
                    .build());
        } catch (IOException ex) {
            Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Yêu cầu lấy tất cả lời mời kết bạn của user
    public void updateFriendInvitationRequest() {
        try {
            sendRequest(FriendInvitationRequest.builder()
                    .action(Action.FRIEND_INVITATION)
                    .nickname(user.getNickname())
                    .build());
        } catch (IOException ex) {
            Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Xét action cho các button trong giao diện login
    public void actionLoginView() {
        // Button 1 - button đăng nhập
        this.loginView.clickButton(this.loginView.getJButton1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendRequest(LoginRequest.builder()
                            .action(Action.LOGIN)
                            .username(loginView.getJTextField1().getText())
                            .password(loginView.getJTextField2().getText())
                            .build());
                } catch (IOException ex) {
                    Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // Button đăng ký
        this.loginView.clickButton(this.loginView.getJButton2(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerView = new RegisterView();
                registerView.setVisible(true);
                registerView.toFront();
                loginView.toBack();
                loginView.hide();
                actionRegisterView();
            }
        });
    }
    
    public void actionRegisterView() {
        // Button quay lại giao diện đăng nhập
        this.registerView.clickButton(this.registerView.getJButton1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerView.setVisible(false);
                loginView.toFront();
                loginView.show();
            }
        });
        
        // Button đăng ký
        this.registerView.clickButton(this.registerView.getJButton2(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = registerView.getJTextField1().getText().trim();                
                String password = registerView.getJTextField2().getText().trim();
                String nickname = registerView.getJTextField3().getText().trim();
                UserIO userIO = new UserIO();
                if(username.equals("") || password.equals("") || nickname.equals("")) {
                    registerView.showMessageDialog("Please fill full information!");
                }
                else if(userIO.getUserByNickname(nickname) != null) {
                    registerView.showMessageDialog("Nickname already exists!");
                } else if(userIO.getUserByUsername(username) != null) {
                    registerView.showMessageDialog("Username already exists!");
                } else {
                    userIO.register(username, password, nickname);
                    registerView.showMessageDialog("Successful!");
                }
            }
        });
    }

    // Xét action cho các button trong giao diện chat
    public void actionChatView() {
        // Button 2 - button gửi tin nhắn
        this.chatView.clickButton(this.chatView.getJButton2(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (chatView.getJComboBox1().getSelectedItem().toString().equals("Send All")) {
                        sendRequest(SendMessageAllRequest.builder()
                                .action(Action.SEND_MESSAGE_ALL)
                                .message(chatView.getJTextField1().getText())
                                .build());
                    } else {
                        sendRequest(MessageRequest.builder()
                                .action(Action.SEND_MESSAGE)
                                .nickname(chatView.getJComboBox1().getSelectedItem().toString())
                                .message(chatView.getJTextField1().getText())
                                .build());
                    }
                    chatView.getJTextField1().setText("");
                } catch (IOException ex) {
                    Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Button menu item1 - button đăng xuất 
        this.chatView.clickMenuItem(this.chatView.getJMenuItem1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendRequest(LogOutRequest.builder().action(Action.LOG_OUT).build());
                    chatView.dispose();
                    System.exit(0);
                } catch (IOException ex) {
                    Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // Button menu item3 - button đổi nickname
        this.chatView.clickMenuItem(this.chatView.getJMenuItem3(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newname = chatView.getNickname();
                if (newname != null || !newname.equals("") || new UserIO().getUserByNickname(newname) == null) {
                    try {
                        user.setNickname(newname);
                        sendRequest(ChangeNameRequest.builder()
                                .action(Action.CHANGE_NICKNAME)
                                .nickname(newname)
                                .user(user).build());
                        chatView.showMessageDialog("Successfull!");
                    } catch (IOException ex) {
                        Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    chatView.showMessageDialog("Nickname already exists!");
                }
            }
        });

        // Button menu item1 - button xem danh sách bạn bè
        this.chatView.clickMenuItem(this.chatView.getJMenuItem4(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendView = new FriendView();
                friendView.setVisible(true);
                friendView.toFront();
                friendView.getJLabel1().setText("Hello " + user.getNickname() + " - Status: Online");
                actionFriendView();
                chatView.toBack();
                chatView.hide();
                updateFriendRequest();
            }
        });
    }

    // Xét action cho các button trong giao diện bạn bè
    public void actionFriendView() {
        // Button quay lại
        this.friendView.clickButton(this.friendView.getJButton2(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendView.setVisible(false);
                chatView.toFront();
                chatView.show();
            }
        });
        
        // Button gửi lời mời kết bạn
        this.friendView.clickButton(this.friendView.getJButton1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = friendView.getNickname();
                if (new UserIO().getUserByNickname(nickname) == null) {
                    friendView.showMessageDialog("Nickname doesn't exist!");
                } else {
                    new FriendIO().addFriend(user.getNickname(), nickname, 0);
                    updateFriendRequest();
                    friendView.showMessageDialog("Send invitation successfull!");
                }
            }
        });
        
        // button chat riêng
        this.friendView.clickButton(this.friendView.getJButton4(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = friendView.getJTable1().getSelectedRow();
                if(r > friendView.getTm().getRowCount() || r < 0) {
                    friendView.showMessageDialog("Choose nickname you want to chat!");
                } else {
                    String nickname = friendView.getTm().getValueAt(r, 0).toString();
                    messagePrivateView = new MessagePrivateView();
                    messagePrivateView.setVisible(true);
                    messagePrivateView.toFront();
                    messagePrivateView.getJLabel1().setText(nickname);
                    actionMessagePrivateView();
                    friendView.toBack();
                    friendView.hide();                   
                    try {
                        sendRequest(UpdateMessagePrivatedRequest.builder()
                                .action(Action.UPDATE_MESSAGE_ALL).user1(user)
                                .user2(new UserIO().getUserByNickname(nickname))
                                .build());
                    } catch (IOException ex) {
                        Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        // button xóa bạn bè
        this.friendView.clickButton(this.friendView.getJButton3(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = friendView.getJTable1().getSelectedRow();
                if (r > friendView.getTm().getRowCount() || r < 0) {
                    friendView.showMessageDialog("Choose nickname you want to chat!");
                } else {
                    String nickname = friendView.getTm().getValueAt(r, 0).toString();
                    new FriendIO().removeFriend(user.getNickname(), nickname);
                    updateFriendRequest();
                    friendView.showMessageDialog("Delete successful!");
                }
            }
        });
        
        // button xem lời mời kết bạn
        this.friendView.clickMenuItem(this.friendView.getJMenuItem1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestFriendView = new RequestFriendView();
                requestFriendView.setVisible(true);
                requestFriendView.toFront();
                actionRequestFriendView();
                friendView.toBack();
                friendView.hide();
                updateFriendInvitationRequest();
            }
        });
    }

    // Xét action cho các button trong giao diện lời mời kết bạn
    public void actionRequestFriendView() {
        // Button back
        this.requestFriendView.clickButton(this.requestFriendView.getJButton3(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestFriendView.setVisible(false);
                friendView.toFront();
                friendView.show();
            }
        });
        
        // Button accept
        this.requestFriendView.clickButton(this.requestFriendView.getJButton1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = requestFriendView.getJTable1().getSelectedRow();
                if(r > requestFriendView.getTm().getRowCount() || r < 0) {
                    requestFriendView.showMessageDialog("Choose nickname you want accept!");
                } else {
                    String nickname = requestFriendView.getTm().getValueAt(0, r).toString();
                    new FriendIO().acceptFriend(user.getNickname(), nickname);
                    updateFriendRequest();
                    requestFriendView.showMessageDialog("Accept invitation successfull!");
                    updateFriendInvitationRequest();
                    updateFriendRequest();
                }
            }
        });
    }
    
    // Xét action cho các button trong giao diện chat riêng
    public void actionMessagePrivateView() {
        // Button quay lại
        this.messagePrivateView.clickButton(this.messagePrivateView.getJButton2(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagePrivateView.setVisible(false);
                friendView.toFront();
                friendView.show();
            }
        });
        
        // Button gửi tin privated
        this.messagePrivateView.clickButton(this.messagePrivateView.getJButton1(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messagePrivateView.getJTextField1().getText();
                if(message == "") {
                    messagePrivateView.showMessageDialog("Hãy nhập tin nhắn!");
                } else {
                    String nickname = messagePrivateView.getJLabel1().getText();
                    try {
                        sendRequest(MessagePrivatedRequest.builder()
                                .action(Action.SEND_MESSAGE_PRIVATE)
                                .message(message).nickname(nickname)
                                .build());
                        messagePrivateView.getJTextField1().setText("");
                    } catch (IOException ex) {
                        Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    public void sendRequest(Request request) throws IOException {
        this.out.writeObject(request);
        this.out.flush();
    }

    private void close() throws IOException {
        if (this.in != null) {
            this.in.close();
        }
        if (this.out != null) {
            this.out.close();
        }

        if (this.clientSocket != null) {
            this.clientSocket.close();
        }
    }

    public void login(String username, String password) {
        try {
            sendRequest(LoginRequest.builder()
                    .action(Action.LOGIN)
                    .username(username)
                    .password(password)
                    .build());
        } catch (IOException ex) {
            Logger.getLogger(ClientProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());
            scanner = new Scanner(System.in);
            new ResponseProcess().start();
            actionLoginView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ResponseProcess extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Object object = in.readObject();
                    if (ObjectUtils.isEmpty(object)) {
                        continue;
                    }

                    if (object instanceof LoginResponse) {
                        LoginResponse loginResponse = (LoginResponse) object;
                        if (loginResponse.getStatusCode().equals(StatusCode.OK)) {
                            user = loginResponse.getUser();
                            chatView = new ChatView();
                            chatView.setVisible(true);
                            chatView.toFront();
                            actionChatView();
                            loginView.setVisible(false);
                        } else {
                            loginView.showMessageDialog("Login failed!");
                        }
                    }

                    if (object instanceof UsersOnlineResponse) {
                        UsersOnlineResponse usersOnlineResponse = (UsersOnlineResponse) object;
                        if (usersOnlineResponse.getStatusCode().equals(StatusCode.OK)) {
                            List<User> userSend = usersOnlineResponse.getUsers();
                            for (User i : userSend) {
                                System.out.println(i.getNickname());
                            }
                            userSend.remove(user);
                            if (userSend.size() == 0) {
                                loginView.showMessageDialog("No user online!");
                            } else {
                                chatView.setValuesForJBox(userSend.stream()
                                        .map(us -> us.getNickname())
                                        .collect(Collectors.toList()));
                            }
                        }
                    }

                    if (object instanceof MessageResponse) {
                        MessageResponse messageResponse = (MessageResponse) object;
                        if (messageResponse.getStatusCode().equals(StatusCode.OK)) {
                            chatView.getJTextArea1().append(messageResponse.getNickname()
                                    + " " + messageResponse.getMessage() + "\n");
                        } else {
                            chatView.showMessageDialog("User is offline!");
                        }
                    }

                    if (object instanceof AllFriendResponse) {
                        if(friendView != null) {
                            friendView.updateTable(((AllFriendResponse) object).getUsers());
                        }                        
                    }
                    if (object instanceof FriendInvitationResponse) {
                        if(requestFriendView != null) {
                            requestFriendView.updateTable(((FriendInvitationResponse) object).getUsers());
                        }                        
                    }
                    if(object instanceof MessagePrivatedResponse) {
                        MessagePrivatedResponse messagePrivatedResponse = (MessagePrivatedResponse) object;
                        if(messagePrivateView != null) {
                            messagePrivateView.appendRecTextPanel(messagePrivatedResponse.getMessage());
                        }
                    }
                    if(object instanceof ReceiveOwnMessageResponse) {
                        ReceiveOwnMessageResponse receiveOwnMessageResponse = (ReceiveOwnMessageResponse) object;
                        if(messagePrivateView != null) {
                            messagePrivateView.appendMeTextPanel(receiveOwnMessageResponse.getMessage());
                        }
                    }
                    if (object instanceof UpdateMessagePrivatedResponse) {
                        UpdateMessagePrivatedResponse updateMessagePrivatedResponse = (UpdateMessagePrivatedResponse) object;
                        List<Message> send = updateMessagePrivatedResponse.getSend();
                        List<Message> rec = updateMessagePrivatedResponse.getRec();
                        int i = 0, j = 0;
                        while (true) {  
                            if(i == send.size()) {
                                while (j < rec.size()) {                                    
                                    messagePrivateView.appendRecTextPanel(rec.get(j));
                                    j++;
                                }
                                break;
                            } 
                            else if(j == rec.size()) {
                                while (i < send.size()) {
                                   messagePrivateView.appendMeTextPanel(send.get(i));
                                   i++;
                                }
                                break;
                            }
//                            if(i == send.size() && j == rec.size())
//                                break;
                            else if(i < send.size() && send.get(i).getTime().before(rec.get(j).getTime())) {
                                messagePrivateView.appendMeTextPanel(send.get(i));
                                i++;
                            } else if (j < rec.size()){
                                messagePrivateView.appendRecTextPanel(rec.get(j));
                                j++;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
