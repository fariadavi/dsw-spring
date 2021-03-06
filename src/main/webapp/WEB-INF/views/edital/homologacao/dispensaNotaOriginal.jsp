<%@include file="/WEB-INF/views/helper/template.jsp" %>

<script>
	var idEdital = <c:out value="${sessionScope.edital.id}"/>
	var comissao = 'original';
</script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/third-party/ngTable/ng-table.min.css" />

<div id="contents" data-ng-controller="dispensaController as ctrl">
   <div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-header">
			<h3><spring:message code="edital.homologacao.dispensa.original.title"/></h3>
		</div>
	</div>
               
   	<div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-filter">
			<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label filtroDiv left ">
	            <input id="filtroNome" type="text" class="mdl-textfield__input" data-ng-change='ctrl.atualizaFiltro()' data-ng-model="filtros.nome" size="40"/>
	            <label class="mdl-textfield__label" for="filtroNome"><spring:message code='edital.homologacao.dispensa.label.name.filter'/></label>
			</div>
			
			<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label filtroDiv">
				<select id="filtroStatus" class="mdl-textfield__input" data-ng-change="ctrl.atualizaFiltro()" data-ng-model="filtros.statusDispensa" data-ng-options="x for x in tipoStatus"></select>
				<label class="mdl-textfield__label" for="filtroStatus"><spring:message code='edital.homologacao.dispensa.label.status.filter'/></label>
			</div>
        </div>
        
        <div class="mdl-cell mdl-cell--12-col">
			<div data-loading-container="tableParams.settings().$loading">
				<table data-ng-table="tableParams" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp wide paginated" style="font-size: 12px"> 
					<tr data-ng-repeat="item in $data"> 
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.name'/>'">
							{{item.nomeCandidato}}							
						</td>
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.dispensar'/>'">
               				<label for="option1_row{{$index}}">
        						<input id="option1_row{{$index}}" name="dispensar_row{{$index}}" type="radio" data-ng-model="item.dispensadoProvaOriginal" data-ng-value="true" data-ng-click="ctrl.liberarSend(item.id, item.dispensadoProvaOriginal, item.justificativaDispensaOriginal)" />
								<spring:message code='edital.homologacao.dispensa.table.dispensar.confirmar'/>
        					</label>
        					<label for="option2_row{{$index}}">
        						<input id="option2_row{{$index}}" name="dispensar_row{{$index}}" type="radio" data-ng-model="item.dispensadoProvaOriginal" data-ng-value="false"  data-ng-click="ctrl.liberarSend(item.id, item.dispensadoProvaOriginal, item.justificativaDispensaOriginal)" />
        						<spring:message code='edital.homologacao.dispensa.table.dispensar.recusar'/>
       						</label>
						</td>
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.dispensa.table.dispensar.justificativa'/>'">
							<textarea rows="3" cols="80" style="resize:none;" data-ng-model="item.justificativaDispensaOriginal" data-ng-disabled="item.dispensadoProvaOriginal || item.dispensadoProvaOriginal == null" data-ng-change="ctrl.liberarSend(item.id, item.dispensadoProvaOriginal, item.justificativaDispensaOriginal)"></textarea>
						</td>
						<td class="text-center">
							<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" id="{{item.id}}" data-ng-disabled="disabled" data-ng-click="ctrl.dispensar(item.id, item.dispensadoProvaOriginal, item.justificativaDispensaOriginal)">
								<spring:message code='edital.homologacao.dispensa.table.comando.enviar'/>
							</button>
						</td>
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
