package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User option request data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 09.02.12
 *         Time: 17:06
 */
@XmlRootElement(name = "set_user_options_request")
@XmlAccessorType(XmlAccessType.NONE)
public class SetUserOptionsRequest
{
    @XmlElement(name = "weight")
    private Integer weight = null;

    @XmlElement(name = "ignore_hub_full")
    private Boolean ignoreHubFullParam = null;

    @XmlElement(name = "ignore_share_size")
    private Boolean ignoreShareSizeParam = null;

    @XmlElement(name = "ignore_spam")
    private Boolean ignoreSpamParam = null;


    public Integer getWeight()
    {
        return weight;
    }


    public void setWeight(Integer weight)
    {
        this.weight = weight;
    }


    public Boolean getIgnoreHubFullParam()
    {
        return ignoreHubFullParam;
    }


    public void setIgnoreHubFullParam(Boolean ignoreHubFullParam)
    {
        this.ignoreHubFullParam = ignoreHubFullParam;
    }


    public Boolean getIgnoreShareSizeParam()
    {
        return ignoreShareSizeParam;
    }


    public void setIgnoreShareSizeParam(Boolean ignoreShareSizeParam)
    {
        this.ignoreShareSizeParam = ignoreShareSizeParam;
    }


    public Boolean getIgnoreSpamParam()
    {
        return ignoreSpamParam;
    }


    public void setIgnoreSpamParam(Boolean ignoreSpamParam)
    {
        this.ignoreSpamParam = ignoreSpamParam;
    }
}
