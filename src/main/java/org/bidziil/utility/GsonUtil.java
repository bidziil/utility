package org.bidziil.utility;

/**
 * User: Zoltan.Szabo
 */
public class GsonUtil {

//	private static final String NULL = "null";
//
//	private static final ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
//		@Override
//		protected Gson initialValue() {
//			return new Gson();
//		}
//	};
//
//	private static final ThreadLocal<JsonParser> jsonParser = new ThreadLocal<JsonParser>() {
//		@Override
//		protected JsonParser initialValue() {
//			return new JsonParser();
//		}
//	};
//
//	/**
//	 * @param src the object for which Json representation is to be created setting for Gson
//	 * @return Json representation of {@code src}.
//	 **/
//	public static String toJson(Object src) {
//		return gson.get().toJson(src);
//	}
//
//	/**
//	* @param <T> the type of the desired object
//	* @param json the string from which the object is to be deserialized
//	* @param klass the class of T
//	* @return an object of type T from the string
//	* @throws com.google.gson.JsonSyntaxException if json is not a valid representation for an object of type klass
//	**/
//	public static <T> T fromJson(String json, Class<?> klass) {
//		return ( T ) gson.get().fromJson(json, klass);
//	}
//
//	/**
//	 *
//	 * @throws JsonParseException
//	 **/
//	public static JsonObject toJsonObject(Object src) {
//		String json = toJson(src);
//		json = ( json != null && !json.equals(NULL) ) ? json : "{}";
//		JsonElement jsonElement = jsonParser.get().parse(json);
//		JsonObject jsonObject = jsonElement.getAsJsonObject();
//		return jsonObject;
//	}
//
//	/**
//	 *
//	 * @throws JsonParseException
//	 **/
//	public static JSONObject toJSONObject(Object src) {
//		try {
//			return new JSONObject(toJson(src));
//		} catch ( JSONException e ) {
//			new JsonParseException(e.getMessage(), e);
//		}
//		return new JSONObject();
//	}
}
