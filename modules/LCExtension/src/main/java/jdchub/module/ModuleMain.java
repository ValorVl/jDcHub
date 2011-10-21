package jdchub.module;

import java.util.logging.Handler;

import ru.sincore.modules.Module;

/**
 * LC Extension implementation plugin
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 17:38
 */
public class ModuleMain extends Module
{
    private static final String moduleName    = "LcExtension";
    private static final String moduleVersion = "0.1.0";

    private LcExtensionHandler handler;

    @Override
    public boolean init()
    {
        System.out.println("Module " + moduleName + " inited");
        handler = new LcExtensionHandler();
        return true;
    }


    @Override
    public boolean deinit()
    {
        System.out.println("Module " + moduleName + " deinited");
        handler = null;
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
        return handler;
    }
}
