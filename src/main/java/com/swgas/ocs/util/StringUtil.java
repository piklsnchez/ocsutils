package com.swgas.ocs.util;

import java.security.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility class for String parsing, formatting, hash generation, etc. 
 * 
 */
final public class StringUtil {
	private static final Logger log = Logger.getLogger(StringUtil.class.getName());

	public static final long IMPLICIT_WAIT_SEC	= 5;
	public static final long PAGE_LOAD_TIMOUT_SEC	= 60;
	public static final long SCRIPT_TIMEOUT_SEC	= 30;
	public static final long THIRTY_SEC_FROM_NOW	= 30000;
	public static final long FIVE_SEC_FROM_NOW	= 5000;
	public static final long THIRTY_DAYS_FROM_NOW	= TimeUnit.DAYS.toMillis(30L);
        public static final DateTimeFormatter MDY_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        public static final DateTimeFormatter YMD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        public static final DateTimeFormatter DMY_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yy");

	private static MessageDigest sha1;
	private static final short SALT_LENGTH = 40;
	
	 static {
		 try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e){}

	 }

	 public static String stripSpecialChars(String text){
		 return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFC);
	 }
	 
	private static char rndChar() {
		int rnd = (int) (Math.random() * 52);
		char base = (rnd < 26) ? 'A' : 'a';
		return (char) (base + rnd % 26);

	}
	
	private static char rndCharNum() {
		int rnd = (int) (Math.random() * 62);
		char base = (rnd < 10) ? '0' : ((rnd-=10) < 26) ? 'A' : 'a';
		return (char) (base + rnd % 26);

	}
	
	private static char rndAllChars() {
		int rnd = (int) (Math.random() * 93);
		char base = 33;
		return (char)(base + rnd);

	}
	
	/**
	 * 
	 * @param a begin (inclusive)
	 * @param b end (exclusive)
	 * @return
	 */
	public static int randomBetween(int a, int b) {
		int len = b - a;
		int val = Math.max(a, b);
		//this causes deadlocks!!!!!!
		/*Random rand = new Random();
		return rand.nextInt(b) + a;*/
		if(len > 0){
			val = Math.abs((int)System.nanoTime() % len) + a;
		}
		log.info(String.format("randomBetween(%d, %d): %d", a,b,val));
		return val;
	}

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			sb.append(rndChar());
		}
		return sb.toString();
	}

	public static String randomStringWithNumbers(int len) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			sb.append(rndCharNum());
		}
		return sb.toString();
	}
	
	public static String randomStringAllChars(int len){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			sb.append(rndAllChars());
		}
		return sb.toString();
	}
	
	public static String generateHash(String item) {
		String uniqid = StringUtil.sha1(UUID.randomUUID().toString());
		return generateHash(item,uniqid);
	}
	
	public static String generateHash(String plainText,String salt) {
		salt = salt.substring(0,SALT_LENGTH);
		return salt+StringUtil.sha1(salt+plainText);
	}

	 /**
	  * Generate sha1 has as hexadecimal string
	  * @param message
	  * @return
	  */
	 public static synchronized String sha1(String message) {
		sha1.reset();
		sha1.update(message.getBytes());
		return toHex(sha1.digest());
	 }

	 /**
	  * Convert byte array to hexadecimal string
	  * @param messageDigest
	  * @return
	  */
	 private static String toHex(byte[] messageDigest) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<messageDigest.length;i++) {
			// NOTE if value in [0,15], toHexString() only returns one char, need to padd with leading zero
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if(hex.length()==1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	 }

	public static String randomNumberString(int len) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append((int) (Math.random() * 9));
		}
		return sb.toString();
	}

	public static String convertAbbreviationToName(String state) {
		String result = null;

		log.info("convertAbbreviationToName() enter");

		if (state.equals("AL")) {
			result = "Alabama";
		} else if (state.equals("AK")) {
			result = "Alaska";
		} else if (state.equals("AZ")) {
			result = "Arizona";
		} else if (state.equals("AR")) {
			result = "Arkansas";
		} else if (state.equals("CA")) {
			result = "California";
		} else if (state.equals("CO")) {
			result = "Colorado";
		} else if (state.equals("CT")) {
			result = "Connecticut";
		} else if (state.equals("DE")) {
			result = "Delaware";
		} else if (state.equals("DC")) {
			result = "District of Columbia";
		} else if (state.equals("FL")) {
			result = "Florida";
		} else if (state.equals("GA")) {
			result = "Georgia";
		} else if (state.equals("HI")) {
			result = "Hawaii";
		} else if (state.equals("ID")) {
			result = "Idaho";
		} else if (state.equals("IL")) {
			result = "Illinois";
		} else if (state.equals("IN")) {
			result = "Indiana";
		} else if (state.equals("IA")) {
			result = "Iowa";
		} else if (state.equals("KS")) {
			result = "Kansas";
		} else if (state.equals("KY")) {
			result = "Kentucky";
		} else if (state.equals("LA")) {
			result = "Louisiana";
		} else if (state.equals("ME")) {
			result = "Maine";
		} else if (state.equals("MD")) {
			result = "Maryland";
		} else if (state.equals("MA")) {
			result = "Massachusetts";
		} else if (state.equals("MI")) {
			result = "Michigan";
		} else if (state.equals("MN")) {
			result = "Minnesota";
		} else if (state.equals("MS")) {
			result = "Mississippi";
		} else if (state.equals("MO")) {
			result = "Missouri";
		} else if (state.equals("MT")) {
			result = "Montana";
		} else if (state.equals("NE")) {
			result = "Nebraska";
		} else if (state.equals("NV")) {
			result = "Nevada";
		} else if (state.equals("NH")) {
			result = "New Hampshire";
		} else if (state.equals("NJ")) {
			result = "New Jersey";
		} else if (state.equals("NM")) {
			result = "New Mexico";
		} else if (state.equals("NY")) {
			result = "New York";
		} else if (state.equals("NC")) {
			result = "North Carolina";
		} else if (state.equals("ND")) {
			result = "North Dakota";
		} else if (state.equals("OH")) {
			result = "Ohio";
		} else if (state.equals("OK")) {
			result = "Oklahoma";
		} else if (state.equals("OR")) {
			result = "Oregon";
		} else if (state.equals("PA")) {
			result = "Pennsylvania";
		} else if (state.equals("RI")) {
			result = "Rhode Island";
		} else if (state.equals("SC")) {
			result = "South Carolina";
		} else if (state.equals("SD")) {
			result = "South Dakota";
		} else if (state.equals("TN")) {
			result = "Tennessee";
		} else if (state.equals("TX")) {
			result = "Texas";
		} else if (state.equals("UT")) {
			result = "Utah";
		} else if (state.equals("VT")) {
			result = "Vermont";
		} else if (state.equals("VA")) {
			result = "Virginia";
		} else if (state.equals("WA")) {
			result = "Washington";
		} else if (state.equals("WV")) {
			result = "West Virginia";
		} else if (state.equals("WI")) {
			result = "Wisconsin";
		} else if (state.equals("WY")) {
			result = "Wyoming";
		} else {
			result = state;
		}
		log.info("convertAbbreviationToName() exit");
		return result;
	}
	
	public static String CsvArray(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : objs) {
			sb.append("'").append(obj).append("', ");
		}
		int len = sb.length();
		sb.delete(len - 2, len);
		return sb.toString();
	}

	/**
	 * finds the position of the @ symbol in the email address, then inserts the
	 * "add"
	 * 
	 * @param emailAddress
	 * @param uuid
	 * @return
	 */
	public static String splitEmail(String emailAddress, String uuid) {
		String newEmailAddress = null;
		String at = "@";

		log.info("splitEmail() enter");
		log.info("uuid = " + uuid + " uuid.length = " + uuid.length());

                String[] e = emailAddress.split(at);
		newEmailAddress = e[0] + uuid + at + e[1];
		log.info("newEmailAddress: " + newEmailAddress);

		log.info("splitEmail() exit");

		return newEmailAddress;
	}
	public static String trimLeadingZeros(String s){
		if(s == null || !s.startsWith("0")){
			return s;
		}
                return ""+Integer.parseInt(s, 10);
	}	

	public static String generateEmailAddress() {
		String email = "weblogic+ocs@localhost.swgas.com";
		return email;
	}
	
	public static String generateDisposableEmailAddress() {
		String email = generateEmailAddress();
		String uuid = getUUID();// UUID.randomUUID();
		email = StringUtil.splitEmail(email, uuid);
		//addEmailToDispose(email);
		return email;
	}

	private static String getUUID() {
		return generateUUID().substring(0, 8);
	}

	private static String generateUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static String generateRandomSSN() {
		return String.format("%09d", new Random().nextInt(100000000));
	}
	
	public static String escapeSql(String sql){
		return sql.replace(":", "\\:");
	}
	
	public static String removeSpecialChars(String string){
            return string.chars()
            .filter(c -> Character.isLetter((char)c) || Character.isWhitespace((char)c))
            .mapToObj(c -> ""+(char)c)
            .collect(Collectors.joining());
	}
}
