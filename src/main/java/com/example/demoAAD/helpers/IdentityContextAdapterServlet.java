// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.demoAAD.helpers;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IdentityContextAdapterServlet implements IdentityContextAdapter, HttpSessionActivationListener {
    private HttpSession session = null;
    private IdentityContextData context = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    public IdentityContextAdapterServlet(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.session = request.getSession();
        this.response = response;
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
        this.session = se.getSession();
        loadContext();
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {
        this.session = se.getSession();
        saveContext();
    }

    public void saveContext() {
        if (this.context == null)
            this.context = new IdentityContextData();

        this.session.setAttribute(Config.SESSION_PARAM, context);
    }

    public void loadContext() {
        this.context = (IdentityContextData) session.getAttribute(Config.SESSION_PARAM);
        if (this.context == null) {
            this.context = new IdentityContextData();
        }
    }

    @Override
    public IdentityContextData getContext() {
        loadContext();
        return this.context;
    }

    @Override
    public void setContext(IdentityContextData context) {
        this.context = context;
        saveContext();
    }

    @Override
    public void redirectUser(String location) throws IOException {
        this.response.sendRedirect(location);
    }

    @Override
    public String getParameter(String parameterName) {
        return this.request.getParameter(parameterName);
    }

}
