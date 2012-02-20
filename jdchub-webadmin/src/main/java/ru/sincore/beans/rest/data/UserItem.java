package ru.sincore.beans.rest.data;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class that describe one user in UsersResponse data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 17.02.12
 *         Time: 11:41
 */
@XmlRootElement(name = "user_params")
@XmlAccessorType(XmlAccessType.NONE)
public class UserItem extends SetUserOptionsRequest
{
    @XmlElement(name = "nick")
    private String  nick;

    @XmlElement(name = "comment")
    private String  comment;

    @XmlElement(name = "tag")
    private String  tag;

    @XmlElement(name = "connection_speed")
    private Integer connectionSpeed;
    
    @XmlElement(name = "ip")
    private String  ip;

    @XmlElement(name = "email")
    private String  email;

    @XmlElement(name = "share_size")
    private Long shareSize;

    @XmlElement(name = "shared_files_count")
    private Long sharedFilesCount;

    @XmlElement(name = "is_registered")
    private Boolean isRegistered;

    public String getNick()
    {
        return nick;
    }


    public void setNick(String nick)
    {
        this.nick = nick;
    }


    public String getComment()
    {
        return comment;
    }


    public void setComment(String comment)
    {
        this.comment = comment;
    }


    public String getTag()
    {
        return tag;
    }


    public void setTag(String tag)
    {
        this.tag = tag;
    }


    public Integer getConnectionSpeed()
    {
        return connectionSpeed;
    }


    public void setConnectionSpeed(Integer connectionSpeed)
    {
        this.connectionSpeed = connectionSpeed;
    }


    public String getIp()
    {
        return ip;
    }


    public void setIp(String ip)
    {
        this.ip = ip;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail(String email)
    {
        this.email = email;
    }


    public Long getShareSize()
    {
        return shareSize;
    }


    public void setShareSize(Long shareSize)
    {
        this.shareSize = shareSize;
    }


    public Long getSharedFilesCount()
    {
        return sharedFilesCount;
    }


    public void setSharedFilesCount(Long sharedFilesCount)
    {
        this.sharedFilesCount = sharedFilesCount;
    }


    public Boolean getRegistered()
    {
        return isRegistered;
    }


    public void setRegistered(Boolean registered)
    {
        isRegistered = registered;
    }

}
