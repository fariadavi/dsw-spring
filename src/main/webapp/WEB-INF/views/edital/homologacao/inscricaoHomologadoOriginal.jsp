<%@include file="/WEB-INF/views/helper/template.jsp" %>

<script>
	var idEdital = <c:out value="${sessionScope.edital.id}"/>
	var comissao = 'selecao';
</script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/static/third-party/ngTable/ng-table.min.css" />

<!-- https://github.com/kybarg/mdl-selectfield | https://codepen.io/kybarg/pen/dGNeYw -->   
<!-- <link rel="stylesheet" href="https://cdn.rawgit.com/kybarg/mdl-selectfield/mdl-menu-implementation/mdl-selectfield.min.css"> -->
<!-- <script src="https://cdn.rawgit.com/kybarg/mdl-selectfield/mdl-menu-implementation/mdl-selectfield.min.js"></script> -->

<div id="contents" data-ng-controller="inscricaoController as ctrl">
   <div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-header">
			<h3><spring:message code="edital.homologacao.inscricao.original.title"/></h3>
		</div>
	</div>
               
   	<div class="mdl-grid">
        <div class="mdl-cell mdl-cell--12-col page-filter">
			<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label filtroDiv left ">
	            <input id="filtroNome" type="text" class="mdl-textfield__input" data-ng-change='ctrl.atualizaFiltro()' data-ng-model="filtros.nome" size="40"/>
	            <label class="mdl-textfield__label" for="filtroNome"><spring:message code='edital.homologacao.inscricao.label.name.filter'/></label>
			</div>
<!-- 			<div class="mdl-selectfield mdl-js-selectfield mdl-selectfield--floating-label filtroDiv"> -->
<!-- 				<select id="filtroStatus" class="mdl-selectfield__select" data-ng-change="ctrl.atualizaFiltro()" data-ng-model="filtros.statusHomologacao" data-ng-options="x for x in tipoStatus"></select> -->
<%-- 				<label class="mdl-selectfield__label" for="filtroStatus"><spring:message code='edital.homologacao.inscricao.label.status.filter'/></label> --%>
<!-- 			</div> -->
			
			<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label filtroDiv">
				<select id="filtroStatus" class="mdl-textfield__input" data-ng-change="ctrl.atualizaFiltro()" data-ng-model="filtros.statusHomologacao" data-ng-options="x for x in tipoStatus"></select>
				<label class="mdl-textfield__label" for="filtroStatus"><spring:message code='edital.homologacao.inscricao.label.status.filter'/></label>
			</div>
        </div>
        
        <div class="mdl-cell mdl-cell--12-col">
			<div data-loading-container="tableParams.settings().$loading">
				<table data-ng-table="tableParams" class="mdl-data-table mdl-js-data-table mdl-shadow--2dp wide paginated" style="font-size: 12px"> 
					<tr data-ng-repeat="item in $data"> 
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.inscricao.table.name'/>'">
							{{item.nomeCandidato}}							
						</td>
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.inscricao.table.homologado.inicial'/>'">
               				<label for="option1_row{{$index}}">
        						<input id="option1_row{{$index}}" name="homologar_row{{$index}}" type="radio" data-ng-model="item.homologadoOriginal" data-ng-value="true" />
								<spring:message code='edital.homologacao.inscricao.table.homologar.confirmar'/>
        					</label>
        					<label for="option2_row{{$index}}">
        						<input id="option2_row{{$index}}" name="homologar_row{{$index}}" type="radio" data-ng-model="item.homologadoOriginal" data-ng-value="false" />
        						<spring:message code='edital.homologacao.inscricao.table.homologar.recusar'/>
       						</label>
						</td>
						<td class="mdl-data-table__cell--non-numeric cols-table" header-class="'text-left'" data-title="'<spring:message code='edital.homologacao.inscricao.table.homologado.justificativa'/>'">
							<textarea rows="3" cols="80" style="resize:none;" data-ng-model="item.justificativaHomologacaoOriginal" data-ng-disabled="item.homologadoOriginal || item.homologadoOriginal == null" ></textarea>
<!-- 							class="mdl-textfield__input"  -->
						</td>
						<td class="text-center">
							<button class="mdl-button mdl-js-button mdl-button--raised mdl-button--colored" 
							data-ng-disabled="(item.homologadoOriginal == null || (!item.homologadoOriginal && (!item.justificativaHomologacaoOriginal || item.justificativaHomologacaoOriginal == null)))"
								data-ng-click="ctrl.homologarOriginal(item.id, item.homologadoOriginal, item.justificativaHomologacaoOriginal)">
								<spring:message code='edital.homologacao.inscricao.table.comando.enviar'/>
							</button>
						</td>
					</tr>
				</table>
				<div data-ng-show="ctrl.noSite" style="text-align: center">
					<spring:message code="edital.homologacao.inscricao.message.noresult"/>
				</div>
			</div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/homologacao/inscricao.controller.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/edital/homologacao/inscricao.dataService.js"></script>
