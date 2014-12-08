package cn.ccagame.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * This is the single entry point for accessing configuration properties.
 * 
 * @author zhangjk
 */
@SuppressWarnings("rawtypes")
public class SystemConfig
{
    private static Properties mConfig;

    private static String systemConfigPath = "/system-config.properties";

    private static Log log = LogFactory.getFactory().getInstance(SystemConfig.class);

    /*
     * Static block run once at class loading We load the default properties and any custom properties we find
     */
    static
    {
        mConfig = new Properties();

        try
        {
            // we'll need this to get at our properties files in the classpath

            Class configClass = Class.forName("cn.ccagame.util.SystemConfig");


            // first, lets load our default properties
            InputStream is = configClass.getResourceAsStream(systemConfigPath);
            mConfig.load(is);
            log.info("successfully loaded default properties.");

            // Now expand system properties for properties in the
            // config.expandedProperties list,
            // replacing them by their expanded values.
            String expandedPropertiesDef = (String)mConfig.get("config.expandedProperties");
            if (expandedPropertiesDef != null)
            {
                String[] expandedProperties = expandedPropertiesDef.split(",");
                for (int i = 0; i < expandedProperties.length; i++)
                {
                    String propName = expandedProperties[i].trim();
                    String initialValue = (String)mConfig.get(propName);
                    if (initialValue != null)
                    {
                        String expandedValue = PropertyExpander.expandSystemProperties(initialValue);
                        mConfig.put(propName, expandedValue);
                        if (log.isDebugEnabled())
                        {
                            log.info("Expanded value of " + propName + " from '" + initialValue + "' to '" + expandedValue + "'");
                        }
                    }
                }
            }

            // some debugging for those that want it
            if (log.isDebugEnabled())
            {
                log.debug("SystemConfig looks like this ...");

                String key = null;
                Enumeration keys = mConfig.keys();
                while (keys.hasMoreElements())
                {
                    key = (String)keys.nextElement();
                    log.debug(key + "=" + mConfig.getProperty(key));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // no, you may not instantiate this class :p
    private SystemConfig()
    {
    }

    /**
     * Retrieve a property value
     * 
     * @param key
     *            Name of the property
     * @return String Value of property requested, null if not found
     */
    public static String getProperty(String key)
    {
        return mConfig.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue)
    {
        log.debug("Fetching property [" + key + "=" + mConfig.getProperty(key) + "]");

        String value = SystemConfig.getProperty(key);

        if (value == null)
        {
            return defaultValue;
        }

        return value;
    }

    /**
     * Retrieve a property as a boolean ... defaults to false if not present.
     */
    public static boolean getBooleanProperty(String name)
    {
        return getBooleanProperty(name, false);
    }

    /**
     * Retrieve a property as a boolean ... with specified default if not present.
     */
    public static boolean getBooleanProperty(String name, boolean defaultValue)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);

        if (value == null)
        {
            return defaultValue;
        }
        return (new Boolean(value)).booleanValue();
    }

    /**
     * Retrieve a property as a int ... defaults to 0 if not present.
     * 
     * @param name
     * @return
     */
    public static int getIntProperty(String name)
    {
        return getIntProperty(name, 0);
    }

    /**
     * ????�娲????��??�?????�????��
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public static int getIntProperty(String name, int defaultValue)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);

        if (value == null)
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    /**
     * ????�娲????��??�?????�???��???nt?�佹�?���?
     * 
     * @param name
     * @param defaultValue
     * @author wondtech liangming
     * @date 2008-07-25
     * @return int[]
     */
    public static int[] getIntPropertyArray(String name, int[] defaultValue)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);

        if (value == null)
        {
            return defaultValue;
        }

        try
        {
            String[] propertyArray = value.split(",");// ???�???�??????????��????��?�?
            int[] result = new int[propertyArray.length];
            for (int i = 0; i < propertyArray.length; i++)
            {// 
                result[i] = Integer.parseInt(propertyArray[i]);
            }
            return result;
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    /**
     * ????�娲????��??�?????�???��???oolean?�佹�?���?
     * 
     * @param name
     * @param defaultValue
     * @author wondtech liangming
     * @date 2008-07-25
     * @return boolean[]
     */
    public static boolean[] getBooleanPropertyArray(String name, boolean[] defaultValue)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);
        if (value == null)
        {
            return defaultValue;
        }
        try
        {
            String[] propertyArray = value.split(",");// ???�???�??????????��????��?�?
            boolean[] result = new boolean[propertyArray.length];
            for (int i = 0; i < propertyArray.length; i++)
            {// 
                result[i] = (new Boolean(propertyArray[i])).booleanValue();
            }
            return result;
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    /**
     * ????�娲????��??�?????�???��???tr?�佹�?���?
     * 
     * @param name
     * @param defaultValue
     * @author wondtech liangming
     * @date 2008-07-25
     * @return String[]
     */
    public static String[] getPropertyArray(String name, String[] defaultValue)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);
        if (value == null)
        {
            return defaultValue;
        }
        try
        {
            String[] propertyArray = value.split(",");// ???�???�??????????��????��?�?
            return propertyArray;
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    /**
     * 
     * 
     * @param name
     * @param defaultValue
     * @author wondtech liangming
     * @date 2008-07-25
     * @return String[]
     */
    public static String[] getPropertyArray(String name)
    {
        // get the value first, then convert
        String value = SystemConfig.getProperty(name);
        if (value == null)
        {
            return null;
        }
        try
        {
            String[] propertyArray = value.split(",");
            return propertyArray;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * Retrieve all property keys
     * 
     * @return Enumeration A list of all keys
     */
    public static Enumeration keys()
    {
        return mConfig.keys();
    }



    @SuppressWarnings("unchecked")
	public static Map getPropertyMap(String name)
    {
        String[] maps = getPropertyArray(name);
        Map map = new TreeMap();
        try
        {
            for (String str : maps)
            {
                String[] array = str.split(":");
                if (array.length > 1)
                {
                    map.put(array[0], array[1]);
                }
            }
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }

        return map;
    }
}
