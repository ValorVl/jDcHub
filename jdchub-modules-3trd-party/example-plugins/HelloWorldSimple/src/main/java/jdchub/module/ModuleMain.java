package jdchub.module;

import ru.sincore.modules.Module;

/**
 * HelloWorld (Simple) example module
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 17:38
 */
public class ModuleMain extends Module
{
    private static final String moduleName    = "HelloWorldSimple";
    private static final String moduleVersion = "1.0.0";

    @Override
    public boolean init()
    {
        System.out.println("Module " + moduleName + " inited");
        return true;
    }


    @Override
    public boolean deinit()
    {
        System.out.println("Module " + moduleName + " deinited");
        return true;
    }


    @Override
    public String getName()
    {
        return moduleName;
    }


    @Override
    public String getVersion()
    {
        return moduleVersion;
    }


    @Override
    public Object getEventHandler()
    {
        return null;
    }


    @Override
    public Object getSignalHandler()
    {
        return null;
    }
}
