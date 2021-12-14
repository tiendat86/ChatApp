/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import model.User;

/**
 *
 * @author dangt
 */
@Getter
public class UsersOnlineResponse extends Response {
    private List<User> users;

    @Builder
    public UsersOnlineResponse(List<User> users, StatusCode statusCode) {
        this.users = users;
        this.statusCode = statusCode;
    }
}
