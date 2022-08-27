package com.gmail.tomasatrat.ui.views.storefront.security;

import com.gmail.tomasatrat.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
