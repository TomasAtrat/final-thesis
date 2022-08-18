package com.gmail.tomasatrat.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.gmail.tomasatrat.ui.views.orderedit.OrderItemEditor;

public class DeleteEvent extends ComponentEvent<OrderItemEditor> {
	public DeleteEvent(OrderItemEditor component) {
		super(component, false);
	}
}