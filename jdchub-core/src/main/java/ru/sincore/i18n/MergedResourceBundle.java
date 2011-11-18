package ru.sincore.i18n;

import java.util.*;

/**
 * Class to store two or more bundles of resources and provide access to them
 *
 * <p>
 * Example of use:
 * <code>
 * <pre>
 *   ResourceBundle resource1 = ResourceBundle.getBundle("seriver_messages");
 *   ResourceBundle resource2 = ResourceBundle.getBundle("client_messages");
 *   ResourceBundle mergedBundle = new MergedResourceBundle(new ResourceBundle[] {resource1, resource2});
 *   ...
 *   mergedBundle.getString("server.message");
 *   mergedBundle.getString("client.message");
 * </pre>
 * </code>
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 26.10.11
 *         Time: 13:53
 */
public class MergedResourceBundle extends ResourceBundle
{
    private ResourceBundle[] bundles;


    public MergedResourceBundle(ResourceBundle[] bundles)
    {
        this.bundles = bundles;
    }


    @Override
    protected Object handleGetObject(String key)
    {
        Object value = null;

        if (key == null)
        {
            throw new NullPointerException();
        }

        // Take last key entry
        for (ResourceBundle bundle : bundles)
        {
            if (bundle.containsKey(key))
            {
                value = bundle.getObject(key);
            }
        }

        if (value == null)
        {
            throw new MissingResourceException("Can't find resource for bundle "
                                               +this.getClass().getName()
                                               +", key "+key,
                                               this.getClass().getName(),
                                               key);
        }

        return value;
    }


    @Override
    public Enumeration<String> getKeys()
    {
        Set<String> keySet = new HashSet<String>(handleKeySet());
        if (parent != null)
        {
            keySet.addAll(Collections.list(parent.getKeys()));
        }

        Enumeration<String> result = Collections.enumeration(keySet);

        return result;
    }


    @Override
    protected Set<String> handleKeySet()
    {
        Set<String> keySet = new HashSet<String>();
        for (ResourceBundle bundle : bundles)
        {
            keySet.addAll(bundle.keySet());
        }

        return keySet;
    }

}
