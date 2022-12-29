package com.example.user.aspect.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unchecked")
public class BaseLogAspect {
    public static final String ARGS = " Arguments is ";
    public static final String RESULT = " Result is ";
    public static final String URI = " URI is ";
    public static final String PREFIX = "#";
    public static final String PREFIX_CONTROLLER_BEFORE = "--->";
    public static final String PREFIX_CONTROLLER_AFTER = "<---";

    public static final String BEFORE_SERVICE_PATTERN = PREFIX + "{}:" + ARGS + "{}";
    public static final String AFTER_SERVICE_PATTERN = PREFIX + "{}:" + RESULT + "{}" + ARGS + "{}";

    public static final String BEFORE_CONTROLLER_PATTERN =
            PREFIX_CONTROLLER_BEFORE + "{}" + " {}:" + URI + "{}" + ARGS + "{}";
    public static final String AFTER_CONTROLLER_PATTERN =
            PREFIX_CONTROLLER_AFTER + "{}" + " {}:" + URI + "{}," + RESULT + "{}," + ARGS + "{}";

    public static final String AFTER_THROWING_PATTERN =
            "Exception in {}.{}() with cause = {} and exception = {}";

    protected String getArgsWithName(JoinPoint joinPoint) {
        String[] parameterNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder("{");
        for (int i = 0; i < args.length; i++) {
            stringBuilder
                    .append(parameterNames[i])
                    .append("=")
                    .append(getStringInstanceOf(Optional.ofNullable(args[i]).orElse("not defined")));
            if (i != args.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    protected String getStringInstanceOf(Object result) {
        if (result instanceof  Objects[]) {
            return Arrays.toString((Object[]) result);
        }
        if (result instanceof HashMap) {
            Map<Object, Object> resultMap = (Map) result;
            return resultMap.entrySet()
                    .stream()
                    .map(entry -> String.join("=", entry.getKey().toString(), entry.getValue().toString()))
                    .collect(Collectors.joining(", ", "{", "}"));
        }
        return Optional.ofNullable(result).orElse("not defined").toString();
    }
}
