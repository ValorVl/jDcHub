package ru.sincore.signalservice;

/**
 * Syncronous signal sending/receiving singleton interface
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 11:13
 */
public class Signal
{
    public static final SignalService signalSerive;
    static
    {
        signalSerive = new SignalService();
    }


    /**
     * Emit signal
     * @param signal
     */
    public static void emit(Object signal)
    {
        signalSerive.emit(signal);
    }


    /**
     * Add new signal handler
     * @param handler
     */
    public static void addHandler(Object handler)
    {
        signalSerive.addHandler(handler);
    }


    /**
     * Remove signal handler
     * @param handler
     */
    public static void removeHandler(Object handler)
    {
        signalSerive.removeHandler(handler);
    }

}
