package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Auth response data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 17:12
 */
@XmlRootElement(name = "auth_response")
@XmlAccessorType(XmlAccessType.NONE)
public class AuthResponse extends CommonResponse
{
    @XmlElement(name = "token")
    private String token = null;

    @XmlElement(name = "user_status")
    private int    userStatus = Constants.UserStatus.UNREGISTERED_USER;

    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }


    public int getUserStatus()
    {
        return userStatus;
    }


    public void setUserStatus(int userStatus)
    {
        this.userStatus = userStatus;
    }
}
