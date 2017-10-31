App.factory("dispensaDataService", ["$http", function ($http) {
	return {
		lista: function(params) {
			return $http.get(contextPath + "/edital/homologacao/dispensa?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || ""));
		},
		dispensarInicial:function(params){
			return $http.post(contextPath + "/edital/homologacao/dispensa/inicial",  {"id":params.id,"dispensa":params.dispensa,"justificativa":params.justificativa},{headers: { "X-CSRF-TOKEN": csrf.value }});						
		},
		dispensarRecurso:function(params){
			return $http.post(contextPath + "/edital/homologacao/dispensa/recurso", {"id":params.id,"dispensa":params.dispensa,"justificativa":params.justificativa},{headers: {"X-CSRF-TOKEN": csrf.value }});
		}
	};
}]);
