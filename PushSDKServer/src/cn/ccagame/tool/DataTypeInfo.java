package cn.ccagame.tool;


public class DataTypeInfo{
	public static final int INTEGER_TYPE = 2;
	public static final int LONG_TYPE = 8;	
	public static final int STRING_TYPE = 4;
	public static final int OBJECT_TYPE = 9;	
	public static final int DOUBLE_TYPE = 9;	
	public static final int SET_TYPE = 10;		
	public static final boolean NO_NULL = true;
	public static final boolean SYS_VALUE = true;	
	public static final boolean CAN_NULL = false;	
	public static final String UNI_RULE = "UNI_RULE";	
	
	public int dataType;
	public boolean noNull;
	public Object defaultValue;
	String checkRule;
	public DataTypeInfo(int dataType,boolean canBeNull,Object... defaultValue){
		this.dataType = dataType;
		this.noNull = canBeNull;
		this.defaultValue = (defaultValue.length==0)?null:defaultValue[0];
	}
	
	public DataTypeInfo(int dataType,boolean canBeNull,String checkRule,Object... defaultValue){
		this.dataType = dataType;
		this.noNull = canBeNull;
		this.checkRule = checkRule;
		this.defaultValue = (defaultValue.length==0)?null:defaultValue[0];
	}
}
