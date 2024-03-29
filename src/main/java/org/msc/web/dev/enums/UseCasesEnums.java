package org.msc.web.dev.enums;

import java.util.HashMap;
import java.util.Map;

public enum UseCasesEnums {

    CREATE("create"),
    FIND_BY_ID("findById"),
    FIND_BY_SP_ID("findBySPid"),
    UPDATE_STATUS("updateStatus"),
    DELETE("delete"),
    ADD("add"),
    GET_REVIEW_LIST("getReviewList"),
    GET_SERVICE_LIST("getServiceList"),
    UPDATE_SERVICE("updateService"),
    DELETE_BY_SERVICE_PROVIDER("deleteByServiceProvider"),
    GET_BY_ID("getByID"),
    GET_SERVICE_PROVIDER_LIST("getServiceProviderList"),
    GET_SERVICE_REQUEST_LIST("getServiceRequestList"),
    GET_SERVICE_LIST_BY_SERVICE_PROVIDER("getServiceListByServiceProvider"),
    GET_UNSEEN_NOTIFICATIONS_COUNT("getUnseenNotificationsCount"),;

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
