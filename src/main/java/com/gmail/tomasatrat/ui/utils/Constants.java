package com.gmail.tomasatrat.ui.utils;

import java.util.Locale;

import org.springframework.data.domain.Sort;

public class Constants {

	public static final Locale APP_LOCALE = Locale.getDefault();

	public static final String ORDER_ID = "orderID";
	public static final String EDIT_SEGMENT = "edit";

	public static final String PAGE_ROOT = "";
	public static final String PAGE_STOREFRONT = "storefront";
	public static final String PAGE_STOREFRONT_ORDER_TEMPLATE =
			PAGE_STOREFRONT + "/:" + ORDER_ID + "?";
	public static final String PAGE_STOREFRONT_ORDER_EDIT_TEMPLATE =
			PAGE_STOREFRONT + "/:" + ORDER_ID + "/" + EDIT_SEGMENT;
	public static final String PAGE_STOREFRONT_ORDER_EDIT =
			"storefront/%d/edit";
	public static final String PAGE_HOME = "home";
	public static final String PAGE_USERS = "users";
	public static final String PAGE_ORDERS = "orders";

	public static final String PAGE_TASKS = "tasks";

	public static final String TITLE_HOME = "Home";
	public static final String TITLE_DASHBOARD = "Dashboard";
	public static final String TITLE_USERS = "Users";
	public static final String TITLE_ORDERS = "Pedidos | Smartstore";
	public static final String TITLE_TASKS = "Tareas";
	public static final String TITLE_PRODUCTS = "Products";
	public static final String TITLE_LOGOUT = "Logout";
	public static final String TITLE_NOT_FOUND = "Page was not found";
	public static final String TITLE_ACCESS_DENIED = "Access denied";

	public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
	public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

	public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";

	// Mutable for testing.
	public static int NOTIFICATION_DURATION = 4000;

}
