package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Runtime info response data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 15:14
 */
@XmlRootElement(name = "runtime_info_response")
@XmlAccessorType(XmlAccessType.NONE)
public class RuntimeInfoResponse extends CommonResponse
{
    {
        setErrorStatus(Constants.Error.NOT_IMPLEMENTED_ERROR);
    }
}
