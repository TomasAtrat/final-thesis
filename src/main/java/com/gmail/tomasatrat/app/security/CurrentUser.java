package com.gmail.tomasatrat.app.security;

import com.gmail.tomasatrat.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
