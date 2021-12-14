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
public class FriendInvitationRequest extends Request {
    String nickname;
    
    @Builder
    public FriendInvitationRequest(Action action, String nickname) {
        super(action);
        this.nickname = nickname;
    }    
}
