package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JVM information response data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 15:15
 */
@XmlRootElement(name = "jvm_info_response")
@XmlAccessorType(XmlAccessType.NONE)
public class JvmInfoResponse extends CommonResponse
{
    {
        setErrorStatus(Constants.Error.NOT_IMPLEMENTED_ERROR);
    }
}
