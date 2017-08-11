package org.gsonformat.intellij.entity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dim on 16/11/7.
 */
public enum DataType {

    Data_Type_Boolean("boolean"), Data_Type_Int("int"), Data_Type_Double("double"),
    Data_Type_long("long"), Data_Type_String("String"), Data_type_Object("Object"), Data_Type_Array("array");
    private String value;

    DataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DataType typeOfObject(Object value) {
        if (value == null) {
            return Data_type_Object;
        }
        DataType type = null;
        if (value instanceof Boolean) {
            type = Data_Type_Boolean;
        } else if (value instanceof Integer) {
            type = Data_Type_Int;
        } else if (value instanceof Double) {
            type = Data_Type_Double;
        } else if (value instanceof Long) {
            type = Data_Type_long;
        } else if (value instanceof String) {
            type = Data_Type_String;
        } else if (value instanceof JSONObject) {
            type = Data_type_Object;
        } else if (value instanceof JSONArray) {
            type = Data_Type_Array;
        } else {
            type = Data_type_Object;
        }
        return type;
    }

    public static DataType typeOfString(String type) {
        if ("boolean".equals(type) || "Boolean".equals(type)) {
            return Data_Type_Boolean;
        }
        if ("Integer".equals(type) || "int".equals(type)) {
            return Data_Type_Int;
        }
        if ("long".equals(type) || "Long".equals(type)) {
            return Data_Type_long;
        }
        if ("String".equals(type) || "String".equals(type)) {
            return Data_Type_String;
        }
        if ("object".equals(type)) {
            return Data_type_Object;
        }
        if ("array".equals(type)) {
            return Data_Type_Array;
        }

        return null;
    }

    public static boolean isSameDataType(String text, String text2) {
        return isSameDataType(typeOfString(text), typeOfString(text2));
    }

    public static boolean isSameDataType(DataType dataType, DataType dataType1) {
        if (dataType == null || dataType1 == null) {
            return false;
        }
        return dataType == dataType1;
    }

    public static String getWrapperTypeSimpleName(DataType type) {
        switch (type) {
            case Data_Type_Boolean:
                return "Boolean";
            case Data_Type_Int:
                return "Integer";
            case Data_Type_Double:
                return "Double";
            case Data_Type_long:
                return "Long";
            default:
                return type.getValue();
        }
    }

}
