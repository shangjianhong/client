package cn.ccagame.database.transaction;

/**
 * The Transaction interface allows operations to be performed against the
 * transaction in the target Transactioin object. A Transaction object is
 * created corresponding to each global transaction creation.
 * 
 * <p>
 * The Transaction object can be used for resource enlistment, synchronization
 * registration, transaction completion and status query operations.
 * 
 * @see javax.transaction.Transaction
 * 
 * @author Martin Liu
 */
public interface Transaction {
	public static int INIT_STATE = -1;

	public static int BEGIN_STATE = 0;

	public static int COMMIT_STATE = 1;

	public static int COMMIT_FAILED_STATE = 2;

	public static int ROLLBACK_STATE = 3;

	/**
	 * Associate the current thread with the Transaction object.
	 * 
	 */
	public void begin();

	/**
	 * Complete the transaction represented by this Transaction object.
	 */
	public void commit();

	/**
	 * Rollback the transaction represented by this Transaction object.
	 */
	public void rollback();

	/**
	 * Obtain the status of the transaction associated with the current thread.
	 * 
	 * @return status of the Transaction object
	 */
	public int getStatus();

}
