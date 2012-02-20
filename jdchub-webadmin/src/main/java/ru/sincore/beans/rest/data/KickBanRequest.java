package ru.sincore.beans.rest.data;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bun or kick request data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 10.02.12
 *         Time: 10:06
 */
@XmlRootElement(name = "kick_ban_request")
@XmlAccessorType(XmlAccessType.NONE)
public class KickBanRequest extends CommonRequest
{
    @XmlElement(name = "expired_date")
    private Date   expiredDate = null;

    @XmlElement(name = "reason")
    private String reason    = "";


    public Date getExpiredDate()
    {
        return expiredDate;
    }


    public void setExpiredDate(Date expiredDate)
    {
        this.expiredDate = expiredDate;
    }


    public String getReason()
    {
        return reason;
    }


    public void setReason(String reason)
    {
        this.reason = reason;
    }
}
