package ru.sincore.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.ProxyClassLoader;

import java.io.InputStream;

/**
 * Given
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 10.11.11
 *         Time: 9:43
 */
public class ConfigLoader extends ProxyClassLoader
{
    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    private ClassLoader loader = null;

    public ConfigLoader(ClassLoader loader)
    {
        this.loader = loader;
    }


    @Override
    public Class loadClass(String className, boolean resolveIt)
    {
        Class result;
        try
        {
            result = loader.loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }

        log.trace( "Returning class " + className + " loaded with thread context classloader" );

        return result;
    }


    @Override
    public InputStream loadResource(String name)
    {
        InputStream is = loader.getResourceAsStream(name);

        if( is != null )
        {
            log.trace( "Returning resource " + name + " loaded with thread context classloader" );

            return is;
        }

        return null;
    }
}
