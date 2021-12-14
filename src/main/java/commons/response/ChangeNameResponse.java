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
public class ChangeNameResponse extends Response{

    @Builder
    public ChangeNameResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
    
}
