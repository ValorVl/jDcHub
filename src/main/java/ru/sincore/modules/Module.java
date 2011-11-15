package ru.sincore.modules;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract module class
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 14:23
 */
public abstract class Module implements Runnable
{
    private ClassLoader moduleClassLoader = null;
    private AtomicBoolean isRun = new AtomicBoolean(false);

    /**
     * Initial module procedure
     *
     * @return true if modules successfuly initialized else false is returned
     */
    public abstract boolean init();

    /**
     * Deinitialization module procedure
     *
     * @return true if module successfuly deinitialized else false is returned
     */
    public abstract boolean deinit();

    /**
     * Provide internal module name
     *
     * @return module name
     */
    public abstract String getName();


    /**
     * Provide internal module version
     *
     * @return module version
     */
    public abstract String getVersion();


    /**
     * Return asynchronous event handler
     *
     * @return null if event handler does not esists, else instance of class-handler
     */
    public Object getEventHandler()
    {
        return null;
    }


    /**
     * Return synchronous signal handler
     *
     * @return null if signal handler does not esists, else instance of class-handler
     */
    public Object getSignalHandler()
    {
        return null;
    }


    public boolean isRun()
    {
        return this.isRun.get();
    }


    public void stop()
    {
        isRun = new AtomicBoolean(false);
    }


    public void setModuleClassLoader(ClassLoader classLoader)
    {
        this.moduleClassLoader = classLoader;
    }



    public void run()
    {
        if (moduleClassLoader != null)
        {
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
        }

        isRun.set(init());

        while (isRun.get())
        {
            synchronized (this)
            {
                // Notify all that init process completed and module worked
                this.notifyAll();
            }

            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }
}
