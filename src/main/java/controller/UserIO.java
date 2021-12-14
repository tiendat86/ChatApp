/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author dangt
 */
public class UserIO extends JDBCConnection {
    public User login(String username, String password) {
        String sql = "select * from user where username = ? and password = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                return User.builder().username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .nickname(rs.getString("nickname"))
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public void register(String username, String password, String nickname) {
        String sql = "insert into user (username, password, nickname) VALUES (?,?,?);";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, nickname);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public User getUserByNickname(String nickname) {
        String sql = "select * from user where nickname = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                return User.builder().username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .nickname(rs.getString("nickname"))
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserIO.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "select * from user where username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
                return User.builder().username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .nickname(rs.getString("nickname"))
                        .build();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserIO.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
    
    public void updateNickname(User user, String nickname) {
        String sql = "update user set nickname = ? where (username = ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nickname);
            ps.setString(2, user.getUsername());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
}
