App.factory("inscricaoDataService", ["$http", function ($http) {
	return {
		lista: function(params) {			
			return $http.get(contextPath + "/edital/homologacao/inscricao/" + params.comissao + "?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || "") + "&status=" + (params.statusHomologacao || ""));
		},
		homologar:function(params){
			return $http.post(contextPath + "/edital/homologacao/inscricao/" + params.comissao,  'id=' + params.id + '&homologado=' + params.homologado + '&justificativa=' + params.justificativa, {headers: { "X-CSRF-TOKEN": csrf.value , "Content-Type": "application/x-www-form-urlencoded" }});						
		}
	};
}]);
