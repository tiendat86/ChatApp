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
import model.Message;

/**
 *
 * @author dangt
 */
@Getter
public class UpdateMessagePrivatedResponse extends Response {
    private List<Message> send;
    private List<Message> rec;

    @Builder
    public UpdateMessagePrivatedResponse(List<Message> send, List<Message> rec, StatusCode statusCode) {
        this.send = send;
        this.rec = rec;
        this.statusCode = statusCode;
    }
    
}
