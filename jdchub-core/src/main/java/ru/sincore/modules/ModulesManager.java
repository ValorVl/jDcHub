package ru.sincore.modules;

import com.adamtaft.eb.EventBusService;
import org.apache.mina.util.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;
import ru.sincore.db.dao.ModuleListDAO;
import ru.sincore.db.dao.ModuleListDAOImpl;
import ru.sincore.db.pojo.ModuleListPOJO;
import ru.sincore.signalservice.Signal;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
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
    private static final Logger log = LoggerFactory.getLogger(ModulesManager.class);

    private final Map<String, ModuleInfo> modules = new CopyOnWriteMap<String, ModuleInfo>();

    private String modulesDirectory = "./modules";

    // Manager nstance
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
    private void fillClassPath(File file, List<File> list)
    {
        final File[] childs = file.listFiles();
        if (childs != null)
        {
            for (File child : childs)
            {
                if (child.getName().endsWith(".jar") && (child.isFile() || child.isDirectory()))
                {
                    list.add(child);
                }
                else if (child.isDirectory())
                {
                    fillClassPath(child, list);
                }
                else
                {
                    // TODO
                }
            }
        }
    }


    private File[] findModules()
    {
        File modulesDirectory = new File(this.modulesDirectory);
        File[] moduleJars      = modulesDirectory.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return (name.endsWith(".jar"));
            }
        });

        if (moduleJars == null)
        {
            return null;
        }

        return moduleJars;
    }


    public Set<String> getModulesNames()
    {
        return modules.keySet();
    }
    
    
    private Module getModuleInstance(File moduleJar)
            throws
            MalformedURLException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException,
            InterruptedException
    {
        Module instance;

        // Detect parent class loader
        ClassLoader parentClassLoader = ModulesManager.class.getClassLoader();

        if (parentClassLoader == null)
        {
            parentClassLoader = ClassLoader.getSystemClassLoader();
        }


        List<URL> classPath = new ArrayList<URL>();

        if (moduleJar.isDirectory())
        {
            // Module as directory

            List<File> jarFiles = new ArrayList<File>();

            // Try load dependecies

            File libDirectory = new File(moduleJar.getAbsolutePath() + "/lib/");
            if (libDirectory.exists() && libDirectory.isDirectory())
            {
                fillClassPath(libDirectory, jarFiles);

/*
                if (!jarFiles.isEmpty())
                {
                    URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

                    // Hack for adding given directory to exists class-path
                    Class clazz = URLClassLoader.class;
                    Method method = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
                    method.setAccessible(true);

                    for (File jar : jarFiles)
                    {
                        method.invoke(classLoader, new Object[] {jar.toURI().toURL()});
                    }

                    jarFiles.clear();
                }
*/
            }


            // Add etc (config dir) to class-path
            File etcDirectory = new File(moduleJar.getAbsolutePath() + "/etc/");
            if (etcDirectory.exists() && etcDirectory.isDirectory())
            {
                jarFiles.add(etcDirectory);
            }

            File mainDirectory = new File(moduleJar.getAbsolutePath() + "/main/");
            if (mainDirectory.exists() && mainDirectory.isDirectory())
            {
                fillClassPath(mainDirectory, jarFiles);
            }
            else
            {
                throw new RuntimeException("Directory 'main' in directory-based plugin does not found: " + moduleJar);
            }

            // Fill class path for URLClassLoader
            for (File jar : jarFiles)
            {
                classPath.add(jar.toURI().toURL());
            }

        }
        else
        {
            // Module as file
            classPath.add(moduleJar.toURI().toURL());
        }

        final URLClassLoader classLoader;
        classLoader = new URLClassLoader(classPath.toArray(new URL[0]),
                                         parentClassLoader);

        Class moduleClass = classLoader.loadClass("jdchub.module.ModuleMain");
        instance = (Module) moduleClass.newInstance();
        instance.setModuleClassLoader(classLoader);

        return instance;
    }


    public boolean loadModule(Module moduleInstance)
    {
        try
        {
            if (!isModuleEnabled(moduleInstance.getName()))
            {
                return false;
            }

            try
            {
                ConfigurationManager.getInstance()
                                    .load(ConfigurationManager.getInstance().getHubConfigDir() +
                                          "/modules/" +
                                          moduleInstance.getName() +
                                          "/" +
                                          moduleInstance.getName() +
                                          ".properties"
                                         );
            }
            catch (Exception e) { /* ignore */ }

            Thread moduleThread = new Thread(moduleInstance);

            // Wait for module initialization
            synchronized (moduleInstance)
            {
                moduleThread.start();
                moduleInstance.wait(10000);
            }

            boolean isInited = moduleInstance.isRun();
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
            log.info("Module " + moduleInstance.getName() + " successfuly loaded.");
            return true;
        }
        catch (Throwable e)
        {
            log.error("Error while module " + moduleInstance.getName() + " loads: \n" + e.getMessage());
        }

        return false;
    }


    public boolean loadModule(String moduleName)
    {
        unloadModule(moduleName);
        
        for (File moduleJar : findModules())
        {
            try
            {
                Module moduleInstance = getModuleInstance(moduleJar);
                if (moduleInstance.getName().equals(moduleName))
                {
                    return loadModule(moduleInstance);
                }
            }
            catch (Throwable e)
            {
                log.error("Error while module jar " + moduleJar.getName() + " loads: \n" + e.getMessage());
            }
        }

        return false;
    }
    
    
    public void loadModules()
    {
        log.info("Modules loading started.");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                unloadModules();

                for (File moduleJar : findModules())
                {
                    try
                    {
                        Module moduleInstance = getModuleInstance(moduleJar);
                        loadModule(moduleInstance);
                    }
                    catch (Throwable e)
                    {
                        log.error("Error while module jar " +
                                  moduleJar.getName() +
                                  " loads: \n" +
                                  e.getMessage());
                    }
                }
            }
        }).start();
    }


    public boolean unloadModule(String moduleName)
    {
        boolean   returnResult = false;
        Throwable throwable    = null;

        try
        {
            if (!isModuleLoaded(moduleName))
            {
                return false;
            }

            log.info("Unload module: " + moduleName);

            ModuleInfo moduleInfo = modules.get(moduleName);

            Object eventHandler = moduleInfo.getModuleInstance().getEventHandler();
            Object signalHandler = moduleInfo.getModuleInstance().getSignalHandler();

            if (moduleInfo.isEnabled())
            {
                returnResult = moduleInfo.getModuleInstance().deinit();
            }

            if (returnResult)
            {
                // Stop module thread
                moduleInfo.getModuleInstance().stop();
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
                
                modules.remove(moduleName);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throwable = e;
        }

        if (!returnResult)
        {
            log.error("Can't unload module: " + moduleName + ", throwable: " + throwable);
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
        if (!modules.containsKey(moduleName))
        {
            return false;
        }

        ModuleInfo moduleInfo = modules.get(moduleName);

        if (moduleInfo.isEnabled() == isEnabled)
        {
            // Do nothing
            return false;
        }

        boolean returnResult;

        if (isEnabled)
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
        ModuleListDAO moduleListDAO = new ModuleListDAOImpl();
        ModuleListPOJO module = moduleListDAO.getModule(moduleName);

        if (module != null)
        {
            return module.isEnabled();
        }

        module = new ModuleListPOJO();

        module.setName(moduleName);
        module.setEnabled(true);

        return moduleListDAO.addModule(module);
    }

    
    public boolean isModuleLoaded(String moduleName)
    {
        return modules.containsKey(moduleName);
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
