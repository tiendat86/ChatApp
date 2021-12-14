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
public class MessagePrivatedRequest extends Request {
    private String message, nickname;
    
    @Builder
    public MessagePrivatedRequest(Action action, String message, String nickname) {
        super(action);
        this.message = message;
        this.nickname = nickname;
    }
    
}
