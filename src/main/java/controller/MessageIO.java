/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.JDBCConnection.connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;
import model.User;

/**
 *
 * @author dangt
 */
public class MessageIO extends JDBCConnection {
    public void saveMessasge(Message message) {
        String sql = "insert into message (user1, user2, message, Date) values (?, ?, ?, ?);";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, message.getUser1().getUsername());           
            ps.setString(2, message.getUser2().getUsername());
            ps.setString(3, message.getMessage());
            ps.setTimestamp(4, new Timestamp(message.getTime().getTime()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Message> getMessageSend(User user1, User user2) {
        String sql = "Select * from message where user1 = ? and user2 = ?";
        List<Message> mess = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user1.getUsername());
            ps.setString(2, user2.getUsername());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                                
                mess.add(Message.builder()
                        .user1(user1).user2(user2)
                        .message(rs.getString("message"))
                        .time(new java.util.Date(rs.getTimestamp("Date").getTime()))
                        .build());
            }
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mess;
    }
    
    public List<Message> getMessageRec(User user1, User user2) {
        String sql = "Select * from message where user1 = ? and user2 = ?";
        List<Message> mess = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user2.getUsername());
            ps.setString(2, user1.getUsername());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                                
                mess.add(Message.builder()
                        .user1(user2).user2(user1)
                        .message(rs.getString("message"))
                        .time(new java.util.Date(rs.getTimestamp("Date").getTime()))
                        .build());
            }
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mess;
    }
    
    public Message getLastMessage(User user1, User user2) {
        String sql = "select * from message where user1 = ? "
                + "and user2 = ? order by Date desc";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user2.getUsername());
            ps.setString(2, user1.getUsername());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                return Message.builder()
                        .user1(user2).user2(user1)
                        .message(rs.getString("message"))
                        .time(new java.util.Date(rs.getTimestamp("Date").getTime()))
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
