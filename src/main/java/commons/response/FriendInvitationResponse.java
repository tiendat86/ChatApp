/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import model.User;

/**
 *
 * @author dangt
 */
@Getter
public class FriendInvitationResponse extends Response{
    Map<User, String> users;

    @Builder
    public FriendInvitationResponse(Map<User, String> users, StatusCode statusCode) {
        this.users = users;
        this.statusCode = statusCode;
    }
}
