package ru.sincore.modules;

/**
 * Abstract module class
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 14:23
 */
public abstract class Module
{
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
}
