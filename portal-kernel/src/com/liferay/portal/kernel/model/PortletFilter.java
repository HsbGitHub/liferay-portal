/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface PortletFilter extends Serializable {

	public String getFilterClass();

	public String getFilterName();

	public Map<String, String> getInitParams();

	public Set<String> getLifecycles();

	public int getOrdinal();

	public PortletApp getPortletApp();

	public void setFilterClass(String filterClass);

	public void setFilterName(String filterName);

	public void setInitParams(Map<String, String> initParams);

	public void setLifecycles(Set<String> lifecycles);

	public void setOrdinal(int ordinal);

	public void setPortletApp(PortletApp portletApp);

}