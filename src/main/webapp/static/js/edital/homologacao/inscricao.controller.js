App.controller("inscricaoController", function ($scope, inscricaoDataService, NgTableParams) {
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
	
	/*
	 * Navega para a pagina de visualizacao de inscricao //REMOVER?
	 */
	self.edita = function(id) {
		window.location = contextPath + "/homologacao/inscricao/edit/" + id;
	}
	
	/*
	 * Cria uma nova inscrição // REMOVER
	 */
	self.novo = function() {
		window.location = contextPath + "/homologacao/inscricao/create";
	}
	
	/*
	 * Remove a inscrição selecionada // REMOVER
	 */
	self.remove = function(id) {
		inscricaoDataService.remove(id).then(atualizaLista);
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