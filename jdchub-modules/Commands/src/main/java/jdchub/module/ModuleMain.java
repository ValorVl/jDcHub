package jdchub.module;

import jdchub.module.commands.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Main;
import ru.sincore.cmd.CommandEngine;
import ru.sincore.modules.Module;

/**
 * This extension extends default cmd package,
 * which realizes user commands.
 *
 * @author Alexey 'lh' Antonov
 *         <p/>
 *         Date: 20.11.12
 */
public class ModuleMain extends Module
{
    private static final Logger log = LoggerFactory.getLogger(ModuleMain.class);

    private static final String moduleName    = "Commands";
    private static final String moduleVersion = "0.9.0";

    @Override
    public boolean init()
    {
        log.info("[" + moduleName + " module]: start initialization...");

        if (!waitServerInitialization(5000))
        {
            log.error("[" + moduleName + " module]: can\'t get HubServer.");
            return false;
        }

        // register handlers
        CommandEngine commandEngine = Main.getServer().getCommandEngine();
        if (commandEngine == null)
        {
            log.error("[" + moduleName + " module]: can\'t get CommandEngine.");
            return false;
        }

        commandEngine.registerCommand("regme",      new RegmeCommand());
        commandEngine.registerCommand("info",       new InfoCommand());
        commandEngine.registerCommand("kick",       new KickCommand());
        commandEngine.registerCommand("grant",      new GrantCommand());
        commandEngine.registerCommand("broadcast",  new BroadcastCommand());
        commandEngine.registerCommand("changepass", new ChangePassCommand());
        commandEngine.registerCommand("topic",      new TopicCommand());
        commandEngine.registerCommand("ban",        new BanCommand());
        commandEngine.registerCommand("banlist",    new BanlistCommand());
        commandEngine.registerCommand("unban",      new UnbanCommand());
        commandEngine.registerCommand("gag",        new GagCommand());
        commandEngine.registerCommand("nosearch",   new NoSearchCommand());
        commandEngine.registerCommand("noctm",      new NoCtmCommand());
        commandEngine.registerCommand("me",         new MeCommand());
        commandEngine.registerCommand("rules",      new RulesCommand());
        commandEngine.registerCommand("faq",        new FAQCommand());
        commandEngine.registerCommand("stats",      new StatsCommand());

        log.info("[" + moduleName + " module]: successfuly initialized.");
        return true;
    }


    @Override
    public boolean deinit()
    {
        // unregister handlers
        CommandEngine commandEngine = Main.getServer().getCommandEngine();

        commandEngine.unregisterCommand("regme");
        commandEngine.unregisterCommand("info");
        commandEngine.unregisterCommand("kick");
        commandEngine.unregisterCommand("grant");
        commandEngine.unregisterCommand("broadcast");
        commandEngine.unregisterCommand("changepass");
        commandEngine.unregisterCommand("topic");
        commandEngine.unregisterCommand("ban");
        commandEngine.unregisterCommand("banlist");
        commandEngine.unregisterCommand("unban");
        commandEngine.unregisterCommand("gag");
        commandEngine.unregisterCommand("nosearch");
        commandEngine.unregisterCommand("noctm");
        commandEngine.unregisterCommand("me");
        commandEngine.unregisterCommand("rules");
        commandEngine.unregisterCommand("faq");
        commandEngine.unregisterCommand("stats");

        log.info("[" + moduleName + " module]: successfuly deinited.");
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
