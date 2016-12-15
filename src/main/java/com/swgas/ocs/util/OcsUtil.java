package com.swgas.ocs.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OcsUtil {
    private static final Logger LOG = Logger.getLogger(OcsUtil.class.getName());
    public static final String ALL_ERRORS_ON_PAGE_CSS = "css=.alert-danger";
    public static final String WEBTRANID_ID           = "tranid";
    public static final String ERROR_MESSAGE_ID       = "errorMessage";
        
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
