<%@include file="/WEB-INF/views/helper/template.jsp" %>

<script>var idEdital =<c:out value="${edital.id}"/></script>

<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/third-party/ngTable/ng-table.min.css" />

<div id="contents" data-ng-controller="dispensaController as ctrl">
   <div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-header">
			<h3><spring:message code="edital.homologacao.dispensa.title"/></h3>
		</div>
	</div>
	
   	<div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-filter">
			<div class="left">
	            <input type="text" data-ng-change='ctrl.atualizaFiltro()' data-ng-model="filtros.nome" placeholder="<spring:message code='edital.homologacao.inscricao.label.name.filter'/>" size="40"/>
			</div>
			<div class="clear">
			</div>
        </div>
        
        <div class="mdl-cell mdl-cell--12-col">
			<div data-loading-container="tableParams.settings().$loading">
				<table data-ng-table="tableParams" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp wide paginated" style="font-size: 12px"> 
					<tr data-ng-repeat="item in $data"> 
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.name'/>'">
							{{item.nomeCandidato}}
						</td>
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.prova.inicial'/>'" data-ng-click="ctrl.dispensar(item.id, 'selecao')">
        					<input class="" type="checkbox" name="group" id="{{item.id}}" />
						</td>
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.provaInicial.justificativa'/>'">
							<input class="" type="text" name="group" id="{{item.id}}" />
						</td>
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.prova.recurso'/>'" data-ng-click="ctrl.dispensar(item.id, 'recurso')">
							<input class="" type="checkbox" name="group" id="{{item.id}}" />
						</td>
						<td class="mdl-data-table__cell--non-numeric" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.provaRecurso.justificativa'/>'">
							<input class="" type="text" name="group" id="{{item.id}}" />
						</td>
<!-- 						<td class="text-center"> -->
<%-- 							<button class="mdl-button mdl-js-button mdl-button--icon" confirmed-click="ctrl.remove(item.id)" ng-confirm-click="<spring:message code='edital.homologacao.dispensa.message.confirm.removal'/>"> --%>
<!-- 								<i class="material-icons">delete</i> -->
<!-- 							</button> -->
<!-- 						</td> -->
					</tr>
				</table>
				<div data-ng-show="ctrl.noSite" style="text-align: center">
					<spring:message code="edital.homologacao.dispensa.message.noresult"/>
				</div>
			</div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/homologacao/dispensa.controller.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/homologacao/dispensa.dataService.js"></script>