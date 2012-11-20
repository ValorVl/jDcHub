package jdchub.module;

import com.adamtaft.eb.EventBusService;
import jdchub.module.feed.RSSFeedPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.events.AdcExtNoMoreSupported;
import ru.sincore.events.AdcExtSupported;
import ru.sincore.modules.Module;

/**
 * The extension adds RSS feed support.
 * Signal FEED in SUP and the INF’s SU field.
 *
 * @author Alexey 'lh' Antonov
 *         <p/>
 *         Date: 08.02.12
 */
public class ModuleMain extends Module
{
    private static final Logger log = LoggerFactory.getLogger(ModuleMain.class);

    private static final String moduleName    = "FEED";
    private static final String moduleVersion = "0.1.0";

    RSSFeedPublisher publisher = null;

    @Override
    public boolean init()
    {
        log.info("[" + moduleName + " module]: start initialization...");
        publisher = new RSSFeedPublisher();

        if (!publisher.start())
        {
            log.error("Module " + moduleName + " NOT inited.");
            return false;
        }

        EventBusService.publish(new AdcExtSupported("FEED"));

        log.info("[" + moduleName + " module]: successfuly initialized.");
        return true;
    }


    @Override
    public boolean deinit()
    {
        publisher.getTimer().cancel();
        publisher = null;

        EventBusService.publish(new AdcExtNoMoreSupported("FEED"));

        log.info("Module " + moduleName + " was deinited");
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
}
