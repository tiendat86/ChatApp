/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author dangt
 */
@Getter
public class MessageResponse extends Response {
    private String nickname, message;

    @Builder
    public MessageResponse(String nickname, String message, StatusCode statusCode) {
        this.nickname = nickname;
        this.message = message;
        this.statusCode = statusCode;
    }
}
