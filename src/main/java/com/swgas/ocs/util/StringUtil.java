package com.swgas.ocs.util;

import java.security.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
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
    private static final String EMAIL_ADDRESS        = "weblogic+ocs@localhost.swgas.com";
    
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

    private static char rndAllChars() {
        return (char)CHARACTERS[(int)(Math.random() * CHARACTERS.length)];
    }

    /**
     *
     * @param a begin (inclusive)
     * @param b end (inclusive)
     * @return
     */
    public static int randomBetween(int a, int b) {
        if(a > b){
            throw new IllegalArgumentException("b can not be greater than a");
        } else if(a == b){
            return a;
        }
        return IntStream.rangeClosed(a, b).skip((int)(Math.random() * (b - a + 1))).findFirst().getAsInt();
    }

    public static String randomString(int len) {
        return IntStream.range(0, len).map(__ -> rndChar()).collect(StringBuilder::new, (sb, c) -> sb.append((char)c), StringBuilder::append).toString();
    }

    public static String randomStringWithNumbers(int len) {
        return IntStream.range(0, len).map(__ -> rndCharNum()).collect(StringBuilder::new, (sb, c) -> sb.append((char)c), StringBuilder::append).toString();
    }

    public static String randomStringAllChars(int len) {
        return IntStream.range(0, len).map(__ -> rndAllChars()).collect(StringBuilder::new, (sb, c) -> sb.append((char)c), StringBuilder::append).toString();
    }

    public static String generateHash(String item) {
        String uniqid = sha1(UUID.randomUUID().toString());
        return generateHash(item, uniqid);
    }

    public static String generateHash(String plainText, String salt) {
        salt = salt.substring(0, SALT_LENGTH);
        return salt + sha1(salt + plainText);
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
            hexString.append(String.format("%02x", Byte.toUnsignedInt(messageDigest[i])));
        }
        return hexString.toString();
    }

    public static String randomNumberString(int len) {
        return new Random().ints(len, 0, 10).mapToObj(Objects::toString).reduce("", String::concat);
    }

    public static String csvArray(Object[] objs) {
        return Arrays.stream(objs)
        .map(Objects::toString)
        .collect(Collectors.joining(","));
    }

    /**
     * finds the position of the @ symbol in the email address, then inserts the uuid
     *
     * @param emailAddress
     * @param uuid
     * @return
     */
    public static String splitEmail(String emailAddress, String uuid) {
        return emailAddress.replace("@", uuid + "@");
    }

    /**
     * 
     * @param s number string; must not be negative
     * @return number string with leading zeros removed unless s equals zero; if s contains non digit characters returns s
     */
    public static String trimLeadingZeros(String s) {
        if (s == null || !s.startsWith("0") || !s.codePoints().allMatch(Character::isDigit)) {
            return s;
        }
        return Integer.valueOf(s, 10).toString();
    }

    public static String generateEmailAddress() {
        return EMAIL_ADDRESS;
    }

    public static String generateDisposableEmailAddress() {
        return splitEmail(generateEmailAddress(), getUuid());
    }

    private static String getUuid() {
        return generateUuid().substring(0, 8);
    }

    public static String generateUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String generateRandomSsn() {
        return new Random().ints(9, 0, 10).mapToObj(Objects::toString).reduce("", String::concat);
    }

    /**
     * this is pretty specific to whatever is using it
     * @param sql
     * @return 
     */
    @Deprecated
    public static String escapeSql(String sql) {
        return sql.replace(":", "\\:");
    }
    
    public static String csvForDb(String[] arr){
        return csvArray(Arrays.stream(arr).map(s -> s.matches("^'\\d{4}-\\d{2}-\\d{2}'$") ? "DATE " + s : s).toArray());
    }

    public static String removeSpecialChars(String string) {
        int[] cp = string.codePoints()
        .filter(c -> Character.isLetter(c) || Character.isWhitespace(c))
        .toArray();
        return new String(cp, 0, cp.length);
    }
}
