/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;
import model.Message;

/**
 *
 * @author dangt
 */
@Getter
public class ReceiveOwnMessageResponse extends Response{
    private Message message;

    @Builder
    public ReceiveOwnMessageResponse(Message message, StatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
