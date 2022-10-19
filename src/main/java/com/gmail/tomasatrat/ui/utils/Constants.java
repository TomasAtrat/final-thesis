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
	public static final String PAGE_READER = "readers";
	public static final String PAGE_STOCK = "stock";

	public static final String PAGE_INVENTORY_PROBLEMS = "inventory_problems";
	public static final String PAGE_INVENTORY = "inventory";
	public static final String PAGE_RECEPTION_PROBLEMS = "reception_problems";
	public static final String PAGE_ORDER_EXPEDITION = "order_expedition";
	public static final String PAGE_DASHBOARD = "dashboard";

	public static final String TITLE_HOME = "Home";
	public static final String TITLE_DASHBOARD = "Dashboard | Smartstore";
	public static final String TITLE_USERS = "Users";
	public static final String TITLE_ORDERS = "Pedidos | Smartstore";
	public static final String TITLE_INVENTORY_PROBLEMS = "Problemas inventario | Smartstore";
	public static final String TITLE_RECEPTION_PROBLEMS = "Problemas recepción | Smartstore";
	public static final String TITLE_ORDER_EXPEDITION = "Pedidos expedidos | Smartstore";
	public static final String TITLE_TASKS = "Tareas";
	public static final String TITLE_READERS = "Lectores RFID";
	public static final String TITLE_PRODUCTS = "Products";
	public static final String TITLE_LOGOUT = "Logout";
	public static final String TITLE_NOT_FOUND = "Page was not found";
	public static final String TITLE_ACCESS_DENIED = "Access denied";

	public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
	public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

	public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";
	public static final String TITLE_STOCK = "Stock";
    public static final String TITLE_INVENTORY = "Inventario";

    // Mutable for testing.
	public static int NOTIFICATION_DURATION = 4000;

}
