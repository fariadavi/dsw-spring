App.controller("dispensaController", function ($scope, inscricaoDataService, NgTableParams) {
	var self = this;
	
	/**
	 * Filtros
	 */
	$scope.filtros = {
		nome: ""
	}
	
	/*
	 * Altera os filtros de consulta
	 */
	self.atualizaFiltro = function () {
		atualizaLista();
	}
	
	/*
	 * Atualiza a lista de inscrições
	 */
	var atualizaLista = function() {
		$scope.tableParams.reload();
	}
      
	self.dispensarInicial = function(id,dispensa,justificativa) {
		dispensarInicial(id,dispensa,justificativa);
	}
    
    var dispensarInicial = function(id,dispensa,justificativa) {
    	return inscricaoDataService.dispensarInicial({
    		id:id,
    		dispensa:dispensa,
    		justificativa:justificativa
    	});
    }
    
    self.dispensarRecurso = function(id,dispensa,justificativa) {
    	dispensarRecurso(id,dispensa,justificativa);
	}
	
    var dispensarRecurso = function(id,dispensa,justificativa) {
    	return inscricaoDataService.dispensarRecurso({
    		id:id,
    		dispensa:dispensa,
    		justificativa:justificativa
    	});
    }
	/*
	 * Prepara a tabela
	 */
	$scope.tableParams = new NgTableParams({}, {
		getData: function (params) {
			return inscricaoDataService.lista({
				id: idEdital,
				page: params.page() - 1,
				size: params.count(),
				nome: $scope.filtros.nome
			}).then(function (data) {
				if(data.data.TotalRecordCount == 0) {
					self.noSite = true;
				}
				else {
					params.total(data.data.TotalRecordCount);
					self.noSite = false;
					return data = data.data.Records;
				}
			});
		}
	});
});