<%@ tag import="java.util.List"
%><%@ tag import="java.util.ArrayList"
%><%@ tag import="org.openmrs.DrugOrder"
%><%@ tag import="org.openmrs.Order"
%><%@ tag import="org.openmrs.api.context.Context"
%><%@ attribute name="enc" required="true" type="org.openmrs.Encounter"
%><%

final List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
for (Order order : enc.getOrders()) {
	final DrugOrder drugOrder = Context.getOrderService().getOrder(order.getOrderId(), DrugOrder.class);
	if (drugOrder != null) {
		drugOrders.add(drugOrder);
	}
}

request.setAttribute("drugOrders", drugOrders);

%>