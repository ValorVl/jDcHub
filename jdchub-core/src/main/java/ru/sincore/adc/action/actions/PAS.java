package ru.sincore.adc.action.actions;

/**
 * PAS action parsing and composing
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 12:14
 */
public class PAS extends GPA
{
    {
        actionName = "PAS";
    }

    public PAS()
    {
        super();
    }


    public PAS(String rawCommand)
    {
        super(rawCommand);
    }
}
