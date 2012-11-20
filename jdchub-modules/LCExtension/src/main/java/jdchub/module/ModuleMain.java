package jdchub.module;

import java.util.logging.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ModuleMain.class);

    private static final String moduleName    = "LcExtension";
    private static final String moduleVersion = "0.1.0";

    private LcExtensionHandler handler;

    @Override
    public boolean init()
    {
        log.info("[" + moduleName + " module]: start initialization...");
        handler = new LcExtensionHandler();
        log.info("[" + moduleName + " module]: successfuly initialized.");
        return true;
    }


    @Override
    public boolean deinit()
    {
        log.info("Module " + moduleName + " deinited");
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
