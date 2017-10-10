package com.swgas.ocs.util;

import java.security.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for String parsing, formatting, hash generation, etc.
 *
 */
public class StringUtil {

    private static final Logger LOG = Logger.getLogger(StringUtil.class.getName());

    public static final long IMPLICIT_WAIT_SEC       = 5;
    public static final long PAGE_LOAD_TIMOUT_SEC    = 60;
    public static final long SCRIPT_TIMEOUT_SEC      = 30;
    public static final long THIRTY_SEC_FROM_NOW     = TimeUnit.SECONDS.toMillis(30);
    public static final long FIVE_SEC_FROM_NOW       = TimeUnit.SECONDS.toMillis(5);
    public static final long THIRTY_DAYS_FROM_NOW    = TimeUnit.DAYS.toMillis(30L);
    public static final DateTimeFormatter MDY_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter YMD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DMY_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yy");
    private static final int[] ALPHAS                = IntStream.concat(IntStream.range('A', 'A' + 26), IntStream.range('a', 'a' + 26)).toArray();    
    private static final int[] ALPHA_NUMERICS        = IntStream.concat(IntStream.concat(IntStream.range('A', 'A' + 26), IntStream.range('a', 'a' + 26)), IntStream.range('0', '0' + 10)).toArray();
    private static final int[] CHARACTERS            = IntStream.rangeClosed(33, 126).toArray();
    
    private static MessageDigest sha1;
    private static final short SALT_LENGTH = 40;

    static {
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {}
    }
    
    public enum States {
          ALABAMA        ("AL", "Alabama")
        , ALASKA         ("AK", "Alaska")        
        , ARAZONA        ("AZ", "Arizona")
        , ARKANSAS       ("AR", "Arkansas")
        , CALIFORNIA     ("CA", "California")
        , COLORADO       ("CO", "Colorada")
        , CONNECTICUT    ("CT", "Connecticut")
        , DELAWARE       ("DE", "Delaware")
        , DISTRICT_OF_COLUMBIA("DC", "District of Columbia")
        , FLORIDA        ("FL", "Florida")
        , GEORGIA        ("GA", "Georgia")
        , HAWAII         ("HI", "Hawaii")
        , IDAHO          ("ID", "Idaho")
        , ILLINOIS       ("IL", "Illinois")
        , INDIANA        ("IN", "Indiana")
        , IOWA           ("IA", "Iowa")
        , KANSAS         ("KS", "Kansas")
        , KENTUCKY       ("KY", "Kentucky")
        , LOUISIANA      ("LA", "Louisiana")
        , MAINE          ("ME", "Maine")
        , MARYLAND       ("MD", "Maryland")
        , MASSACHUSETTS  ("MA", "Massachusetts")
        , MICHIGAN       ("MI", "Michigan")
        , MINNESOTA      ("MN", "Minnesota")
        , MISSISSIPPI    ("MS", "Mississippi")
        , MISSOURI       ("MO", "Missouri")
        , MONTANA        ("MT", "Montana")
        , NEBRASKA       ("NE", "Nebraska")
        , NEVADA         ("NV", "Nevada")
        , NEW_HAMPSHIRE  ("NH", "New Hampshire")
        , NEW_JERSEY     ("NJ", "New Jersey")
        , NEW_MEXICO     ("NM", "New Mexico")
        , NEW_YORK       ("NY", "New York")
        , NORTH_CAROLINA ("NC", "North Carolina")
        , NORTH_DAKOTA   ("ND", "North Dakota")
        , OHIO           ("OH", "Ohio")
        , OKLAHOMA       ("OK", "Oklahoma")
        , OREGON         ("OR", "Oregon")
        , PENNSYLVANIA   ("PA", "Pennsylvania")
        , RHODE_ISLAND   ("RI", "Rhode Island")
        , SOUTH_CAROLINA ("SC", "South Carolina")
        , SOUTH_DAKOTA   ("SD", "South Dakota")
        , TENNESSEE      ("TN", "Tennessee")
        , TEXAS          ("TX", "Texas")
        , UTAH           ("UT", "Utah")
        , VERMONT        ("VT", "Vermont")
        , VIRGINIA       ("VA", "Virginia")
        , WASHINGTON     ("WA", "Washington")
        , WEST_VIRGINIA  ("WV", "West Virginia")
        , WISCONSIN      ("WI", "Wisconsin")
        , WYOMING        ("WY", "Wyoming");
  
        private final String abriviation;
        private final String name;
        States(String abrv, String name){
            abriviation = abrv;
            this.name = name;
        }
        public String getAbriviation(){return abriviation;}
//        public
        public String getName(){return name;}
    }

    public static String camelCase(String string) {        
        String result = Pattern.compile("_").splitAsStream(string)
        .filter(s -> !s.isEmpty())
        .map(s -> s.toLowerCase())
        .map(s -> s.replaceFirst(".", s.substring(0,1).toUpperCase()))
        .reduce("", String::concat);
        return result.isEmpty() ? result : result.replaceFirst(".", result.substring(0,1).toLowerCase());
    }

    public static String unCamelCase(String string) {
        int cp = "_".codePointAt(0);
        int[] codePoints = string.codePoints()
        .flatMap(c -> Character.isUpperCase(c) || Character.isDigit(c) ? IntStream.of(cp, Character.toLowerCase(c)) : IntStream.of(c))
        .toArray();
        return new String(codePoints, 0, codePoints.length);
    }

    public static String stripSpecialChars(String text) {
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFC);
    }

    private static char rndChar() {
        return (char)ALPHAS[(int)(Math.random() * ALPHAS.length)];
    }

    private static char rndCharNum() {
        return (char)ALPHA_NUMERICS[(int)(Math.random() * ALPHA_NUMERICS.length)];
    }
    
    public static void main(String... args){
      IntStream.range(0, 100).parallel().forEach(__ -> LOG.info(Character.toString(rndCharNum())));
    }

    private static char rndAllChars() {
        return (char)CHARACTERS[(int)(Math.random() * CHARACTERS.length)];
    }

    /**
     *
     * @param a begin (inclusive)
     * @param b end (exclusive)
     * @return
     */
    public static int randomBetween(int a, int b) {
        if(a > b){
            throw new IllegalArgumentException("b can not be greater than a");
        }
        
        int len = b - a;
        int val = Math.max(a, b);
        //this causes deadlocks!!!!!!
        /*Random rand = new Random();
		return rand.nextInt(b) + a;*/
        if (len > 0) {
            val = Math.abs((int) System.nanoTime() % len) + a;
        }
        LOG.info(String.format("randomBetween(%d, %d): %d", a, b, val));
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

    public static String randomStringAllChars(int len) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(rndAllChars());
        }
        return sb.toString();
    }

    public static String generateHash(String item) {
        String uniqid = StringUtil.sha1(UUID.randomUUID().toString());
        return generateHash(item, uniqid);
    }

    public static String generateHash(String plainText, String salt) {
        salt = salt.substring(0, SALT_LENGTH);
        return salt + StringUtil.sha1(salt + plainText);
    }

    /**
     * Generate sha1 has as hexadecimal string
     *
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
     *
     * @param messageDigest
     * @return
     */
    private static String toHex(byte[] messageDigest) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < messageDigest.length; i++) {
            // NOTE if value in [0,15], toHexString() only returns one char, need to padd with leading zero
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
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

    public static String csvArray(Object[] objs) {
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
        String[] e = emailAddress.split(at);
        newEmailAddress = e[0] + uuid + at + e[1];
        return newEmailAddress;
    }

    public static String trimLeadingZeros(String s) {
        if (s == null || !s.startsWith("0")) {
            return s;
        }
        return "" + Integer.parseInt(s, 10);
    }

    public static String generateEmailAddress() {
        String email = "weblogic+ocs@localhost.swgas.com";
        return email;
    }

    public static String generateDisposableEmailAddress() {
        String email = generateEmailAddress();
        String uuid = getUuid();// UUID.randomUUID();
        email = StringUtil.splitEmail(email, uuid);
        //addEmailToDispose(email);
        return email;
    }

    private static String getUuid() {
        return generateUuid().substring(0, 8);
    }

    public static String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String generateRandomSsn() {
        return String.format("%09d", new Random().nextInt(100000000));
    }

    public static String escapeSql(String sql) {
        return sql.replace(":", "\\:");
    }
    
    public static String csv(String[] s){
        StringBuilder sb = new StringBuilder();
        for(String string : s){
            sb.append(string).append(",");
        }
        int ind = sb.lastIndexOf(",");
        return ind > -1 ? sb.toString().substring(0, ind) : sb.toString();
    }
    
    public static String csvForDb(String[] s){
        StringBuilder sb = new StringBuilder();
        for(String string : s){
            try{
                Integer.parseInt(string);
            } catch(NumberFormatException e){
                string = "'" + string + "'";
                if(Pattern.compile("^'\\d{4}-\\d{2}-\\d{2}'$").matcher(string).matches()){
                    string = "DATE " + string;
                }
            }
            sb.append(string).append(",");
        }
        return sb.toString().substring(0, sb.lastIndexOf(","));
    }

    public static String removeSpecialChars(String string) {
        return string.chars()
        .filter(c -> Character.isLetter((char) c) || Character.isWhitespace((char) c))
        .mapToObj(c -> "" + (char) c)
        .collect(Collectors.joining());
    }
}
