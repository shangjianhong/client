package cn.ccagame.tool;



import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * The class use to provide a common way to lookup the JNDI.
 * 
 * @author Martin Liu
 */
public class JNDIUtil {

	/**
	 * Lookup JNDI remote object
	 * 
	 * @param jndiName
	 *            String
	 * @throws NamingException
	 */
	public static Object lookupRemoteObject(String jndiName)
			throws NamingException {

		Context initial = new InitialContext();
		return initial.lookup(jndiName);
	}

	/**
	 * Lookup JNDI local object
	 * 
	 * @param jndiName
	 *            String
	 * @throws NamingException
	 */
	public static Object lookupLocalObject(String jndiName)
			throws NamingException {
		Context initial = new InitialContext();
		return initial.lookup(jndiName);
	}

}
