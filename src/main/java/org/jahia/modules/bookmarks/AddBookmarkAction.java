/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2017 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.bookmarks;

import org.jahia.ajax.gwt.helper.ContentManagerHelper;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;

import javax.jcr.PathNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: david
 * Date: May 12, 2010
 * Time: 4:06:46 PM
 */
public class AddBookmarkAction extends Action {
    private ContentManagerHelper contentManager;
    final String bookmarkPath = "bookmarks";

    public void setContentManager(ContentManagerHelper contentManager) {
        this.contentManager = contentManager;
    }

    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        // test if bookmark node is present
        JCRSessionWrapper jcrSessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession(resource.getWorkspace(), resource.getLocale());
        JCRNodeWrapper userBookmarks = null;
        String userPath = renderContext.getUser().getLocalPath();
        try {
            userBookmarks = jcrSessionWrapper.getNode(userPath + "/" + bookmarkPath);
        } catch (PathNotFoundException pnf) {
            userBookmarks = contentManager.addNode(jcrSessionWrapper.getNode(userPath), bookmarkPath, "jnt:bookmarks", null, null,Locale.getDefault());
            userBookmarks.saveSession();
        }

        JSONObject result = new JSONObject();
        if (userBookmarks != null &&  !contentManager.checkExistence(userBookmarks.getPath() + "/" + req.getParameter("jcr:title").replace(" ","-"), jcrSessionWrapper, Locale.getDefault())) {
            JCRNodeWrapper bookmark = contentManager.addNode(userBookmarks, req.getParameter("jcr:title").replace(" ","-"), "jnt:bookmark", null, null, Locale.getDefault());
            bookmark.setProperty("date", new GregorianCalendar());
            if (req.getParameter("url") != null) { bookmark.setProperty("url", req.getParameter("url")); }
            bookmark.setProperty("jcr:title", req.getParameter("jcr:title"));
            bookmark.saveSession();

            result.put("isNew", true);
        }else {
            result.put("isNew", false);
        }

        return new ActionResult(HttpServletResponse.SC_OK, null, result);
    }
}
