package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Auth request data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 15:18
 */
@XmlRootElement(name = "auth_request")
@XmlAccessorType(XmlAccessType.NONE)
public class AuthRequest
{
    @XmlElement(name = "login")
    private String login;

    @XmlElement(name = "password")
    private String password;


    public String getLogin()
    {
        return login;
    }


    public void setLogin(String login)
    {
        this.login = login;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }
}
