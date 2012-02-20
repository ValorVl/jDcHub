package ru.sincore.beans.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Common response data
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 15:35
 */
@XmlRootElement(name = "common_response")
@XmlAccessorType(XmlAccessType.NONE)
public class CommonResponse
{
    @XmlElement(name = "message")
    private String message = Constants.Error.NO_ERROR.errorText();

    @XmlElement(name = "error")
    private int    error   = Constants.Error.NO_ERROR.errorCode();

    
    public void setErrorStatus(Constants.Error error, String additionalComment)
    {
        StringBuffer stringBuffer = new StringBuffer();
        String       tempErrorText;
        if (error == null)
        {
            tempErrorText = Constants.Error.NO_ERROR.errorText();
            this.error   = Constants.Error.NO_ERROR.errorCode();
        }
        else
        {
            tempErrorText = error.errorText();
            this.error   = error.errorCode();
        }

        stringBuffer.append(tempErrorText);

        if (additionalComment != null)
        {
            stringBuffer.append(": ");
            stringBuffer.append(additionalComment);
        }

        this.message = stringBuffer.toString();
    }


    public void setErrorStatus(Constants.Error error)
    {
        setErrorStatus(error, null);
    }
    

    public String getMessage()
    {
        return message;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }


    public int getError()
    {
        return error;
    }


    public void setError(int error)
    {
        this.error = error;
    }
}
