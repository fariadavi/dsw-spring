App.controller("inscricaoController", function($scope, inscricaoDataService, NgTableParams) {
	var self = this;

	$scope.tipoStatus = ["Aguardando homologação", "Homologados", "Não-homologados", "Todos"];

	$scope.disabled= true;
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
	 *	Libera o botão de Enviar 
	 */
	self.liberarSend = function(itemId, homologado, justificativa){
		angular.element(document.getElementById(itemId)).scope().disabled = !homologado && !justificativa;
	}
	
	/*
	 * Atualiza o status de homologacao de uma inscrição
	 */
	self.homologar = function(id, homologado, justificativa) {
		return inscricaoDataService.homologar({
			id : id,
			homologado : homologado,
			justificativa : justificativa,
			comissao: comissao
		}).then(function successCallback(response) {
			angular.element(document.getElementById(id)).scope().disabled = true;
			showError('Inscrição atualizada com sucesso!');
		}, function errorCallback(response) {
			showError('Ocorreu um erro ao atualizar a inscrição.');
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