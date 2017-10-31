App.controller("inscricaoController", function($scope, inscricaoDataService, NgTableParams) {
	var self = this;

	$scope.tipoStatus = [ "Aguardando homologação", "Homologados",
			"Não-homologados", "Todos" ];

	/**
	 * Filtros
	 */
	$scope.filtros = {
		nome : "",
		statusHomologacao : "Todos"
	}

	/*
	 * Altera os filtros de consulta
	 */
	self.atualizaFiltro = function() {
		atualizaLista();
	}

	/*
	 * Atualiza a lista de inscrições
	 */
	var atualizaLista = function() {
		$scope.tableParams.reload();
	}

	/*
	 * Atualiza o status de homologacao original de uma inscrição
	 */
	self.homologarOriginal = function(id, homologado, justificativa) {
		return inscricaoDataService.homologarOriginal({
			id : id,
			homologado : homologado,
			justificativa : justificativa
		}).then(function successCallback(response) {
			showError('Sucesso');
		}, function errorCallback(response) {
			showError('Erro');
		});
	}

	/*
	 * Atualiza o status de homologacao de recuso de uma inscrição
	 */
	self.homologarRecurso = function(id, homologado, justificativa) {
		return inscricaoDataService.homologarRecurso({
			id : id,
			homologado : homologado,
			justificativa : justificativa
		}).then(function successCallback(response) {
			showError('Sucesso');
		}, function errorCallback(response) {
			showError('Erro');
		});
	}

	/*
	 * Prepara a tabela
	 */
	$scope.tableParams = new NgTableParams({}, {
		getData : function(params) {
			return inscricaoDataService.lista({
				id : idEdital,
				page : params.page() - 1,
				size : params.count(),
				nome : $scope.filtros.nome,
				statusHomologacao : $scope.filtros.statusHomologacao,
				comissao : comissao
			}).then(function(data) {
				if (data.data.TotalRecordCount == 0) {
					self.noSite = true;
				} else {
					params.total(data.data.TotalRecordCount);
					self.noSite = false;
					return data = data.data.Records;
				}
			});
		}
	});
});