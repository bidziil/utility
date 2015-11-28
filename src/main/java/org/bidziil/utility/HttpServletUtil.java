package org.bidziil.utility;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HttpServletUtil
 *
 * @author szabo.zoltan
 * */
public class HttpServletUtil {

	private final static String SEPARATOR       = "\n";
	private final static String MULTILINE_IDENT = "  ";
	private final static int    MAX_DEPTH       = 10;

	private final static Set<String> attributeFilter = new HashSet<String>(CommonUtil.asList(new String[] {
	  "weblogic",
	  "weblogic.servlet"
	}));

	// the list of semi primitive types (no need for cloning)
	private final static List<Class<?>> semiPrimitves = CommonUtil.asList(new Class<?>[] {
	  boolean.class,
	  Boolean.class,
	  byte.class,
	  Byte.class,
	  char.class,
	  Character.class,
	  short.class,
	  Short.class,
	  int.class,
	  Integer.class,
	  long.class,
	  Long.class,
	  float.class,
	  Float.class,
	  double.class,
	  Double.class,
	  Enum.class // enums are treated as primitives some cases
	});

	private final static List<Class<?>> useToStringList = CommonUtil.asList(new Class<?>[] {
	  java.util.Date.class,
	  java.io.File.class,
	  java.lang.StringBuilder.class,
	  java.lang.StringBuffer.class,
	  javax.xml.datatype.XMLGregorianCalendar.class
	});

	/** dumpApplicationAttributes */
	public static String getApplicationAttributesAsString(ServletContext servletContext) {
		if ( servletContext == null ) throw new RuntimeException("The 'servletContext' parameter can not be null.");

		Enumeration<?> e = servletContext.getAttributeNames();
		StringBuffer sb = new StringBuffer();

		while ( e.hasMoreElements() ) {
			String name = ( String ) e.nextElement();
			if ( isFilteredAttribute(name) ) continue;
			sb.append(SEPARATOR).append("* ").append(name).append(" - ");
			appendAttributeToString(servletContext.getAttribute(name), sb, 1);
		}
		return sb.toString();
	}

	/** dumpSessionAttributes */
	public static String getSessionAttributesAsString(HttpSession session) {
		if ( session == null ) throw new RuntimeException("The 'session' parameter can not be null.");

		Enumeration<?> e = session.getAttributeNames();
		StringBuffer sb = new StringBuffer();

		while ( e.hasMoreElements() ) {
			String name = (String) e.nextElement();
			if (isFilteredAttribute(name)) continue;
			sb.append(SEPARATOR).append("* ").append(name).append(" - ");
			appendAttributeToString(session.getAttribute(name), sb, 1);
		}
		return sb.toString();
	}

	/** dumpRequestAttributes */
	public static String getRequestAttributesAsString(HttpServletRequest request) {
		if ( request == null ) throw new RuntimeException("The 'request' parameter can not be null.");

		Enumeration<?> e = request.getAttributeNames();
		StringBuffer sb = new StringBuffer();

		while ( e.hasMoreElements() ) {
			String name = (String) e.nextElement();
			if (isFilteredAttribute(name)) continue;
			sb.append(SEPARATOR).append("* ").append(name).append(" - ");
			// Get the value of the attribute
			appendAttributeToString(request.getAttribute(name), sb, 1);
		}
		return sb.toString();
	}

	/** getObjectAsString */
	public static String getObjectAsString(Object o) {
		return appendAttributeToString(o, new StringBuffer(), 1).toString();
	}

	/** appendAttributeToString */
	private static StringBuffer appendAttributeToString(Object attribute, StringBuffer sb, int depth) {
		if ( attribute == null ) return sb.append("null");
		Class<?> klass = attribute.getClass();
		if ( depth > MAX_DEPTH ) { return sb.append(removeNewLine(String.valueOf(attribute))).append(" class=").append(klass.getSimpleName()); }

		if ( klass.isArray() ) {
			sb.append(Array.class.getSimpleName()).append(", size = ").append(Array.getLength(attribute));
			for ( int i = 0, length = Array.getLength(attribute); i < length; i++ ) {
				sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth));
				appendKeyValuePairtoString(i, Array.get(attribute, i) , sb, depth + 1);
			}
		} else if(attribute instanceof Dictionary<?,?>) {
			sb.append(attribute.getClass().getSimpleName()).append(", size = ").append(((Dictionary<?,?>) attribute).size());
			Enumeration<?> e = ((Dictionary<?, ?>) attribute).keys();
			while ( e.hasMoreElements() ) {
				Object key = e.nextElement();
				sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth));
				appendKeyValuePairtoString(key, ((Dictionary<?, ?>) attribute).get(key), sb, depth + 1);
			}
		} else if ( attribute instanceof Collection ) {
			sb.append(attribute.getClass().getSimpleName()).append(", size = ").append(((Collection<?>) attribute).size());
			for ( Object element : (Collection<?>) attribute ) {
				sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth));
				appendAttributeToString(element, sb, depth + 1);
			}
		} else if(attribute instanceof Iterable<?>) {
			Iterable<?> iterable = (Iterable<?>)attribute;
			// determining length
			int size = 0;
			Iterator<?> it = iterable.iterator();
			while ( it.hasNext() ) { it.next(); size++; }

			sb.append(attribute.getClass().getSimpleName()).append(", size = ").append(size);
			for ( Object item : iterable ) {
				sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth));
				appendAttributeToString(item, sb, depth + 1);
			}
		} else if ( attribute instanceof Map<?,?> ) {
			sb.append(attribute.getClass().getSimpleName()).append(", size = ").append(((Map<?, ?>) attribute).size());
			for ( Map.Entry<?, ?> entry : ((Map<?, ?>) attribute).entrySet() ) {
				sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth));
				appendKeyValuePairtoString(entry.getKey(), entry.getValue(), sb, depth + 1);
			}
		} else {
			appendObjectToString(attribute, sb, depth + 1);
		}
		return sb;
	}

	private static StringBuffer appendKeyValuePairtoString(Object key, Object value, StringBuffer sb, int depth) {
		sb.append('[');
		appendAttributeToString(key, sb, depth);
		sb.append("] -> ");
		appendAttributeToString(value, sb, depth);
		return sb;
	}

	private static StringBuffer appendObjectToString(Object o, StringBuffer sb, int depth) {
		if ( o == null ) return sb.append("null");
		Class<?> klass = o.getClass();

		Field[] fields = klass.getFields();
		Arrays.sort(fields, new Comparator<Field>() {
			@Override public int compare(Field o1, Field o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		List<Field> toPrint = new ArrayList<Field>();
		for ( Field field : fields ) {
			if ( (field.getModifiers() & Modifier.STATIC) == 0 ) { toPrint.add(field); }
		}

		try {
			if ( klass.equals(Character.class) || klass.equals(char.class) ) {
				sb.append('\'').append(o).append('\'').append(" class=").append(klass.getSimpleName());
			} else if ( o instanceof CharSequence ) {
				sb.append('\"').append(removeNewLine(CharSequence.class.cast(o).toString())).append('\"').append(" class=").append(klass.getSimpleName());
			} else if(canUseToString(klass)) {
				sb.append(removeNewLine(String.valueOf(o))).append(" class=").append(klass.getSimpleName());
			} else if ( toPrint.size() == 0 ) {
				sb.append("class=").append(klass.getSimpleName()).append(" {");
				BeanInfo beanInfo = Introspector.getBeanInfo(klass);
				int size = 0;
				for(PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
					String name = pd.getDisplayName();
					if(	"class".equals(name) || "declaringClass".equals(name) || pd.getReadMethod() == null) continue;
					Object value = pd.getReadMethod().invoke(o);
					sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth)).append(pd.getDisplayName());
					sb.append(" = ");
					appendAttributeToString(value, sb, depth + 1);
					size++;
				}
				sb.append((size > 0 ? SEPARATOR : "")).append((size > 0 ? getMultilineIdentByDepth(depth - 1) : "")).append("}");
			} else {
				sb.append("class=").append(klass.getSimpleName()).append(" {");
				int size = 0;
				for ( Field f : toPrint ) {
					Object value = f.get(o);
					sb.append(SEPARATOR).append(getMultilineIdentByDepth(depth)).append(f.getName());
					sb.append(" = ");
					appendAttributeToString(value, sb, depth + 1);
					size++;
				}
				sb.append((size > 0 ? SEPARATOR : "")).append((size > 0 ? getMultilineIdentByDepth(depth - 1) : "")).append("}");
			}
		} catch (Exception e) {
			sb.append("An error occured due to call ").append(klass.getSimpleName()).append(".toString() Exception:").append(e.getClass().getSimpleName()).append(" ").append(e.getMessage());
		}
		return sb;
	}

	private static boolean canUseToString(Class<?> klass) {
		return isAssignableFromAnyOf(klass, semiPrimitves) || isAssignableFromAnyOf(klass, useToStringList);
	}

	private static boolean isAssignableFromAnyOf(Class<?> klass, List<Class<?>> klasses) {
		for ( Class<?> spcls : klasses ) {
			if ( spcls.isAssignableFrom(klass) ) { return true; }
		}
		return false;
	}

	private static String getMultilineIdentByDepth(int depth) {
		StringBuffer multilineIdent = new StringBuffer("");
		for ( int i = 0; i < depth; i++ ) {
			multilineIdent.append(MULTILINE_IDENT);
		}
		return multilineIdent.toString();
	}

	private static String removeNewLine(String string) {
		return string.replaceAll("\n", " ").replaceAll("\r", " ");
	}

	private static boolean isFilteredAttribute(String attributeName) {
		if ( attributeName == null || attributeName.isEmpty() ) return false;
		for ( String filter : attributeFilter ) {
			if ( attributeName.startsWith(filter) ) return true;
		}
		return false;
	}

	/** getSessionAttribute */
	@SuppressWarnings("unchecked")
	public static <T> T getApplicationAttribute(ServletContext servletContext, String key, Class<T> clazz) {
		return (T) getApplicationAttribute(servletContext, key);
	}

	/** getApplicationAttribute */
	public static Object getApplicationAttribute(ServletContext servletContext, String key) {
		if ( servletContext == null ) throw new NullPointerException("The 'servletContext' can not be null");
		return servletContext.getAttribute(key);
	}

	/** setApplicationAttribute */
	public static void setApplicationAttribute(ServletContext servletContext, String key, Object attribute) {
		if ( servletContext == null ) throw new NullPointerException("The 'servletContext' can not be null");
		servletContext.setAttribute(key, attribute);
	}

	/** getSessionAttribute */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(HttpSession session, String key, Class<T> clazz) {
		return (T) getSessionAttribute(session, key);
	}

	/** getSessionAttribute */
	public static Object getSessionAttribute(HttpSession session, String key) {
		if ( session == null ) throw new NullPointerException("The 'session' can not be null");
		return session.getAttribute(key);
	}

	/** setSessionAttribute */
	public static void setSessionAttribute(HttpSession session, String key, Object attribute) {
		if ( session == null ) throw new NullPointerException("The 'session' can not be null");
		session.setAttribute(key, attribute);
	}

	/** getRequestAttribute */
	@SuppressWarnings("unchecked")
	public static <T> T getRequestAttribute(HttpServletRequest request, String key, Class<T> clazz) {
		return (T) getRequestAttribute(request, key);
	}

	/** getRequestAttribute */
	public static Object getRequestAttribute(HttpServletRequest request, String key) {
		if ( request == null ) throw new NullPointerException("The 'request' can not be null");
		return request.getAttribute(key);
	}

	/** setRequestAttribute */
	public static void setRequestAttribute(HttpServletRequest request, String key, Object attribute) {
		if ( request == null ) throw new NullPointerException("The 'request' can not be null");
		request.setAttribute(key, attribute);
	}

	// ///////////////////////////////////////////
	// Helper Methods
	// ///////////////////////////////////////////

	/** Gets action from request attributes or request parameters */
	public static <T> T getParameter(HttpServletRequest httpReq, String paramName, Class<T> klass) {
		return getParameter(httpReq, paramName, klass, false);
	}

	/** Gets action from request attributes or request parameters */
	@SuppressWarnings("unchecked")
	public static <T> T getParameter(HttpServletRequest httpReq, String paramName, Class<T> klass, boolean removeParam) {
		Object paramValue = httpReq.getAttribute(paramName);
		if ( paramValue == null ) {
			paramValue = httpReq.getParameter(paramName);
		} else {
			if ( removeParam ) httpReq.removeAttribute(paramName);
		}
		return (T) paramValue;
	}

	/** Gets the requested page */
	public static String getRequestedPage(HttpServletRequest httpReq) {
		String url = httpReq.getRequestURI();
		int firstSlash = url.indexOf("/", 1);
		String requestedPage = null;
		if ( firstSlash != -1 ) requestedPage = url.substring(firstSlash + 1, url.length());
		return requestedPage;
	}

	/** Gets the action from the request */
	public static String getRequestAction(HttpServletRequest httpReq) {
		return httpReq != null ? httpReq.getRequestURI().substring(httpReq.getRequestURI().lastIndexOf("/") + 1) : null;
	}

	/** Removes url parameter from url */
	public static String removeUrlParameterFromUrl(String url, String parameterName) {
		int startIndex = url.indexOf(parameterName);
		if ( startIndex != -1 ) {
			url = url.substring(0, startIndex) + url.substring(Math.min(startIndex + parameterName.length() + 26, url.length()), url.length());
			if ( url.endsWith("?") || url.endsWith("&") ) url = url.substring(0, url.length() - 1);
		}
		return url;
	}
}
