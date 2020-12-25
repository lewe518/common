package com.lewely.common.function;

/**
 * ComparePredicate
 *
 * @author yiliua
 * Create at: 2020/10/28
 */
@FunctionalInterface
public interface ComparePredicate<T, R> {
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @param r the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(T t, R r);
}
