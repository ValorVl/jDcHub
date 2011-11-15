package ru.sincore.modules;

import com.adamtaft.eb.EventBusService;
import org.apache.mina.util.CopyOnWriteMap;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import ru.sincore.signalservice.Signal;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

/**
 * Dynamicaly adding modules manager
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 14:35
 */
public class ModulesManager
{
    private final Map<String, ModuleInfo> modules = new CopyOnWriteMap<String, ModuleInfo>();

    private String modulesDirectory = "./modules";

    // Manager instance
    private static ModulesManager manager = new ModulesManager();

    //
    // Static interfaces
    //

    public static ModulesManager instance()
    {
        return manager;
    }


    //
    // Non-static interfaces
    //

    private Module getModuleInstance(File moduleJar)
            throws
            MalformedURLException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException
    {
        Module instance = null;

        if (moduleJar.isDirectory())
        {
            // Module as directory

            JarClassLoader jcl = new JarClassLoader();

            // Try load dependecyes
            File libDirectory = new File(moduleJar.getAbsolutePath() + "/lib/");
            if (libDirectory.exists() && libDirectory.isDirectory())
            {
                jcl.add(libDirectory.getAbsolutePath());
            }

            // Add etc (config dir) to class-path
            File etcDirectory = new File(moduleJar.getAbsolutePath() + "/etc/");
            if (etcDirectory.exists() && etcDirectory.isDirectory())
            {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{ etcDirectory.toURI().toURL() });
                jcl.addLoader(new ConfigLoader(classLoader));
            }

            JclObjectFactory factory = JclObjectFactory.getInstance();
            instance = (Module) factory.create(jcl, "jdchub.module.ModuleMain");
        }
        else
        {
            // Module as file

            URLClassLoader classLoader;
            ClassLoader parentClassLoader = ModulesManager.class.getClassLoader();
            classLoader = new URLClassLoader(new URL[]{ moduleJar.toURI().toURL() },
                                             parentClassLoader);

            Class moduleClass = classLoader.loadClass("jdchub.module.ModuleMain");
            instance = (Module) moduleClass.newInstance();
        }

        return instance;
    }


    public boolean loadModule(File moduleJar)
    {
        try
        {
            Module moduleInstance = getModuleInstance(moduleJar);

            // TODO: load info about module from DB and skip init process if module disabled
            boolean isInited = moduleInstance.init();
            String name = moduleInstance.getName();

            if (isInited)
            {
                Object eventHandler = moduleInstance.getEventHandler();
                Object signalHandler = moduleInstance.getSignalHandler();

                if (eventHandler != null)
                {
                    EventBusService.subscribe(eventHandler);
                }

                if (signalHandler != null)
                {
                    Signal.addHandler(signalHandler);
                }
            }

            ModuleInfo moduleInfo = new ModuleInfo(moduleInstance, isInited);

            modules.put(name, moduleInfo);

            return true;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return false;
    }



    public void loadModules()
    {
        unloadModules();

        File modulesDirectory = new File(this.modulesDirectory);
        File[] moduleJars      = modulesDirectory.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return (name.endsWith(".jar"));
            }
        });

        System.out.println(modulesDirectory.getAbsolutePath());
        System.out.println(moduleJars);

        if (moduleJars == null)
        {
            return;
        }

        for (File moduleJar : moduleJars)
        {
            loadModule(moduleJar);
        }

    }


    public boolean unloadModule(String moduleName)
    {
        if (!modules.containsKey(moduleName))
        {
            return false;
        }

        boolean returnResult = true;
        ModuleInfo moduleInfo = modules.get(moduleName);

        Object eventHandler = moduleInfo.getModuleInstance().getEventHandler();
        Object signalHandler = moduleInfo.getModuleInstance().getSignalHandler();

        if (moduleInfo.isEnabled())
        {
            returnResult = moduleInfo.getModuleInstance().deinit();
        }

        if (returnResult)
        {
            if (eventHandler != null)
            {
                EventBusService.unsubscribe(eventHandler);
            }

            if (signalHandler != null)
            {
                Signal.removeHandler(signalHandler);
            }
        }

        return returnResult;
    }


    public void unloadModules()
    {
        Set<String> modulesNames = modules.keySet();
        for (String moduleName : modulesNames)
        {
            boolean isUnloaded = unloadModule(moduleName);
            if (isUnloaded)
            {
                modules.remove(moduleName);
            }
            else
            {
                // TODO: can't unload module
            }
        }
    }


    public boolean enableModule(String moduleName, boolean isEnabled)
    {
        if (modules.containsKey(moduleName) == false)
        {
            return false;
        }

        ModuleInfo moduleInfo = modules.get(moduleName);

        if (moduleInfo.isEnabled() == isEnabled)
        {
            // Do nothing
            return false;
        }

        boolean returnResult = true;

        if (isEnabled == true)
        {
            returnResult = moduleInfo.getModuleInstance().init();
            EventBusService.subscribe(moduleInfo.getModuleInstance().getEventHandler());
            Signal.addHandler(moduleInfo.getModuleInstance().getSignalHandler());
            moduleInfo.setEnabled(returnResult);
        }
        else
        {
            returnResult = moduleInfo.getModuleInstance().deinit();
            EventBusService.unsubscribe(moduleInfo.getModuleInstance().getEventHandler());
            Signal.removeHandler(moduleInfo.getModuleInstance().getSignalHandler());
            moduleInfo.setEnabled(!returnResult);
        }

        return returnResult;
    }


    public boolean isModuleEnabled(String moduleName)
    {
        if (modules.containsKey(moduleName) == false)
        {
            return false;
        }

        return modules.get(moduleName).isEnabled();
    }


    // Private module info class
    private class ModuleInfo
    {
        private Module  moduleInstance;
        private boolean isEnabled;


        public ModuleInfo(Module moduleInstance, boolean isEnabled)
        {
            this.moduleInstance = moduleInstance;
            this.isEnabled      = isEnabled;
        }


        public Module getModuleInstance()
        {
            return moduleInstance;
        }


        public boolean isEnabled()
        {
            return this.isEnabled;
        }


        public void setEnabled(boolean enabled)
        {
            isEnabled = enabled;
        }


        public String getName()
        {
            return moduleInstance.getName();
        }


        public String getVersion()
        {
            return moduleInstance.getVersion();
        }
    }
}
