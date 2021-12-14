/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import model.User;

/**
 *
 * @author dangt
 */
@Getter
public class LoginResponse extends Response{
    private User user;

    @Builder
    public LoginResponse(User user, StatusCode statusCode) {
        this.user = user;
        this.statusCode = statusCode;
    }
}
