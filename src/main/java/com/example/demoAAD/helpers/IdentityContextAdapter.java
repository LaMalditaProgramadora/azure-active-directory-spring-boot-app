// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.demoAAD.helpers;

import java.io.IOException;

public interface IdentityContextAdapter {
    public void setContext(IdentityContextData context);
    public IdentityContextData getContext();
    public void redirectUser(String location) throws IOException;
    public String getParameter(String parameterName);
}
