/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.response;

import commons.enums.StatusCode;
import java.io.Serializable;
import lombok.Getter;

/**
 *
 * @author dangt
 */
@Getter
public abstract class Response implements Serializable{
    protected StatusCode statusCode;
}
