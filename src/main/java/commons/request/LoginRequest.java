/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.request;

import commons.enums.Action;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author dangt
 */
@Getter
public class LoginRequest extends Request {
    private String username, password;
    
    @Builder
    public LoginRequest(Action action, String username, String password) {
        super(action);
        this.username = username;
        this.password = password;
    }
}
