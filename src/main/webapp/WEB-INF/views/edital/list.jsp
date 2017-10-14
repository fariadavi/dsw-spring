<%@include file="/WEB-INF/views/helper/template.jsp" %>

<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/third-party/ngTable/ng-table.min.css" />

<div id="contents" data-ng-controller="listaController as ctrl">
   <div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-header">
			<h3><spring:message code="edital.homologacao.inscricao.title"/></h3>
		</div>
	</div>
	
   	<div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-filter">
			<div class="left">
	            <input type="text" data-ng-change='ctrl.atualizaFiltro()' data-ng-model="filtros.nome" placeholder="<spring:message code='edital.list.label.name.filter'/>" size="40"/>
			</div>
			<div class="right">
				<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" data-ng-click="ctrl.novo()">
					<spring:message code='edital.list.button.new'/>
				</button>
			</div>
			<div class="clear">
			</div>
        </div>
        
        <div class="mdl-cell mdl-cell--12-col">
			<div data-loading-container="tableParams.settings().$loading">
				<table data-ng-table="tableParams" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp wide paginated" style="font-size: 12px"> 
					<tr data-ng-repeat="item in $data"> 
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.list.table.name'/>'" data-ng-click="ctrl.edita(item.id)">
							{{item.nome}}
						</td>
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.list.table.status'/>'" data-ng-click="ctrl.edita(item.id)">
							{{item.nomeStatus}}
						</td>
						<td class="text-center">
							<button class="mdl-button mdl-js-button mdl-button--icon" confirmed-click="ctrl.remove(item.id)" ng-confirm-click="<spring:message code='edital.list.message.confirm.removal'/>">
								<i class="material-icons">delete</i>
							</button>
						</td>
					</tr>
				</table>
				<div data-ng-show="ctrl.noSite" style="text-align: center">
					<spring:message code="edital.list.message.noresult"/>
				</div>
			</div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/list.controller.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/list.dataService.js"></script>
