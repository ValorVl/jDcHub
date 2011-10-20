package jdchub.module;

import ru.sincore.modules.Module;

/**
 * HelloWorld example module
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 17:38
 */
public class ModuleMain extends Module
{
    @Override
    public boolean init()
    {
        System.out.println("Module HelloWorld inited");
        return true;
    }


    @Override
    public boolean deinit()
    {
        System.out.println("Module HelloWorld deinited");
        return true;
    }


    @Override
    public String getName()
    {
        return "HelloWorld";
    }


    @Override
    public String getVersion()
    {
        return "0.1.0";
    }


    @Override
    public Object getEventHandler()
    {
        return super.getEventHandler();
    }


    @Override
    public Object getSignalHandler()
    {
        return super.getSignalHandler();
    }
}
