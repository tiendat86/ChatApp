/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.request;

import commons.enums.Action;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import model.User;

/**
 *
 * @author dangt
 */
@Getter
public class SendMessageAllRequest extends Request {
    private String message;
    
    @Builder
    public SendMessageAllRequest(Action action, String message) {
        super(action);
        this.message = message;
    }
    
}
