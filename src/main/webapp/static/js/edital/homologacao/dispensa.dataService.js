App.factory("dispensaDataService", ["$http", function ($http) {
	return {
		listaDispensaProva: function(params) {			
			if(params.comissao == "selecao")
				return $http.get(contextPath + "/edital/homologacao/dispensa/inicial?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || "") + "&status=" + (params.statusDispensa || ""));
			else
				return $http.get(contextPath + "/edital/homologacao/dispensa/recurso?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || "") + "&status=" + (params.statusDispensa || ""));
		},
		dispensarInicial:function(params){
			return $http.post(contextPath + "/edital/homologacao/dispensaProva/inicial",  'id=' + params.id + '&dispensado=' + params.dispensado + '&justificativa=' + params.justificativa, {headers: { "X-CSRF-TOKEN": csrf.value , "Content-Type": "application/x-www-form-urlencoded" }});						
		},
		dispensarRecurso:function(params){
			return $http.post(contextPath + "/edital/homologacao/dispensaProva/recurso",  'id=' + params.id + '&dispensado=' + params.dispensado + '&justificativa=' + params.justificativa, {headers: { "X-CSRF-TOKEN": csrf.value , "Content-Type": "application/x-www-form-urlencoded" }});						
		}
	};
}]);
