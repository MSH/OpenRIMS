package org.msh.pdex2.model.enums;

/**
 * Created with IntelliJ IDEA.
 * User: utkarsh
 * Date: 5/13/14
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
public enum YesNoNA {

    YES,
    NO,
    NA;

    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
