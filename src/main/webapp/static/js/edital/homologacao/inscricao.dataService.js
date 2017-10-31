App.factory("inscricaoDataService", ["$http", function ($http) {
	return {
		lista: function(params) {
			return $http.get(contextPath + "/edital/homologacao/inscricao?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || "") + "&status=" + (params.statusHomologacao || ""));
		},
		homologarOriginal:function(params){
			alert('a');
			return $http.post(contextPath + "/edital/homologacao/inscricao/original",  'id=' + params.id + '&homologado=' + params.homologado + '&justificativa=' + params.justificativa, {headers: { "X-CSRF-TOKEN": csrf.value , "Content-Type": "application/x-www-form-urlencoded" }});						
		},
		homologarRecurso:function(params){
			return $http.post(contextPath + "/edital/homologacao/inscricao/recurso",  {"id":params.id,"homologado":params.homologado,"justificativa":params.justificativa}, {headers: { "X-CSRF-TOKEN": csrf.value }});						
		}
	};
}]);
