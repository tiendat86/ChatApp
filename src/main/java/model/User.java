/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author dangt
 */
@Data
@Builder
public class User implements Serializable {
    private String username, password, nickname;
    final long serialVersionUID = 3L;
}
