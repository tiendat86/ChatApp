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
public class MessageRequest extends Request{
    private String nickname, message;
    
    @Builder
    public MessageRequest(Action action, String nickname, String message) {
        super(action);
        this.nickname = nickname;
        this.message = message;
    }
    
}
