/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.request;

import commons.enums.Action;
import lombok.Builder;
import lombok.Getter;
import model.User;

/**
 *
 * @author dangt
 */
@Getter
public class ChangeNameRequest extends Request{
    private User user;
    private String nickname;

    @Builder
    public ChangeNameRequest(Action action, User user, String nickname) {
        super(action);
        this.user = user;
        this.nickname = nickname;
    }
}
