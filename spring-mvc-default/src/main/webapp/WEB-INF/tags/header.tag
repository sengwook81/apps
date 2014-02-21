<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>	
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Zero's Hadoop Installer</title>
<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" scope="request"/>
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script src="${baseURL}/lib/js/jquery.json-2.4.min.js"></script>
<link rel="stylesheet" href="${baseURL}/lib/css/default.css" type="text/css">
