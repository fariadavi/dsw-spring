<%@include file="/WEB-INF/views/helper/template.jsp" %>

<div id="contents">
   <div class="mdl-grid">
        <div class="mdl-cell mdl-cell--1-col">
        </div>
        
        <div class="mdl-cell mdl-cell--5-col">
            <p class="homepage-title">
                <spring:message code="homepage.titulo.acoes"/>
            </p>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/list">
			    		<spring:message code="homepage.comandos.edital.registro"/>
			    	</a>
		   </sec:authorize>
		   <c:if test="${sessionScope.edital.status.codigo == 0}">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/abertura">
			    		<spring:message code="homepage.comandos.edital.abre"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo == 1}">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/inscricao/encerramento">
			    		<spring:message code="homepage.comandos.edital.inscricoes.encerrar"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo == 2}">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/homologacao/inscricao">
			    		<spring:message code="homepage.comandos.edital.inscricoes.homologar"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/homologacao/dispensa">
			    		<spring:message code="homepage.comandos.edital.dispensas.homologar"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/homologacao/encerramento">
			    		<spring:message code="homepage.comandos.edital.homologacao.encerrar"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo == 3}">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/escrita/presenca">
			    		<spring:message code="homepage.comandos.edital.presenca.prova.escrita"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/escrita/notas">
			    		<spring:message code="homepage.comandos.edital.notas.prova.escrita"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/escrita/encerramento">
			    		<spring:message code="homepage.comandos.edital.encerrar.prova.escrita"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo == 4}">
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/alinhamento/presenca">
			    		<spring:message code="homepage.comandos.edital.presenca.prova.oral"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/alinhamento/notas">
			    		<spring:message code="homepage.comandos.edital.notas.prova.alinhamento"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/edital/alinhamento/encerramento">
			    		<spring:message code="homepage.comandos.edital.encerrar.prova.alinhamento"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo == 5}">
		    		<span class="homepage-no-link">
		    			<spring:message code="homepage.comandos.edital.nenhum.comando"/>
		    		</span>
		   </c:if>
		   <c:if test="${empty sessionScope.edital}">
		    		<span class="homepage-no-link">
		    			<spring:message code="homepage.comandos.edital.nenhum.comando"/>
		    		</span>
		   </c:if>
        </div>

        <div class="mdl-cell mdl-cell--6-col">
		   <c:if test="${sessionScope.edital.status.codigo >= 2}">
	            <p class="homepage-title">
	                <spring:message code="homepage.titulo.relatorios.homologacao"/>
	            </p>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/homologacao/homologacao/original">
			    		<spring:message code="homepage.relatorio.inscricoes.homologadas.original"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/homologacao/homologacao/recurso">
			    		<spring:message code="homepage.relatorio.inscricoes.homologadas.recurso"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/homologacao/dispensa/original">
			    		<spring:message code="homepage.relatorio.candidatos.dispensados.original"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/homologacao/dispensa/recurso">
			    		<spring:message code="homepage.relatorio.candidatos.dispensados.recurso"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo >= 3}">
	            <p class="homepage-title">
	                <spring:message code="homepage.titulo.relatorios.provas.escritas"/>
	            </p>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/escritas/presenca">
			    		<spring:message code="homepage.relatorio.presenca.provas.escritas"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/escritas/notas/original">
			    		<spring:message code="homepage.relatorio.notas.provas.escritas.original"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/escritas/notas/recurso">
			    		<spring:message code="homepage.relatorio.notas.provas.escritas.recurso"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/escritas/pendencias">
			    		<spring:message code="homepage.relatorio.professores.pendencias.prova.escrita"/>
			    	</a>
		   </c:if>
		   <c:if test="${sessionScope.edital.status.codigo >= 4}">
	            <p class="homepage-title">
	                <spring:message code="homepage.titulo.relatorios.alinhamento"/>
	            </p>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/alinhamento/presenca">
			    		<spring:message code="homepage.relatorio.presenca.prova.oral"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/alinhamento/notas/original">
			    		<spring:message code="homepage.relatorio.notas.alinhamento.original"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/alinhamento/notas/recurso">
			    		<spring:message code="homepage.relatorio.notas.alinhamento.recurso"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/alinhamento/pendencias">
			    		<spring:message code="homepage.relatorio.professores.pendencias.alinhamento"/>
			    	</a>
			    <a class="homepage-link" href="${pageContext.request.contextPath}/relatorio/aprovacao">
			    		<spring:message code="homepage.relatorio.aprovacao.final"/>
			    	</a>
		   </c:if>
		</div>
	</div>
</div>