package com.gmail.tomasatrat.testbench.elements.ui;

import com.gmail.tomasatrat.testbench.elements.components.AppNavigationElement;
import com.vaadin.testbench.HasElementQuery;

public interface HasApp extends HasElementQuery {

	default MainViewElement getApp() {
		return $(MainViewElement.class).onPage().first();
	}

	default AppNavigationElement getMenu() {
		return getApp().getMenu();
	}

}
