package org.bidziil.utility;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.log4j.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CommonUtil
 */
public class CommonUtil {

	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	/** The maximum size to which the padding constant(s) can expand. </p> */
	private static final int PAD_LIMIT = 8192;

	/** A String for a space character. */
	public static final String SPACE = " ";

	/** The empty String {@code ""}. */
	public static final String EMPTY = "";

	public static final char[] WHITESPACES = new char[] {' ', '\t', '\n', '\r', '\f'};

	private static String DB_DATE_FORMAT = "yyyyMMdd";

	private static final ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat dff = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			dff.setLenient(false);
			return dff;
		}
	};

	private static final ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch ( NoSuchAlgorithmException nsae ) {
				logger.error(String.format("%s occurred due to %s", nsae.getClass().getSimpleName(), nsae.getMessage()), nsae);
			}
			return md5;
		}
	};

//	private static final ThreadLocal<XStream> xStream = new ThreadLocal<XStream>() {
//		@Override protected XStream initialValue() {
//			XStream stream = new XStream();
//			// stream.setMode(XStream.);
//			return stream;
//		}
//	};

	private static DatatypeFactory DATATYPE_FACTORY;

	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
		} catch ( DatatypeConfigurationException dce ) {
			logger.error(String.format("Error occured while create a new datatype factory instance due to %s", dce.getMessage()), dce);
		}
	}

	protected CommonUtil() {}

	public static boolean isNullOrEmpty(StringBuffer val) {
		return val == null || val.length() == 0;
	}

	public static boolean isNullOrEmpty(String val) {
		return val == null || val.isEmpty();
	}

	public static boolean isNullOrEmpty(Collection<?> val) {
		return val == null || val.isEmpty();
	}

	public static boolean isNullOrEmpty(Iterator<?> val) {
		return val == null || !val.hasNext();
	}

	public static boolean isNullOrEmpty(Object[] val) {
		return val == null || val.length == 0;
	}

	public static boolean isNullOrEmpty(Number val) {
		return val == null;
	}

	/** true, ha null vagy üres, egyébként false */
	public static boolean isNullOrEmpty(Map<?, ?> val) {
		return val == null || val.isEmpty();
	}

	public static boolean isNullOrZero(Number val) {
		return val == null || val.equals(0);
	}

	public static String nullToNull(Number value){
		return ( value != null ) ? String.valueOf(value) : null;
	}

	public static boolean isNullOrEmpty(Object val) {
		if (val instanceof String) {
			return isNullOrEmpty((String) val);

		} else if (val instanceof StringBuffer) {
			return isNullOrEmpty((StringBuffer) val);

		} else if (val instanceof Collection) {
			return isNullOrEmpty((Collection<?>) val);

		} else if (val instanceof Iterator) {
			return isNullOrEmpty((Iterator<?>) val);

		} else if (val instanceof Object[]) {
			return isNullOrEmpty((Object[]) val);

		} else if (val instanceof Number) {
			return isNullOrEmpty((Number) val);

		} else if (val instanceof Map) {
			return isNullOrEmpty((Map<?, ?>) val);

		} else {
			return null == val;
		}
	}

	public static String nullToEmpty(String value){
		return (value != null) ? value : "";
	}

	public static String nullToEmpty(Number value){
		return (value != null) ? value.toString() : "";
	}

	public static String emptyToNBSP(String value){
		return !isNullOrEmpty(value) ? value : "&nbsp;";
	}

	public static String reduceWhiteSpace(String value){
		return nullToEmpty(value).trim().replaceAll("\\s+", " ");
	}
	
	public static Boolean nullToFalse(Boolean value){
		return (value != null) ? value : Boolean.FALSE;
	}
	
	public static boolean nullCheck(Boolean b) {
		return b != null ? b : false;
	}

	public static Integer toInt(String value){
		Integer result = 0;
		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException nfe) { }
		return result;
	}

	public static Long toLong(String value){
		Long result = 0L;
		try {
			result = Long.parseLong(value);
		} catch (NumberFormatException nfe) { }
		return result;
	}
	
	public static Long stringToLong(String s) {
		return ( s != null ) ? Long.parseLong(s) : null;
	}

	public static String longToString(Long l) {
		return ( l != null ) ? l.toString() : null;
	}
	
	public static Integer max(Integer a, Integer b){
		if ( a == null ){
			return b;
		} else if ( b == null ){
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	public static Integer min(Integer a, Integer b){
		if ( a == null ){
			return b;
		} else if ( b == null ){
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	public static Long max(Long a, Long b){
		if ( a == null ){
			return b;
		} else if ( b == null ){
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	public static Long min(Long a, Long b){
		if ( a == null ){
			return b;
		} else if ( b == null ){
			return a;
		} else {
			return Math.min(a, b);
		}
	}

	/**
	 * <p>
	 * Checks if a CharSequence is empty ("") or null.
	 * </p>
	 *
	 * <pre>
	 * GWTUtils.isEmpty(null)      = true
	 * GWTUtils.isEmpty("")        = true
	 * GWTUtils.isEmpty(" ")       = false
	 * GWTUtils.isEmpty("bob")     = false
	 * GWTUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * CharSequence. That functionality is available in isBlank().
	 * </p>
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * <pre>
	 * GWTUtils.concat(null)         = "";
	 * GWTUtils.concat("abc")        = "abc";
	 * GWTUtils.concat("abc", "abc") = "abcabc"
	 * GWTUtils.concat("abc", null)  = "abc"
	 * </pre>
	 **/
	public static String concat(CharSequence... strs) {
		return concat(EMPTY, strs);
	}

	public static String concat(String filler, CharSequence... strs) {
		if ( strs == null || strs.length == 0) { return EMPTY; }
		if ( filler == null ) filler = EMPTY;
		final int inputLength = strs.length;
		switch (inputLength) {
			case 1:
				return ( strs[0] != null ) ? StringUtils.trim(strs[0].toString()) : EMPTY;
			default:
				final StringBuilder buf = new StringBuilder();
				for ( int index = 0; index < inputLength; index++ ) {
					if ( isEmpty(strs[index]) ) continue;
					buf.append(StringUtils.trim(strs[index].toString())).append(( index < ( inputLength - 1 ) ) ? filler : EMPTY);
				}
				return StringUtils.trim(buf.toString());
		}
	}

	public static String concat(String filler, String[] strs, int from, int to) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = from; i < to; i++) {
			stringBuilder.append(strs[i]);
			if (i < to-1) {
				stringBuilder.append(filler);
			}
		}
		return stringBuilder.toString();
	}

	
	public static String[] addArrays(String[] a, String[] b){
		List<String> l = new ArrayList<String>();
		l.addAll(CommonUtil.asList(a));
		l.addAll(CommonUtil.asList(b));
		return l.toArray(a);
	}

	/**
	 * http://www.codingforums.com/java-and-jsp/147387-merge-csv-files-specified-folder-using-java.html
	 **/
	public static byte[] concatByteArrays(Collection<byte[]> csvs) {
		if ( csvs == null || csvs.isEmpty() ) return new byte[0];

		byte[] result = new byte[calcLengthOfResult(csvs)];
		Iterator<byte[]> it = csvs.iterator();
		for ( int index = 0, length = csvs.size(), offset = 0; it.hasNext(); index++ ) {
			byte[] csv = it.next();
			if (csv == null || csv.length == 0) continue;
			System.arraycopy(csv, 0, result, offset, csv.length);
			offset += csv.length;
		}

		return result;
	}

	private static int calcLengthOfResult(Collection<byte[]> csvs) {
		int length = 0;
		Iterator<byte[]> it = csvs.iterator();
		while ( it.hasNext() ) {
			byte[] csv = it.next();
			if (csv == null || csv.length == 0) continue;
			length += csv.length;
		}
		return length;
	}

	public static void addToSet(List<Long> ids, Set<Long> set) {
		if ( ids != null ){
			for ( long l : ids ){
				set.add(l);
			}
		}
	}

	public static <T> List<T> asList(Set<T> set) {
		if ( set == null ){ return new ArrayList<T>(); }
		List<T> result = new ArrayList<T>();
		for ( T t : set ) {
			result.add(t);
		}
		return result;
	}

	public static <T> List<T> asList(T[] array) {
		if ( array == null ){ return new ArrayList<T>(); }
		return Arrays.asList(array);
	}

	public static <T extends Enum<T>> EnumSet<T> asEnumSet(T[] array) {
		return EnumSet.<T>copyOf(Arrays.asList(array));
	}

	public static <T> boolean contains(final T[] array, final T v) {
		for ( final T e : array )
			if ( e == v || v != null && v.equals(e) )
				return true;
		return false;
	}

	/**
	 * Format a date object.
	 *
	 * @param date the date object being formatted
	 * @return string representation for this date in desired format
	 * @throws IllegalArgumentException if the specified pattern could not be parsed
	 */
	public static String formatDate(String pattern, Date date) {
		if ( date == null ) return "";
		SimpleDateFormat sdf = new SimpleDateFormat((pattern != null && !pattern.isEmpty()) ? pattern : DB_DATE_FORMAT);
		return sdf.format(date);
	}

	/**
	 * Format a date object.
	 *
	 * @param date the date object being formatted
	 * @return string representation for this date in desired format
	 */
	public static String formatDate(Date date) {
		if ( date == null ) return "";
		SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT);
		return sdf.format(date);
	}

	/**
	 * http://www.mkyong.com/java/java-md5-hashing-example/
	 * */
	public static String hash(String str) throws IOException {
		InputStream is = null;
		StringBuffer sb = null;
		try {
			MessageDigest md = MD5.get();
			is = new ByteArrayInputStream(str.getBytes());
			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();

			sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}

		} catch (IOException ioe) {
			logger.error(String.format("%s occurred due to %s", ioe.getClass().getSimpleName(), ioe.getMessage()), ioe);
			throw ioe;
		}
		return ( sb != null ) ? sb.toString() : null;
	}

	public static String testDateFormat(Date d){
		if ( d == null ) {
			return "n/a";
		}
		return df.get().format(d);
	}

	/**
	 * is all in set
	 * @param enumClass
	 * @param enumerationSet
	 * @param enumeration
	 * @return result
	 **/
	public static <T extends Enum<T>> boolean isAllInSet(Class<T> enumClass, EnumSet<T> enumerationSet, T... enumeration) {
		if ( enumerationSet == null ) return false;
		if ( enumeration == null || enumeration.length == 0 ) { return enumerationSet.isEmpty(); }

		EnumSet<T> toAdd = EnumSet.<T>noneOf(enumClass);
		EnumSet<T> toDel = EnumSet.<T>noneOf(enumClass);
		symmetricDifference(enumerationSet, asEnumSet(enumeration), toAdd, toDel);

		return toAdd.isEmpty();
	}

	/**
	 * is any in set
	 * @param enumClass
	 * @param enumerationSet
	 * @param enumeration
	 * @return result
	 **/
	public static <T extends Enum<T>> boolean isAnyInSet(Class<T> enumClass, EnumSet<T> enumerationSet, T... enumeration) {
		if ( enumerationSet == null ) return false;
		if ( enumeration == null || enumeration.length == 0 ) { return enumerationSet.isEmpty(); }

		for ( T e : enumeration ) {
			if ( enumerationSet.contains(e) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculates the symmetric difference to create the destination set from the source set.
	 * If any of the sets is null an exception will be thrown.
	 *
	 * @param <T> the type of the records that the sets contain
	 * @param src input, source set
	 * @param dst input, destination set
	 * @param toAdd output, the records to add
	 * @param toDel output, the records to remove
	 **/
	public static <T> void symmetricDifference(Collection<T> src, Collection<T> dst, Collection<T> toAdd, Collection<T> toDel) {
		if(src == null) throw new IllegalArgumentException("The 'src' parameter cannot be null");
		if(dst == null) throw new IllegalArgumentException("The 'dst' parameter cannot be null");
		if(toAdd == null) throw new IllegalArgumentException("The 'add' parameter cannot be null");
		if(toDel == null) throw new IllegalArgumentException("The 'del' parameter cannot be null");

		toAdd.clear();
		toDel.clear();

		// compare for addition
		for(T record : dst) {
			if( !src.contains( record ) ) { toAdd.add(record); }
		}

		// compare for removal
		for( T record : src ) {
			if( !dst.contains( record ) ) { toDel.add(record); }
		}
	}


	/**
	 * Calculates the symmetric difference to create the destination set from the source set.
	 * If any of the sets is null an exception will be thrown.
	 *
	 * @param <T> the type of the records that the sets contain
	 * @param src input, source set
	 * @param dst input, destination set
	 * @param toAdd output, the records to add
	 * @param toDel output, the records to remove
	 **/
	public static <T> void symmetricDifference(Set<T> src, Set<T> dst, Set<T> toAdd, Set<T> toDel) {
		symmetricDifference(( Collection ) src, ( Collection ) dst, ( Collection ) toAdd, ( Collection ) toDel);
	}

	/**
	 * Calculates the symmetric difference to create the destination array from the source array.
	 * If any of the arrays is null an exception will be thrown.
	 *
	 * @param <T> the type of the records that the lists contain
	 * @param src input, source list
	 * @param dst input, destination list
	 * @param toAdd output, the records to add
	 * @param toDel output, the records to remove
	 *
	 **/
	public static <T> void symmetricDifference(List<T> src, List<T> dst, List<T> toAdd, List<T> toDel) {
		symmetricDifference(( Collection ) src, ( Collection ) dst, ( Collection ) toAdd, ( Collection ) toDel);
	}

	public static <T> Collection<T> union(Collection<T> a, Collection<T> b) {
		Set<T> union = new HashSet<T>();
		if ( a != null ) union.addAll(a);
		if ( b != null ) union.addAll(b);
		return new ArrayList<T>(union);
	}

	public static <T> Collection<T> intersection(Collection<T> a, Collection<T> b) {
		Set<T> intersection = new HashSet<T>();
		if ( a != null ) intersection.addAll(a);
		if ( b != null ) intersection.retainAll(b);
		return new ArrayList<T>(intersection);
	}

	public static <K, V> Map<K, V> intersection(Map<K, V> a, Map<K, V> b) {
		Collection<K> intersection = CommonUtil.intersection(( a != null ) ? a.keySet() : null, ( b != null ) ? b.keySet() : null);

		Map<K, V> intersectionMap = new HashMap<K, V>();
		for ( K k : intersection ) {
			if ( a != null && a.containsKey(k) ) {
				intersectionMap.put(k, a.get(k));
			} else if ( b != null && b.containsKey(k) ) {
				intersectionMap.put(k, b.get(k));
			}
		}
		return intersectionMap;
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c, Comparator<? super T> comparator) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list, comparator);
		return list;
	}

	public static String getStackTrace(Throwable t) {
		String stackTrace = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			stackTrace = sw.getBuffer().toString();
		} catch (Exception ex) {
		} finally {
			if (pw != null) try { pw.close(); } catch (Exception e) { }
			if (sw != null) try { sw.close(); } catch (Exception e) { }
		}
		return stackTrace;
	}

	public static StackTraceElement[] getStackTrace(long depthOfStackTrace) {
		List<StackTraceElement> stes = new ArrayList<StackTraceElement>();

		try {
			throw new RuntimeException();
		} catch (RuntimeException e) {
			StackTraceElement[] stackTrace = e.getStackTrace();

			for ( int i = 1; i < stackTrace.length && i < (depthOfStackTrace + 1); i++ ) {
				StackTraceElement stackTraceElement = stackTrace[i];

				if ( stackTraceElement != null ) {
					stes.add(stackTraceElement);
				}
			}
		}
		return stes.toArray(new StackTraceElement[stes.size()]);
	}
	
	/**
	 * getStackTraceElementAsString
	 *
	 * @param ste - StackTraceElement
	 * @return String
	 */
	public static String getStackTraceElementAsString(StackTraceElement ste) {
		return String.format("%s.%s:%s", ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
	}

//	/**
//	 * Serialize an object to a pretty-printed XML String.
//	 * @throws com.thoughtworks.xstream.XStreamException if the object cannot be serialized
//	 */
//	public static String toXML(Object src) {
//		return xStream.get().toXML(src);
//	}

	/**
	 * Append a get parameter with given paramId and paramValue
	 * <p>
	 * example:
	 * String url = this.appendGetParameter("http://example.com/action", "paramId", "paramValue")
	 * <br/>
	 * url -> "http://example.com/action?paramId=paramValue"
	 * </p>
	 * <p>
	 * example :
	 * String url = this.appendGetParameter("http://example.com/action?id=10", "paramId", "paramValue")
	 * <br/>
	 * url -> "http://example.com/action?id=10&paramId=paramValue"
	 * </p>
	 *
	 * @param url
	 * @param paramId
	 * @param paramValue
	 * @return String
	 */
	public static String appendGetParameter(String url, String paramId, String paramValue) {
		if ( paramId == null || paramId.isEmpty() || paramValue == null || paramValue.isEmpty() ) return url;
		// if ( !url.endsWith("/") ) { sb.append("/"); }
		return new StringBuilder(url).append((url.indexOf("?") > 0) ? "&" : "?").append(paramId).append("=").append(paramValue).toString();
	}

	public static <T> T tryNewDTO(Class<T> dtoClass, Long id) {
		if ( id == null ) { return null; }
		T o = null;
		try {
			o = tryNewInstance(dtoClass);
			tryToInvokeMethod(o, "setId", id);
		} catch (NullPointerException npe) {}
		return o;
	}

	/**
	 * @param klass
	 * @param methodName get method with this name
	 * @param parameters
	 **/
	public static <T> Method tryToGetMethod(Class<T> klass, String methodName, Object... parameters) {
		return tryToGetMethod(klass, methodName, objectArrayToTypesArray(parameters));
	}

	/**
	 * @param klass
	 * @param methodName get method with this name
	 * @param parameterTypes
	 **/
	public static <T> Method tryToGetMethod(Class<T> klass, String methodName, Class<?>... parameterTypes) {
		if ( klass == null ) throw new NullPointerException("The 'klass' can not be null!");
		if ( methodName == null || methodName.isEmpty() ) throw new NullPointerException("The 'methodName' can not be null or empty!");
		if ( parameterTypes == null ) { parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY; }

		Method method = null;
		try {
			method = klass.getMethod(methodName, parameterTypes);
		} catch ( NoSuchMethodException nsme ) {
			logger.error(String.format("%s occurred while get method due to %s", nsme.getClass().getSimpleName(), nsme.getMessage()), nsme);
		}
		return method;
	}

	/**
	 * @param array
	 **/
	public static Class<?>[] objectArrayToTypesArray(Object... array) {
		if ( array == null ) { array = ArrayUtils.EMPTY_OBJECT_ARRAY; }

		Class<?>[] parameterTypes = new Class<?>[array.length];
		for ( int i = 0, length = array.length; i < length; i++ ) {
			if ( array[i] == null ) throw new NullPointerException("The parameters element can not be null or empty!");
			Object object = array[i];
			parameterTypes[i] = ( object != null ) ? object.getClass() : null;
		}

		return parameterTypes;
	}

	public static <T> T tryNewInstance(final Class<T> klass, Object... args){
		T object = null;
		if ( klass == null ) throw new NullPointerException("The 'klass' can not be null!");
		try {
			object = ConstructorUtils.<T>invokeConstructor(klass, args);
		} catch ( NoSuchMethodException | IllegalAccessException | InstantiationException e ) {
			logger.error(String.format("%s occurred due to %s", e.getClass().getSimpleName(), e.getMessage()), e);
		} catch ( InvocationTargetException ite ) {
			Throwable t = (InvocationTargetException.class.equals(ite.getClass()) && ite.getCause() != null) ? ite.getCause() : ite;
			logger.error(String.format("%s occurred due to %s", t.getClass().getSimpleName(), t.getMessage()), t);
		}
		return object;
	}

	/**
	 * tryNewInstance
	 * @return T
	 **/
	@SuppressWarnings("unchecked")
	public static <T> T tryNewInstance(Class<T> klass) {
		T object = null;
		if ( klass == null ) throw new NullPointerException("The 'klass' can not be null!");
		try {
			object = ConstructorUtils.invokeConstructor(klass);
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
			logger.error(String.format("%s occurred due to %s", e.getClass().getSimpleName(), e.getMessage()), e);
		} catch (InvocationTargetException ite) {
			Throwable targetException = (InvocationTargetException.class.equals(ite.getClass()) && ite.getCause() != null) ? ite.getCause() : ite;
			logger.error(String.format("%s occurred due to %s", targetException.getClass().getSimpleName(), targetException.getMessage()), targetException);
		}
		return (T) object;
	}

	/**
	 * newInstanceIfNull
	 * @return T
	 **/
	@SuppressWarnings({"unchecked" })
	public static <T> T newInstanceIfNull(Object object, Class<T> klass) {
		if ( object == null ) {
			object = tryNewInstance(klass);
		}
		return (T) object;
	}

	/**
	 * tryToCallMethod
	 * @param target
	 * @param methodName get method with this name
	 * @param parameters
	 * @return RV
	 **/
	@SuppressWarnings("unchecked")
	public static <RV, T> RV tryToInvokeMethod(T target, String methodName, Object... parameters) {
		if ( target == null ) throw new NullPointerException("The 'target' can not be null!");
		if ( methodName == null || methodName.isEmpty() ) throw new NullPointerException("The 'methodName' can not be null or empty!");
		if ( parameters == null ) { parameters = ArrayUtils.EMPTY_OBJECT_ARRAY; }

		Method method = tryToGetMethod(target.getClass(), methodName, parameters);
		if ( method == null ) return null;

		Object rv = null;
		try {
			rv = method.invoke(target, parameters);
		} catch (IllegalAccessException e) {
			logger.error(String.format("%s occurred due to %s", e.getClass().getSimpleName(), e.getMessage()), e);
		} catch (InvocationTargetException ite) {
			Throwable targetException = (InvocationTargetException.class.equals(ite.getClass()) && ite.getCause() != null) ? ite.getCause() : ite;
			logger.error(String.format("%s occurred due to %s", targetException.getClass().getSimpleName(), targetException.getMessage()), targetException);
		}

		return (RV) rv;
	}
}
