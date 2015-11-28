package org.bidziil.utility;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Zoltan.Szabo
 */
public class MBeanUtil {

	private static final boolean FLAT            = false;
	private final static String  SEPARATOR       = "\n";
	private final static String  MULTILINE_IDENT = "  ";

	protected MBeanUtil() {}

	public static ObjectInstance getObjectInstance(MBeanServerConnection mbeanConn, ObjectName objn) throws MBeanException {
		if ( mbeanConn == null ) throw new NullPointerException("The 'mbeanConn' can not be null!");
		try {
			return mbeanConn.getObjectInstance(objn);
		} catch ( InstanceNotFoundException infe ) {
			throw new MBeanException(infe, infe.getMessage());
		} catch ( IOException ioe ) {
			throw new MBeanException(ioe, ioe.getMessage());
		}
	}

	public static MBeanInfo getMBeanInstance(MBeanServerConnection mbeanConn, ObjectName objn) throws MBeanException {
		if ( mbeanConn == null ) throw new NullPointerException("The 'mbeanConn' can not be null!");
		try {
			return mbeanConn.getMBeanInfo(objn);
		} catch ( InstanceNotFoundException infe ) {
			throw new MBeanException(infe);
		} catch ( ReflectionException re ) {
			throw new MBeanException(re, re.getMessage());
		} catch ( IOException ioe ) {
			throw new MBeanException(ioe, ioe.getMessage());
		} catch ( IntrospectionException ie ) {
			throw new MBeanException(ie, ie.getMessage());
		}
	}

	public static String getObjectInstanceInfo(ObjectInstance objectInst) {
		if ( objectInst == null ) return null;
		StringBuilder sb = new StringBuilder();
		sb.append("ClassName:").append(objectInst.getClassName()).append(FLAT ? " " : SEPARATOR);
		sb.append("ObjectName:").append(objectInst.getObjectName()).append(FLAT ? " " : SEPARATOR);
		return sb.toString().trim();
	}

	public static String getMBeanInstance(MBeanInfo mBeanInfo) {
		if ( mBeanInfo == null ) return null;
		StringBuilder sb = new StringBuilder();
		sb.append("ClassName:").append(mBeanInfo.getClassName()).append(FLAT ? " " : SEPARATOR);

		MBeanAttributeInfo[] attributeInfos = mBeanInfo.getAttributes();
		sb.append("Attributes:").append("").append(FLAT ? " " : SEPARATOR);
		for (MBeanAttributeInfo attributeInfo: attributeInfos) {
			sb.append(MULTILINE_IDENT)
				.append(attributeInfo.getType()).append(" ")
				.append(attributeInfo.getName()).append("; ")
				.append("// ").append(attributeInfo.getDescription())
				.append(FLAT ? " " : SEPARATOR);
			//sb.append(MULTILINE_IDENT).append(attributeInfo).append(FLAT ? " " : SEPARATOR);
		}

		MBeanOperationInfo[] operations = mBeanInfo.getOperations();
		sb.append("Operations:").append("").append(FLAT ? " " : SEPARATOR);
		for (MBeanOperationInfo operation: operations) {
			sb.append(MULTILINE_IDENT)
				.append(operation.getReturnType()).append(" ")
				.append(operation.getName()).append(" ")
				.append(Arrays.toString(operation.getSignature())).append(" ")
				.append("// ").append(operation.getDescription())
				.append(FLAT ? " " : SEPARATOR);
//			sb.append(MULTILINE_IDENT).append(operation).append(FLAT ? " " : SEPARATOR);
		}

		MBeanNotificationInfo[] notifications = mBeanInfo.getNotifications();
		sb.append("Notifications:").append("").append(FLAT ? " " : SEPARATOR);
		for (MBeanNotificationInfo notification: notifications) {
			sb.append(MULTILINE_IDENT).append(notification).append(FLAT ? " " : SEPARATOR);
		}

		return sb.toString().trim();
	}

	/**
	 * <p>Invokes an operation on an MBean.</p>
	 *
	 * @param mbeanConn
	 * @param objn The object name of the MBean on which the method is to be invoked.
	 * @param operationName The name of the operation to be invoked.
	 * @param params An array containing the parameters to be set when the operation is invoked
	 * @param signature An array containing the signature of the operation, an array of class names in the format returned by
	 * {@link Class#getName()}. The class objects will be loaded using the same class loader as the one used for loading the
	 *                     MBean on which the operation was invoked.
	 *
	 * @return The object returned by the operation, which represents
	 * the result of invoking the operation on the MBean specified.
	 *
	 * @exception MBeanException
	 */
	public static <T> T invoke(MBeanServerConnection mbeanConn, ObjectName objn, String operationName, Object params[], String signature[]) throws MBeanException {
		if ( mbeanConn == null ) throw new NullPointerException("The 'mbeanConn' can not be null!");
		try {
			return ( T ) mbeanConn.invoke(objn, operationName, params, signature);
		} catch ( MBeanException mbe ) {
			throw mbe;
		} catch ( InstanceNotFoundException infe ) {
			throw new MBeanException(infe, infe.getMessage());
		} catch ( ReflectionException re ) {
			throw new MBeanException(re, re.getMessage());
		} catch ( IOException ioe ) {
			throw new MBeanException(ioe, ioe.getMessage());
		}
	}

}
