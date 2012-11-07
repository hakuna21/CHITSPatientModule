<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c-rt.tld" 
%><c:choose><c:when test="${empty familyFolders}">[Not a member of any family]</c:when><c:otherwise
>${familyFolders[0].address}<c:if test="${empty familyFolders[0].address}"><em>[Street not specified]</em></c:if><c:if 
	test="${not empty familyFolders[0].barangayName}">, ${familyFolders[0].barangayName}</c:if><c:if 
	test="${not empty familyFolders[0].cityName}">, ${familyFolders[0].cityName}</c:if
></c:otherwise></c:choose>