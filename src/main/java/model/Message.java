/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author dangt
 */
@Data
@Builder
public class Message implements Serializable {
    private User user1, user2;
    private String message;
    private Date time;
}
