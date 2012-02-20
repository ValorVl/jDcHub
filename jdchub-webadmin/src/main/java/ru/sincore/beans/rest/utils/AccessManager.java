package ru.sincore.beans.rest.utils;

import javax.servlet.http.HttpServletRequest;

import ru.sincore.beans.rest.data.CommonRequest;

/**
 * Simple access manager for JDCHub REST services
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 16:13
 */
public class AccessManager
{
    /**
     * Check for valid session
     *
     * @param request   servlet request
     * @param data      {@link CommonRequest} object that contain token info
     * @return  <code>true</code> if session exists and valid, else <code>false</code>
     */
    public static boolean check(HttpServletRequest request, CommonRequest data)
    {
        if (data == null  || data.getToken() == null || (data.getToken().trim().equals("")))
        {
            return false;
        }

        Session session = SessionManager.getSession(request, data.getToken());
        if (session == null)
        {
            return false;
        }

        return true;
    }


    /**
     * Check for valid session and valid user weight
     *
     * @param request       servlet request
     * @param data          {@link CommonRequest} object that contain token info
     * @param needWeight    needed user weight
     * @return <code>true</code> if session valid and weight of user is greater or equal <code>needWeight</code>
     */
    public static boolean check(HttpServletRequest request, CommonRequest data, int needWeight)
    {
        if (check(request, data) == false)
        {
            return false;
        }

        Session session = SessionManager.getSession(request, data.getToken());
        int weight = session.<Integer>get("weight");

        if (weight < needWeight)
        {
            return false;
        }

        return true;
    }
}
