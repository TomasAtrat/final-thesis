/**
 *
 */
package com.gmail.tomasatrat.ui.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.gmail.tomasatrat.ui.views.storefront.security.CurrentUser;
import com.gmail.tomasatrat.backend.data.entity.Order;
import com.gmail.tomasatrat.backend.service.OrderService;
import com.gmail.tomasatrat.ui.views.storefront.StorefrontView;

@Configuration
public class PresenterFactory {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Order, StorefrontView> orderEntityPresenter(OrderService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

}
