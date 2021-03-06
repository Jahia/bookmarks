<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="search" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="uiComponents" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="propertyDefinition" type="org.jahia.services.content.nodetypes.ExtendedPropertyDefinition"--%>
<%--@elvariable id="type" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:include view="hidden.header"/>
<template:addResources type="css" resources="bookmarks.css"/>
<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="ajaxreplace.js"/>
<c:set var="user" value="${uiComponents:getBindedComponent(currentNode, renderContext, 'j:bindedComponent')}"/>

<c:if test="${empty user or not jcr:isNodeType(user, 'jnt:user')}">
    <jcr:node var="user" path="${renderContext.user.localPath}"/>
</c:if>
<c:set var="ps" value="?pagerUrl=${url.mainResource}"/>
<c:if test="${!empty param.pageUrl}">
    <c:set var="ps" value="?pagerUrl=${param.pageUrl}"/>
</c:if>
<c:forEach items="${param}" var="p" varStatus="status">
    <c:if test="${p.key != 'pagerUrl' && p.key != 'jsite'}">
        <c:set var="ps" value="${ps}&${p.key}=${p.value}" />
    </c:if>
</c:forEach>
<c:set target="${moduleMap}" property="pagerUrl" value="${param.pagerUrl}"/>

<div id="bookmarkList${user.identifier}">
    <template:initPager totalSize="${moduleMap.end}" pageSize="${currentNode.properties['numberOfBookmarksPerPage'].string}" id="${renderContext.mainResource.node.identifier}"/>
    <template:displayPagination id="${renderContext.mainResource.node.identifier}"/>

    <c:if test="${currentResource.workspace eq 'default'}">
        <script type="text/javascript">
            $('#bookmarkList${user.identifier}').load('<c:url value="${url.baseLive}${currentNode.path}.html.ajax${ps}"/>');
        </script>
    </c:if>

    <c:if test="${currentResource.workspace ne 'default'}">
        <c:if test="${moduleMap.listTotalSize eq 0}">
            <fmt:message key="bookmark.emptyResults"/>
        </c:if>
        <c:if test="${moduleMap.listTotalSize ne 0}">
            <ul class="userMyBookmarksList" id="${currentNode.UUID}">
                <c:forEach items="${moduleMap.currentList}" var="bookmark" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}">
                    <li>
                        <jcr:nodeProperty node="${bookmark}" name="jcr:title" var="title"/>
                        <jcr:node var="myNode" path="${bookmark.path}"/>
                        <a class="userMyBookmarksListIcon" href="${bookmark.properties['url'].string}">${bookmark.properties['jcr:title'].string}</a>
                        &nbsp;<span class="small">&nbsp;<fmt:formatDate
                            value="${bookmark.properties['date'].date.time}" dateStyle="short" type="both"/></span>
                        <div class="floatright">
                            &nbsp;<button class="deleteBookmark" id="${bookmark.path}">
                            <span class="icon-contribute icon-delete"></span><fmt:message key="label.delete"/></button>
                        </div>
                        <div class="clear"></div>
                    </li>
                </c:forEach>
            </ul>
            <script type="text/javascript">
                function deleteBookmark(source) {
                    $.post('<c:url value="${url.base}"/>' + source, {"jcrMethodToCall":"delete"},
                        function(result) {
                            $('#bookmarkList${user.identifier}').load('<c:url value="${url.baseLive}${currentNode.path}.html.ajax"/>${ps}');
                        },'json');
                }

                $(document).ready(function() {
                    var bookMarks = document.getElementsByClassName("deleteBookmark");
                    for (var i = 0; i < bookMarks.length; i++) {
                        bookMarks[i].addEventListener("click", function(e) {
                            deleteBookmark(e.currentTarget.id);
                        });
                    }
                });
            </script>
        </c:if>
    </c:if>
</div>
