package ru.sincore.beans.rest.utils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

/**
 * Simple session manager for JDCHub REST services
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 16:08
 */
public class SessionManager
{
    public static final long INVALIDATE_PERIOD = 1000 * 60 * 5; // 5 min

    private static SessionManager manager = null;

    private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    //private Date lastInvalidationCheck = new Date();


    /**
     *  Main constructor
     */
    private SessionManager()
    {
        // I think there is more good way to invalidate sessions
        // Like: invalidate on demand
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Invalidate sessions
                while (true)
                {
                    try
                    {
                        Thread.sleep(INVALIDATE_PERIOD);
                    }
                    catch (Exception e){}

                    tryInvalidateSessions();
                }
            }
        }).start();
    }


    private void tryInvalidateSessions()
    {
        for (String key : sessions.keySet())
        {
            tryInvalidateSession(key);
        }
    }

    
    private boolean tryInvalidateSession(String key)
    {
        Session session = sessions.get(key);
        return tryInvalidateSession(session);
    }


    private boolean tryInvalidateSession(Session session)
    {
        boolean result = false;
        Date current = new Date();

        if (current.getTime() - session.getLastAccess().getTime() > INVALIDATE_PERIOD)
        {
            // Invalidate session
            System.out.println("Invalidate REST session: " + session.getToken());
            sessions.remove(session.getToken());
            result = true;
        }

        return result;
    }


    private static synchronized SessionManager instance()
    {
        if (manager == null)
        {
            manager = new SessionManager();
        }

        return manager;
    }


    /**
     * Get session by token
     *
     * @param request   servlet request
     * @param token     unique session token
     * @return          session object or <code>null</code> if it does not exists
     */
    public static Session getSession(HttpServletRequest request, String token)
    {
        Session session = null;
        SessionManager manager = instance();

        if (!manager.sessions.containsKey(token))
        {
            return null;
        }

        session = manager.sessions.get(token);
        // Add check for remove addr
        //if (session.get("re").equals(request.getRemoteAddr()))


        session.updateLastAccess();
        return session;
    }


    /**
     * Create new session and put it under SessionManager
     *
     * @param request   servlet request
     * @return return new session
     */
    public static Session newSession(HttpServletRequest request)
    {
        SessionManager manager = instance();
        Session session = new Session();

        // TODO: add remote addr info
        //

        manager.sessions.put(session.getToken(), session);

        return session;
    }
}
