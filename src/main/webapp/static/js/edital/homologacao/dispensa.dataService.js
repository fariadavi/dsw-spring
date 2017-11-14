App.factory("dispensaDataService", ["$http", function ($http) {
	return {
		listaDispensaProva: function(params) {
			return $http.get(contextPath + "/edital/homologacao/dispensa/" + params.comissao + "?idEdital=" + params.id +"&page=" + params.page + "&size=" + params.size + "&nome=" + (params.nome || "") + "&status=" + (params.statusDispensa || ""));
		},
		dispensar:function(params){
			return $http.post(contextPath + "/edital/homologacao/dispensa/" + params.comissao, 'id=' + params.id + '&dispensado=' + params.dispensado + '&justificativa=' + params.justificativa, {headers: { "X-CSRF-TOKEN": csrf.value , "Content-Type": "application/x-www-form-urlencoded" }});						
		}
	};
}]);
