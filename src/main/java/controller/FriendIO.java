/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Friend;
import model.User;

/**
 *
 * @author dangt
 */
public class FriendIO extends JDBCConnection {
    public List<User> getAllFriends(String nickname) {
        String sql = "Select * from friends where nickname_us1 = ? and status = 1";
        List<User> friends = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                friends.add(new UserIO().getUserByNickname(rs.getString("nickname_us2")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }
    
    public List<User> sendFriendInvitation(String nickname) {
        String sql = "Select * from friends where nickname_us2 = ? and status = 0";
        List<User> friends = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                friends.add(new UserIO().getUserByNickname(rs.getString("nickname_us1")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return friends;
    }
    
    public void acceptFriend(String nickname1, String nickname2) {
        addFriend(nickname1, nickname2, 1);
        String sql = "update friends set status = 1 WHERE (nickname_us1 = ?) and (nickname_us2 = ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname2);
            ps.setString(2, nickname1);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void addFriend(String nickname1, String nickname2, int status) {
        String sql = "insert into friends (nickname_us1, nickname_us2, status) values (?, ?, ?);";
        List<User> friends = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname1);           
            ps.setString(2, nickname2);
            ps.setInt(3, status);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeFriend(String nickname1, String nickname2) {
        String sql = "delete from friends where (nickname_us1 = ?) and (nickname_us2 = ?);";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname1);           
            ps.setString(2, nickname2);
            ps.executeUpdate();
            ps.setString(1, nickname2);           
            ps.setString(2, nickname1);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(FriendIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
