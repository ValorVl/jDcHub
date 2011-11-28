package ru.sincore.signalservice;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Syncronous signal sending/receiving service
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 19.10.11
 *         Time: 10:36
 */
public class SignalService
{
    private final List<HandlerInfo>          handlers  = new CopyOnWriteArrayList<HandlerInfo>();
    private final BlockingQueue<HandlerInfo> killQueue = new LinkedBlockingQueue<HandlerInfo>();


    //
    // Constructors
    //

    public SignalService()
    {
        Thread killQueueThread = new Thread(new KillQueueRunner(), "KillQueue Consumer Thread [SignalService]");
        killQueueThread.setDaemon(true);
        killQueueThread.start();
    }


    //
    // Non-static interfaces
    //


    /**
     * Emit signal
     * @param signal
     */
    public void emit(Object signal)
    {
        try
        {
            for (HandlerInfo handlerInfo : handlers)
            {
                Object handler = handlerInfo.getHandler();
                if (handler == null)
                {
                    killQueue.put(handlerInfo);
                    continue;
                }

                if (!handlerInfo.matchesSignal(signal))
                {
                    continue;
                }

                handlerInfo.getMethod().invoke(handler, signal);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Add handler for signals
     * @param handler
     */
    public void addHandler(Object handler)
    {
        boolean isPresent = false;

        for (HandlerInfo info : handlers)
        {
            Object otherHandler = info.getHandler();
            if (otherHandler == null)
            {
                try
                {
                    killQueue.put(info);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                continue;
            }

            if (otherHandler == handler)
            {
                isPresent = true;
            }
        }

        if (isPresent)
        {
            return;
        }

        Method[] methods = handler.getClass().getDeclaredMethods();
        for (Method method : methods)
        {
            // Check that @SignalHandler annotation is present
            SignalHandler signalHandler = method.getAnnotation(SignalHandler.class);
            if (signalHandler == null)
            {
                continue;
            }

            // Check count of handler arguments
            Class<?> [] arguments = method.getParameterTypes();
            if (arguments.length != 1)
            {
                throw new IllegalArgumentException("SignalHandler methods must specify a single Object paramter.");
            }

            HandlerInfo info = new HandlerInfo(arguments[0], method, handler);
            handlers.add(info);
        }
    }


    /**
     * Remove signal handler
     * @param handler
     */
    public void removeHandler(Object handler)
    {
        List<HandlerInfo> killList = new ArrayList<HandlerInfo>();
        for (HandlerInfo info : handlers)
        {
            Object obj = info.getHandler();
            if (obj == null || obj == handler)
            {
                killList.add(info);
            }
        }

        for (HandlerInfo kill : killList)
        {
            handlers.remove(kill);
        }
    }


    //
    //
    //


    // consumer runnable to remove handler infos from the subscription list
    // if they are null.  this is if the GC has collected them.
    private class KillQueueRunner implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    HandlerInfo info = killQueue.take();
                    if (info.getHandler() == null)
                    {
                        handlers.remove(info);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    //
    // Private classes
    //


    // used to hold the handler details
    // taked from Adam Taft Simple Event Bus
    private static class HandlerInfo
    {
        private final Class<?>         signalClass;
        private final Method           method;
        private final WeakReference<?> handler;


        public HandlerInfo(Class<?> signalClass,
                           Method method,
                           Object handler)
        {
            this.signalClass = signalClass;
            this.method      = method;
            this.handler     = new WeakReference<Object>(handler);
        }


        public boolean matchesSignal(Object signal)
        {
            return signal.getClass().equals(signalClass);
        }


        public Method getMethod()
        {
            return method;
        }


        public Object getHandler()
        {
            return handler.get();
        }
    }

}
