package com.swgas.ocs.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OcsUtil {
    private static final Logger LOG = Logger.getLogger(OcsUtil.class.getName());
    public static final String ALL_ERRORS_ON_PAGE_CSS = "css=.alert-danger";
    public static final String WEBTRANID_ID           = "tranid";
    public static final String ERROR_MESSAGE_ID       = "errorMessage";
    
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

    public static String camelCase(String s){
        if(!s.contains("_")){
            return s;
        }
        boolean hump = true;
        StringBuilder sb = new StringBuilder();
        for(char c : s.toCharArray()){
            if('_' == c){
                hump = true;
            } else {
                sb.append((hump ? Character.toUpperCase(c) : Character.toLowerCase(c)));
                hump = false;
            }
        }
        return sb.toString();
    }

    public static String unCamelCase(String s){
        if(s.contains("_")){
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for(char c : s.toCharArray()){
            sb.append((Character.isUpperCase(c) || Character.isDigit(c) ? ("_" + c) : c));
        }
        if(sb.charAt(0) == '_'){
            sb.replace(0, 1, "");
        }
        return sb.toString().toUpperCase();
    }
    
    public static <R> Optional<R> swallow(FunctionThatThrowsReturns<R> call) {
        try {
            return Optional.ofNullable(call.apply());
        } catch (Exception e) {
            LOG.warning(() -> String.format("swallowed: %s%n%s", e, Arrays.stream(e.getStackTrace())
                .map((ele) -> String.format("%s:%d; ", ele.getFileName(), ele.getLineNumber()))
                .reduce(String::concat).orElse("?")
            ));
            return Optional.empty();
        }
    }

    public static void swallow(FunctionThatThrows call) {
        try {
            call.apply();
        } catch (Throwable e) {
            LOG.warning(() -> String.format("swallowed: %s (Cause: %s)%n%s", e, e.getCause(), Arrays.stream(e.getStackTrace())
                .map((ele) -> String.format("%s:%d; ", ele.getFileName(), ele.getLineNumber()))
                .reduce(String::concat).orElse("?")
            ));
        }
    }

    public static void runtime(FunctionThatThrows call) {
        try {
            call.apply();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T runtime(FunctionThatThrowsReturns<T> call) {
        try {
            return call.apply();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Closeable<T> makeClosable(T t) {
        return new CloseableImpl(t) {
            @Override
            public void close() {
                swallow(() -> t.getClass().getDeclaredMethod("close").invoke(t));
            }
        };
    }

    @FunctionalInterface
    public static interface FunctionThatThrows {

        public void apply() throws Exception;
    }

    @FunctionalInterface
    public static interface FunctionThatThrowsReturns<R> {

        public R apply() throws Exception;
    }

    @FunctionalInterface
    public static interface Closeable<T> extends AutoCloseable {

        default public T get() {
            throw new RuntimeException("not implemented");
        }

        @Override
        public void close();
    }

    public static abstract class CloseableImpl<T> implements Closeable<T> {

        final private T t;

        public CloseableImpl(T t) {
            this.t = t;
        }

        @Override
        public T get() {
            return t;
        }
    }

    public static <K, V> Map<K, V> quickMap(Object[][] oa) {
        return Arrays.stream(oa).collect(Collectors.toMap(a -> (K) a[0], a -> (V) a[1]));
    }
}
