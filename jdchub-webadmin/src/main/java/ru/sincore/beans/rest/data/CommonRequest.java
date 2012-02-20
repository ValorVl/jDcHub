package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Common request data. Used as common part for all request data classes
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 15:22
 */
@XmlRootElement(name = "common_request")
@XmlAccessorType(XmlAccessType.NONE)
public class CommonRequest
{
    @XmlElement(name = "token")
    private String token = null;


    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }
}
