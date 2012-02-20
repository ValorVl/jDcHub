package ru.sincore.beans.rest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple session object for JDCHub REST services
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 15:36
 */
public class Session
{
    private String token;
    private Date   lastAccess;
    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();


    /**
     * Main constructor. Generate unique token on creation
     */
    public Session()
    {
        // TODO: check token quality
        try
        {
            SecureRandom  secureRandom  = SecureRandom.getInstance("SHA1PRNG");
            String        randomNumber  = new Integer(secureRandom.nextInt()).toString();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[]        result        = messageDigest.digest(randomNumber.getBytes());

            token = hexEncode(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            token = UUID.randomUUID().toString();
        }

        lastAccess = new Date();
    }


    /**
     * Encode byte array to HEX string
     * @param aInput        source byte array
     * @return              HEX-encoded byte array
     */
    static private String hexEncode( byte[] aInput)
    {
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for ( int idx = 0; idx < aInput.length; ++idx)
        {
            byte b = aInput[idx];
            result.append( digits[ (b&0xf0) >> 4 ] );
            result.append( digits[ b&0x0f] );
        }
        return result.toString();
    }


    /**
     * Get parameter from session object. If parameter does not exists or it have incompatible type
     * method return <code>null</code> value.
     *
     * <br/>
     *
     * Use:
     * <pre>
     *     Session session = new Session();
     *     ...
     *     String  string  = session.<String>get("string_param1");
     *     Integer integer = session.<Integer>get("integer_param2");
     * </pre>
     *
     * @param key       String key for parameter
     * @param <T>       Type for generic method
     * @return          value of parameter from session or <code>null</code> if parameter does not
     *                  exists or have incompatible type.
     */
    public <T> T get(String key)
    {
        T value = null;

        if (attributes.containsKey(key))
        {
            try
            {
                value = (T)attributes.get(key);
            }
            catch (Exception e)
            {
            }
        }

        updateLastAccess();

        return value;
    }


    /**
     * Store parameter value to session.
     *
     * @param key       String key for parameter (aka parameter name)
     * @param value     parameter value
     */
    public void set(String key, Object value)
    {
        attributes.put(key, value);
        updateLastAccess();
    }


    /**
     * Take keys for all params in session
     *
     * @return  set of params keys in session
     */
    public Set<String> getKeys()
    {
        return attributes.keySet();
    }


    /**
     * Get session token - unique session identifier
     *
     * @return session token. It is no <code>null</code> in any cases
     */
    public String getToken()
    {
        return token;
    }


    /**
     * Method returns the time of last access to session.
     *
     * Last access time updated in next cases:
     *   a) {@link #get(String)} method
     *   b) {@link #set(String, Object)} method
     *   c) manually after call method {@link #updateLastAccess} - in most cases {@link SessionManager} doing it
     *
     * @return last access time to session
     */
    public Date getLastAccess()
    {
        return lastAccess;
    }


    /**
     * Update last session access time. Reset it to current time
     */
    public void updateLastAccess()
    {
        lastAccess = new Date();
    }
}
