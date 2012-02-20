package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User request data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 09.02.12
 *         Time: 16:29
 */
@XmlRootElement(name = "users_request")
@XmlAccessorType(XmlAccessType.NONE)
public class UsersRequest extends CommonRequest
{
    @XmlElement(name = "user_mask")
    private String userMask = null;

    public String getUserMask()
    {
        return userMask;
    }


    public void setUserMask(String userMask)
    {
        this.userMask = userMask;
    }
}
