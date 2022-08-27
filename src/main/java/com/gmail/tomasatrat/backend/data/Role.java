package com.gmail.tomasatrat.backend.data;

public class Role {
    public static final String TECHNICIAN = "Técnico";
    public static final String EMPLOYEE = "Empleado";
    // This role implicitly allows access to all views.
    public static final String ADMIN = "admin";

    private Role() {
        // Static methods and fields only
    }

    public static String[] getAllRoles() {
        return new String[]{TECHNICIAN, EMPLOYEE, ADMIN};
    }

    public static String[] getBusinessRoles() {
        return new String[]{EMPLOYEE, ADMIN};
    }
}
