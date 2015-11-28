package org.bidziil.utility;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConvertUtil {

	private static Logger          logger;
	private static DatatypeFactory DATATYPE_FACTORY;
	
	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
			logger = Logger.getLogger(ConvertUtil.class);
		} catch ( DatatypeConfigurationException dce ) {
			logger.error(String.format("Error occurred while create a new datatype factory instance due to %s", dce.getMessage()), dce);
		} catch ( Throwable t ) {
			logger.error(String.format("Error occurred while initialize ConvertUtil due to %s", t.getMessage()), t);
		}
	}

	private final static Map<Character, Character> hungarianCharacterMap = new HashMap<Character, Character>();
	static {
		hungarianCharacterMap.put(new Character('á'), new Character('a'));
		hungarianCharacterMap.put(new Character('é'), new Character('e'));
		hungarianCharacterMap.put(new Character('í'), new Character('i'));
		hungarianCharacterMap.put(new Character('ó'), new Character('o'));
		hungarianCharacterMap.put(new Character('ö'), new Character('o'));
		hungarianCharacterMap.put(new Character('ő'), new Character('o'));
		hungarianCharacterMap.put(new Character('ú'), new Character('u'));
		hungarianCharacterMap.put(new Character('ü'), new Character('u'));
		hungarianCharacterMap.put(new Character('ű'), new Character('u'));
		hungarianCharacterMap.put(new Character('Á'), new Character('A'));
		hungarianCharacterMap.put(new Character('É'), new Character('E'));
		hungarianCharacterMap.put(new Character('Í'), new Character('I'));
		hungarianCharacterMap.put(new Character('Ó'), new Character('O'));
		hungarianCharacterMap.put(new Character('Ö'), new Character('O'));
		hungarianCharacterMap.put(new Character('Ő'), new Character('O'));
		hungarianCharacterMap.put(new Character('Ú'), new Character('U'));
		hungarianCharacterMap.put(new Character('Ü'), new Character('U'));
		hungarianCharacterMap.put(new Character('Ű'), new Character('U'));
	}

	private final static ArrayList<String> separator = new ArrayList<String>();
	static {
		separator.add("");
		separator.add("ezer");
		separator.add("millió");
		separator.add("milliárd");
		separator.add("billió");
		separator.add("billiárd");
		separator.add("trillió");
		separator.add("trilliárd");
	}

	private final static ArrayList<String> egyes = new ArrayList<String>();
	static {
		egyes.add("");
		egyes.add("egy");
		egyes.add("kettő");
		egyes.add("három");
		egyes.add("négy");
		egyes.add("öt");
		egyes.add("hat");
		egyes.add("hét");
		egyes.add("nyolc");
		egyes.add("kilenc");
	}

	private final static ArrayList<String> tizes = new ArrayList<String>();
	static {
		tizes.add("");
		tizes.add("tíz");
		tizes.add("húsz");
		tizes.add("harminc");
		tizes.add("negyven");
		tizes.add("ötven");
		tizes.add("hatvan");
		tizes.add("hetven");
		tizes.add("nyolcvan");
		tizes.add("kilencven");
	}

	private final static ArrayList<String> tizen = new ArrayList<String>();
	static {
		tizen.add("");
		tizen.add("tizen");
		tizen.add("huszon");
		tizen.add("harminc");
		tizen.add("negyven");
		tizen.add("ötven");
		tizen.add("hatvan");
		tizen.add("hetven");
		tizen.add("nyolcvan");
		tizen.add("kilencven");

	}

	public static String convertAmountToText(Long amount) {

		boolean moreThan2000 = amount > 2000l;//kétezer alatt nem írunk - jelet

		ArrayList<String> splited = splitToThree(Long.toString(amount));

		StringBuffer text = new StringBuffer();

		for ( int i = 0; i < splited.size(); i++ ) {
			if ( i != 0 && i < splited.size() && moreThan2000 && !"000".equals(splited.get(i)) ) {
				text.append("-");
			}
			text.append(numberToText(splited.get(i)));
			if ( !"000".equals(splited.get(i)) ) {
				text.append(separator.get(splited.size() - 1 - i));
			}
		}

//		System.out.println(text.toString());

		return text.toString();
	}

	public static String[] splitFullNameToThreePart(String fullName) {
		String[] splitedName = new String[3];
		splitedName[0] = "";
		splitedName[1] = "";
		splitedName[2] = "";
		if ( fullName == null || fullName.isEmpty() ) return splitedName;

		int start = fullName.indexOf(' ');
		int end = fullName.lastIndexOf(' ');

		if ( start >= 0 ) {
			splitedName[0] = fullName.substring(0, start);
			if ( end > start ) {
				splitedName[1] = fullName.substring(start + 1, end);
			}
			splitedName[2] = fullName.substring(end + 1, fullName.length());
		}
		return splitedName;
	}

	private static String numberToText(String number) {

		StringBuffer result = new StringBuffer();
		if (number.length() == 3&&!"000".equals(number)) {
			result.append(egyes.get(Integer.parseInt(Character.toString(number.charAt(0)))));
			if ('0'!=number.charAt(0)){
				result.append("száz");	
			}			
			if ('0'==number.charAt(2)) {// tizen és huszon miatt
				result.append(tizes.get(Integer.parseInt(Character.toString(number.charAt(1)))));
			} else {
				result.append(tizen.get(Integer.parseInt(Character.toString(number.charAt(1)))));
			}
			result.append(egyes.get(Integer.parseInt(Character.toString(number.charAt(2)))));
		} else if (number.length() == 2) {
			if ('0'==number.charAt(1)) {// tizen és huszon miatt
				result.append(tizes.get(Integer.parseInt(Character.toString(number.charAt(0)))));
			} else {
				result.append(tizen.get(Integer.parseInt(Character.toString(number.charAt(0)))));
			}
			result.append(egyes.get(Integer.parseInt(Character.toString(number.charAt(1)))));
		} else if (number.length() == 1) {
			result.append(egyes.get(Integer.parseInt(Character.toString(number.charAt(0)))));
		}

		return result.toString();
	}
	
	/**
	 * Egy string-et 3-as darabokra vág szét, a végéről kezdve
	 * @param number
	 * @return String arraylista
	 */
	private static ArrayList<String> splitToThree(String number) {
		ArrayList<String> temp = new ArrayList<String>();

		while (number.length() != 0) {
			if (number.length() > 3) {
				temp.add(number.substring(number.length() - 3, number.length()));
				number = number.substring(0, number.length() - 3);
			} else {
				temp.add(number);
				number = "";
			}

		}
		ArrayList<String> result = new ArrayList<String>();
		for (int i = temp.size() - 1; i >= 0; i--) {
			result.add(temp.get(i));
		}
		return result;
	}

	public static String convertNumberWithSpace(Long number){
		StringBuffer sb=new StringBuffer();
		if (number!=null){
			String text=Long.toString(number);
			ArrayList<String> splited=splitToThree(text);
			for (Iterator<String> iterator = splited.iterator(); iterator.hasNext();) {
				String piece = iterator.next();
				sb.append(piece);
				if (iterator.hasNext()){
					sb.append(" ");
				}
			}
		}else{
			return null;
		}
		return sb.toString();
	}
	
	public static Date dateTimeToDate(Date date) {
		if (date == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) { }
		return date;
		/*
		Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
		*/
	}

	public static Long toYear(XMLGregorianCalendar xmlGregorianCalendar) {
		if ( xmlGregorianCalendar == null ) { return null; }
		return Long.valueOf(toCalendar(xmlGregorianCalendar).get(Calendar.YEAR));
	}

	public static XMLGregorianCalendar toXMLCalendar(Long year){
		if ( year == null ) { return null; }
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, new Long(year).intValue());
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return toXMLCalendar(calendar);
	}

	public static XMLGregorianCalendar toXMLCalendar(Date d){
		if (d == null) return null;
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(d);
		return toXMLCalendar(gregorianCalendar);
	}

	public static XMLGregorianCalendar toXMLCalendar(java.util.Calendar calendar) {
		if ( calendar == null ) return null;
		if ( calendar instanceof GregorianCalendar ) {
			return DATATYPE_FACTORY.newXMLGregorianCalendar(GregorianCalendar.class.cast(calendar));
		} else {
			logger.warn(String.format("The calendar instance [%s] is not a GregorianCalendar!", calendar.getClass().getSimpleName() ));
			return null;
		}
	}

	public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
		return (xmlGregorianCalendar != null) ? toCalendar(xmlGregorianCalendar).getTime() : null;
	}

	public static Date toDate(java.util.Calendar calendar) {
		return (calendar != null) ? calendar.getTime() : null;
	}

	public static java.util.Calendar toCalendar(Date d) {
		if ( d == null ) return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar;
	}

	public static java.util.Calendar toCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
		return (xmlGregorianCalendar != null) ? xmlGregorianCalendar.toGregorianCalendar() : null;
	}

	public static String documentToString(Document doc) throws TransformerException {
		if ( doc == null ) return null;
		StringWriter sw = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(new DOMSource(doc), new StreamResult(sw));
		return sw.toString();
	}

	public static String replaceHungarianCharacter(String s) {
		if ( s == null ) return null;
		String result = new String(s);
		for ( Map.Entry<Character, Character> entry : hungarianCharacterMap.entrySet()) {
			result = result.replaceAll(entry.getKey().toString(), entry.getValue().toString());
		}
		return result;
	}

	public static Document stringToDocument(String s) throws TransformerException, ParserConfigurationException, IOException, SAXException {
		if ( s == null ) return null;
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = null;
		StringReader sr = null;
		try {
			is = new InputSource();
			sr = new StringReader(s);
			is.setCharacterStream(sr);
			is.setEncoding("UTF-8");
			return db.parse(is);
		} finally {
			if ( is != null ) { try { is = null; } catch (Throwable t) { logger.warn(String.format("Resource close failure due %s", t.getMessage()), t); } }
			if ( sr != null ) { try { sr.close(); } catch (Throwable t) { logger.warn(String.format("Resource close failure due %s", t.getMessage()), t); } }
		}
	}

	public static byte[] fileToByteArray(File file) throws IOException {
		if ( file == null ) return new byte[0];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return org.apache.commons.io.IOUtils.toByteArray(fis);
		} finally {
			if ( fis != null ) { try { fis.close(); } catch (Throwable t) { logger.warn(String.format("Resource close failure due %s", t.getMessage()), t); } }
		}
	}

	public static File byteArrayToFile(byte[] byteArray) throws IOException {
		File file = File.createTempFile(UUID.randomUUID().toString(), null, new File(System.getProperty("java.io.tmpdir")));
		if ( byteArray == null || byteArray.length == 0 ) { return file; }
		// org.apache.commons.io.FileUtils.writeByteArrayToFile(file, byteArray);
		OutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(file));
			org.apache.commons.io.IOUtils.write(byteArray, output);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if ( output != null ) { try { output.close(); } catch (Throwable t) { logger.warn(String.format("Resource close failure due %s", t.getMessage()), t); } }
		}

		return file;
	}
	
	public static String nanoSecondsToTime(long nanoSeconds) {
		long nano = nanoSeconds % 1000L;
		long mikro = (nanoSeconds / 1000L) % 1000L;
		long milli = (nanoSeconds / 1000000L) % 1000L;
		long seconds = TimeUnit.SECONDS.convert(nanoSeconds, TimeUnit.NANOSECONDS) % 60;
		long minutes = (TimeUnit.SECONDS.convert(nanoSeconds, TimeUnit.NANOSECONDS) / 60) % 60;
		long hours = (TimeUnit.SECONDS.convert(nanoSeconds, TimeUnit.NANOSECONDS) / 3600) % 24;
		long days = (TimeUnit.SECONDS.convert(nanoSeconds, TimeUnit.NANOSECONDS) / 86400);
		if ( nanoSeconds <= 999L ) {
			return threeDigitString(nano) + "ns";
		} else if ( nanoSeconds <= 999999L ) {
			return threeDigitString(mikro) + "us " + threeDigitString(nano) + "ns";
		} else if ( nanoSeconds <= 999999999L ) {
			return threeDigitString(milli) + "ms " + threeDigitString(mikro) + "us " + threeDigitString(nano) + "ns";
		} else if ( nanoSeconds <= 59999999999L ) {
			return twoDigitString(seconds) + "s " + threeDigitString(milli) + "ms " + threeDigitString(mikro) + "us " + threeDigitString(nano) + "ns";
		} else if ( nanoSeconds <= 3599999999999L ) {
			return twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s " + threeDigitString(milli) + "ms " + threeDigitString(mikro) + "us " + threeDigitString(nano) + "ns";
		} else if ( nanoSeconds <= 86399999999999L ) {
			return twoDigitString(hours) + "h " + twoDigitString(minutes) + "m " + twoDigitString(seconds) + "s " + threeDigitString(milli) + "ms " + threeDigitString(mikro) + "us " + threeDigitString(nano) + "ns";
		} else {
			return days + "d " + 
					twoDigitString(hours) + "h " + 
					twoDigitString(minutes) + "m " + 
					twoDigitString(seconds) + "s " + 
					threeDigitString(milli) + "ms " + 
					threeDigitString(mikro) + "us " + 
					threeDigitString(nano) + "ns";
		}
	}
	
	public static String secondsToTime(long seconds) {
		long second = seconds % 60L;
		long minute = (seconds / 60L) % 60L;
		long hour = (seconds / 3600L) % 24L;
		long day = (seconds / 86400L);
		return day + "d " + twoDigitString(hour) + "h " + twoDigitString(minute) + "m " + twoDigitString(second) + "s ";
	}
	
	public static long timeToSeconds(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {}
		return (date != null) ? date.getTime() : -1l;
	}
	
	private static String twoDigitString(long number) {
		if ( number == 0 ) { return "00"; }
		if ( number / 10 == 0 ) { return "0" + number; }
		return String.valueOf(number);
	}
	
	private static String threeDigitString(long number) {
		if ( number == 0 ) { return "000"; }
		if ( number / 10 == 0 ) { return "00" + number; }
		if ( number / 100 == 0 ) { return "0" + number; }
		return String.valueOf(number);
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static String listToString(List<?> stringList) {
//		return listToString(stringList, new ToString(){
//			@Override public String toString(Object t) {
//				return String.valueOf(t);
//			}
//		});
//	}
//
//	public static <T> String listToString(List<T> stringList, ToString<T> toString) {
//		if ( stringList == null || stringList.size() == 0 ) return "";
//		StringBuilder sb = new StringBuilder();
//		for ( int i = 0, length = stringList.size(); i < length; i++ ) {
//			T o = stringList.get(i);
//			if ( o == null ) continue;
//			sb.append(toString.toString(o)).append((i < (length - 1)) ? ", " : "");
//		}
//		return sb.toString();
//	}
}
