package cn.ccagame.database;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
  
public final class JedisUtil {  
    private static final Logger LOGGER = Logger.getLogger(JedisUtil.class);  
    private static final String FILE_NAME = "redis.properties";  
    private static int DEFAULT_DB_INDEX = 0;  
  
    private static JedisPool jedisPool = null;  
  
    private JedisUtil() {  
        // private constructor  
    }  
  
    private static void initialPool() {  
        final Properties configurations = new Properties();  
        String filePath;  
        String address = "";  
        int port = 6379;  
  
        try {  
            filePath = JedisUtil.class.getResource("/").getPath() + "/" + FILE_NAME;  
            File file = new File(filePath);  
  
            configurations.load(new FileInputStream(file));  
            address = configurations.getProperty("redis.ip");  
            port = Integer.valueOf(configurations.getProperty("redis.port"));  
  
            String strDbIndex = configurations.getProperty("db_index");  
            if (strDbIndex != null) {  
                DEFAULT_DB_INDEX = Integer.valueOf(strDbIndex);  
            }  
            LOGGER.info("Redis server info: " + address + ":" + port);  
  
            final JedisPoolConfig config = new JedisPoolConfig();  
            String strMaxActive = configurations.getProperty("redis.pool.maxActive");  
            if (strMaxActive != null) {  
                config.setMaxActive(Integer.valueOf(strMaxActive));  
            }  
            String strMaxIdle = configurations.getProperty("redis.pool.maxIdle");  
            if (strMaxIdle != null) {  
                config.setMaxIdle(Integer.valueOf(strMaxIdle));  
            }  
            String strMaxWait = configurations.getProperty("redis.pool.maxWait");  
            if (strMaxWait != null) {  
                config.setMaxWait(Integer.valueOf(strMaxWait));  
            }  
            config.setTestOnBorrow(false);  
  
            int timeout = 4000;  
            jedisPool = new JedisPool(config, address, port, timeout);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
  
        }  
  
    }  
  
    public synchronized static Jedis getJedisInstance() {  
        if (jedisPool == null) {  
            initialPool();  
        }  
        try {  
            if (jedisPool != null) {  
                Jedis resource = jedisPool.getResource();  
                resource.select(DEFAULT_DB_INDEX);  
                return resource;  
            } else {  
                return null;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    public synchronized static Jedis getJedisInstance(final int dbIndex) {  
        if (jedisPool == null) {  
            initialPool();  
        }  
        try {  
            if (jedisPool != null) {  
                Jedis resource = jedisPool.getResource();  
                resource.select(dbIndex);  
                return resource;  
            } else {  
                return null;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    public static void returnResource(final Jedis jedis) {  
        if (jedis != null) {  
            jedisPool.returnResource(jedis);  
        }  
    }  
}  
