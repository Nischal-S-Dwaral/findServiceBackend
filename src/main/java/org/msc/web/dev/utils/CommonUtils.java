package org.msc.web.dev.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class CommonUtils {

    public static <T> boolean checkIfObjectIsNotNull(T offer) {
        return Objects.nonNull(offer);
    }

    public static <T> boolean isAnyFieldEmpty(Field[] declaredFields, T offer)  {

        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                if (field.get(offer)==null) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Exception occurred in processing");
            }
        }
        return false;
    }
}
