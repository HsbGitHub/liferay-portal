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

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorCategoryUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class FormNavigatorTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public void setBackURL(String backURL) {
		_backURL = backURL;
	}

	/**
	 * @deprecated As of 3.0.0
	 */
	@Deprecated
	public void setCategoryNames(String[] categoryNames) {
		_categoryNames = categoryNames;
	}

	/**
	 * @deprecated As of 7.0.0
	 */
	@Deprecated
	public void setCategorySections(String[][] categorySections) {
		_categorySections = categorySections;
	}

	public void setFormModelBean(Object formModelBean) {
		_formModelBean = formModelBean;
	}

	public void setId(String id) {
		_id = id;
	}

	/**
	 * @deprecated As of 7.0.0
	 */
	@Deprecated
	public void setJspPath(String jspPath) {
		_jspPath = jspPath;
	}

	public void setShowButtons(boolean showButtons) {
		_showButtons = showButtons;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_backURL = null;
		_categoryNames = null;
		_categorySections = null;
		_formModelBean = null;
		_id = null;
		_jspPath = null;
		_showButtons = true;
	}

	protected String[] getCategoryKeys() {
		if (_categoryNames != null) {
			return _categoryNames;
		}

		return FormNavigatorCategoryUtil.getKeys(_id);
	}

	protected String[] getCategoryLabels() {
		if (_categoryNames != null) {
			return _categoryNames;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		return FormNavigatorCategoryUtil.getLabels(
			_id, themeDisplay.getLocale());
	}

	protected String[][] getCategorySectionKeys() {
		if (_categorySections != null) {
			return _categorySections;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] categoryKeys = getCategoryKeys();

		String[][] categorySectionKeys = new String[0][];

		for (String categoryKey : categoryKeys) {
			categorySectionKeys = ArrayUtil.append(
				categorySectionKeys,
				FormNavigatorEntryUtil.getKeys(
					_id, categoryKey, themeDisplay.getUser(), _formModelBean));
		}

		return categorySectionKeys;
	}

	protected String[][] getCategorySectionLabels() {
		if (_categorySections != null) {
			return _categorySections;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] categoryKeys = getCategoryKeys();

		String[][] categorySectionLabels = new String[0][];

		for (String categoryKey : categoryKeys) {
			categorySectionLabels = ArrayUtil.append(
				categorySectionLabels,
				FormNavigatorEntryUtil.getLabels(
					_id, categoryKey, themeDisplay.getUser(), _formModelBean,
					themeDisplay.getLocale()));
		}

		return categorySectionLabels;
	}

	protected String[] getDeprecatedCategorySections() {
		if (_categorySections == null) {
			return new String[0];
		}

		String[] deprecatedCategorySections = new String[0];

		for (String[] categorySection : _categorySections) {
			deprecatedCategorySections = ArrayUtil.append(
				deprecatedCategorySections, categorySection);
		}

		return deprecatedCategorySections;
	}

	@Override
	protected String getPage() {
		return "/form_navigator/page.jsp";
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute(
			"liferay-frontend:form-navigator:backURL", _backURL);
		request.setAttribute(
			"liferay-frontend:form-navigator:categoryKeys", getCategoryKeys());
		request.setAttribute(
			"liferay-frontend:form-navigator:categoryLabels",
			getCategoryLabels());
		request.setAttribute(
			"liferay-frontend:form-navigator:categorySectionKeys",
			getCategorySectionKeys());
		request.setAttribute(
			"liferay-frontend:form-navigator:categorySectionLabels",
			getCategorySectionLabels());
		request.setAttribute(
			"liferay-frontend:form-navigator:deprecatedCategorySections",
			getDeprecatedCategorySections());
		request.setAttribute(
			"liferay-frontend:form-navigator:formModelBean", _formModelBean);
		request.setAttribute("liferay-frontend:form-navigator:id", _id);
		request.setAttribute(
			"liferay-frontend:form-navigator:jspPath", _jspPath);
		request.setAttribute(
			"liferay-frontend:form-navigator:showButtons",
			String.valueOf(_showButtons));
	}

	private String _backURL;
	private String[] _categoryNames;
	private String[][] _categorySections;
	private Object _formModelBean;
	private String _id;
	private String _jspPath;
	private boolean _showButtons = true;

}