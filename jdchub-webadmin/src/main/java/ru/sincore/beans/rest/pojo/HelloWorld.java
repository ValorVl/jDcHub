package ru.sincore.beans.rest.pojo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 03.02.12
 *         Time: 12:49
 */
@XmlRootElement
public class HelloWorld
{
    private String text;


    public String getText()
    {
        return text;
    }


    public void setText(String text)
    {
        this.text = text;
    }
}
