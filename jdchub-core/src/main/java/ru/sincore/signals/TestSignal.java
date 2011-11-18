package ru.sincore.signals;

/**
 * Test (aka Ping) signal
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 12:29
 */
public class TestSignal
{
    private String text;


    public TestSignal()
    {
    }


    public TestSignal(String text)
    {
        this.text = text;
    }


    public String getText()
    {
        return text;
    }


    public void setText(String text)
    {
        this.text = text;
    }
}
