package org.msc.web.dev.enums;

import java.util.HashMap;
import java.util.Map;

public enum UseCasesEnums {

    CREATE("create"),
    FIND_BY_ID("findById"),
    UPDATE_STATUS("updateStatus"),
    DELETE("delete");
    private final String useCaseName;
    private static final Map<String, UseCasesEnums> stringUseCasesEnumsMap = new HashMap<>();

    UseCasesEnums(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    static {
        for (UseCasesEnums enums : values()) {
            stringUseCasesEnumsMap.put(enums.useCaseName, enums);
        }
    }

    public static UseCasesEnums getEnumByString(String val) {
        return stringUseCasesEnumsMap.get(val);
    }

    public String getUseCaseName() {
        return useCaseName;
    }
}
