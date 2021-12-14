/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commons.request;

import commons.enums.Action;
import java.io.Serializable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author dangt
 */
@Getter
@RequiredArgsConstructor
public abstract class Request implements Serializable {
    @NonNull
    protected Action action;
}
