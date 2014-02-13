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
 * Created by dgaillard on 2/4/2014.
 */
public class DeleteAction extends Action {

    private ContentManagerHelper contentManager;
    final String bookmarkPath = "bookmarks";

    public void setContentManager(ContentManagerHelper contentManager) {
        this.contentManager = contentManager;
    }

    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        // test if bookmark node is present
        JCRSessionWrapper jcrSessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession(resource.getWorkspace(), resource.getLocale());
        JCRNodeWrapper userBookmarks = null;

        String bookmarkPathToDelete = req.getParameter("bookPathDel");

        String userPath = renderContext.getUser().getLocalPath();

        try {
            userBookmarks = jcrSessionWrapper.getNode(userPath + "/" + bookmarkPath);
        } catch (PathNotFoundException pnf) {
            System.out.println(pnf);
        }

        if (userBookmarks != null &&  !contentManager.checkExistence(userBookmarks.getPath() + "/" + req.getParameter("jcr:title").replace(" ","-"), jcrSessionWrapper, Locale.getDefault())) {
            JCRNodeWrapper bookmark = contentManager.addNode(userBookmarks, req.getParameter("jcr:title"), "jnt:bookmark", null, null, Locale.getDefault());
            bookmark.setProperty("date", new GregorianCalendar());
            if (req.getParameter("url") != null) { bookmark.setProperty("url", req.getParameter("url")); }
            bookmark.setProperty("jcr:title", req.getParameter("jcr:title"));
            bookmark.setProperty("site", urlResolver.getSiteKeyByServerName());
            bookmark.saveSession();
        }
        return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject());
    }
}
