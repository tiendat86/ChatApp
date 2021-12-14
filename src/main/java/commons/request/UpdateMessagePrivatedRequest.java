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
public class UpdateMessagePrivatedRequest extends Request {
    private User user1, user2;
    
    @Builder
    public UpdateMessagePrivatedRequest(Action action, User user1, User user2) {
        super(action);
        this.user1 = user1;
        this.user2 = user2;
    }
    
}
