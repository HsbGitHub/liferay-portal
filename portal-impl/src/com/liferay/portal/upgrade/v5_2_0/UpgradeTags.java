/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.upgrade.v5_2_0;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.UpgradeException;
import com.liferay.portal.upgrade.UpgradeProcess;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.tags.NoSuchVocabularyException;
import com.liferay.portlet.tags.model.TagsVocabulary;
import com.liferay.portlet.tags.service.TagsVocabularyLocalServiceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="UpgradeTags.java.html"><b><i>View Source</i></b></a>
 *
 * @author Jorge Ferrer
 */
public class UpgradeTags extends UpgradeProcess {

	public void upgrade() throws UpgradeException {
		_log.info("Upgrading");

		try {
			upgradeEntryGroupId();
			upgradePropertyCategory();
		}
		catch (Exception e) {
			throw new UpgradeException(e);
		}
	}

	protected long getVocabularyId(
			long userId, long groupId, String vocabularyName)
		throws Exception {

		vocabularyName = vocabularyName.trim();

		if (Validator.isNull(vocabularyName) ||
			ArrayUtil.contains(
				_DEFAULT_CATEGORY_PROPERTY_VALUES, vocabularyName)) {

			vocabularyName = PropsValues.TAGS_VOCABULARY_DEFAULT;
		}

		String key = groupId + StringPool.UNDERLINE + vocabularyName;

		TagsVocabulary vocabulary = _vocabulariesMap.get(key);

		if (vocabulary == null) {
			try {
				vocabulary = TagsVocabularyLocalServiceUtil.getGroupVocabulary(
					groupId, vocabularyName);
			}
			catch (NoSuchVocabularyException nsve) {
				vocabulary = TagsVocabularyLocalServiceUtil.addVocabulary(
					userId, groupId, vocabularyName, true);
			}

			_vocabulariesMap.put(key, vocabulary);
		}

		return vocabulary.getVocabularyId();
	}

	protected void upgradePropertyCategory() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getConnection();

			ps = con.prepareStatement(
				"select TE.entryId, TE.groupId, TE.userId, TP.propertyId, " +
					"TP.value from TagsEntry as TE, TagsProperty as TP where " +
						"TE.entryId = TP.entryId and TE.vocabularyId <= 0 " +
							"and TP.key_ = 'category'");

			rs = ps.executeQuery();

			while (rs.next()) {
				long entryId = rs.getLong("TE.entryId");
				long groupId = rs.getLong("TE.groupId");
				long userId = rs.getLong("TE.userId");
				long propertyId = rs.getLong("TP.propertyId");
				String value = rs.getString("TP.value");

				long vocabularyId = getVocabularyId(userId, groupId, value);

				ps = con.prepareStatement(
					"update TagsEntry set vocabularyId = ? where entryId = ?");

				ps.setLong(1, vocabularyId);
				ps.setLong(2, entryId);

				ps.executeUpdate();

				ps = con.prepareStatement(
					"delete from TagsProperty where propertyId = ?");

				ps.setLong(1, propertyId);

				ps.executeUpdate();

				ps.close();
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void upgradeEntryGroupId() throws Exception {
	}

	private String[] _DEFAULT_CATEGORY_PROPERTY_VALUES = new String[] {
		"undefined", "no category", "category"
	};

	private static Log _log = LogFactory.getLog(UpgradeTags.class);

	private Map<String, TagsVocabulary> _vocabulariesMap =
		new HashMap<String, TagsVocabulary>();

}