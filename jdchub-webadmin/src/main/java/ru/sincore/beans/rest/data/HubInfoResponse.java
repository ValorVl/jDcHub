package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * jDCHub info
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 15:15
 */
@XmlRootElement(name = "hub_info_response")
@XmlAccessorType(XmlAccessType.NONE)
public class HubInfoResponse extends CommonResponse
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "version")
    private String version;

    @XmlElement(name = "share_size")
    private long   shareSize;

    @XmlElement(name = "shared_files_count")
    private long   sharedFilesCount;

    @XmlElement(name = "online_users_count")
    private int    onlineUsersCount;


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getVersion()
    {
        return version;
    }


    public void setVersion(String version)
    {
        this.version = version;
    }


    public long getShareSize()
    {
        return shareSize;
    }


    public void setShareSize(long shareSize)
    {
        this.shareSize = shareSize;
    }


    public long getSharedFilesCount()
    {
        return sharedFilesCount;
    }


    public void setSharedFilesCount(long sharedFilesCount)
    {
        this.sharedFilesCount = sharedFilesCount;
    }


    public int getOnlineUsersCount()
    {
        return onlineUsersCount;
    }


    public void setOnlineUsersCount(int onlineUsersCount)
    {
        this.onlineUsersCount = onlineUsersCount;
    }
}
