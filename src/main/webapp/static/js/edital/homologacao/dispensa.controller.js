App.controller("dispensaController", function($scope, dispensaDataService, NgTableParams) {
	var self = this;

	$scope.tipoStatus = ["Aguardando dispensa", "Dispensados", "Não-dispensados", "Todos"];

	$scope.disabled = true;
	/**
	 * Filtros
	 */
	$scope.filtros = {
		nome : "",
		statusDispensa : "Todos"
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
	self.liberarSend = function(itemId, dispensado, justificativa){
		angular.element(document.getElementById(itemId)).scope().disabled = !dispensado && !justificativa;
	}
	
	/*
	 * Atualiza o status de dispensa de prova de uma inscrição
	 */
	self.dispensar = function(id, dispensado, justificativa) {
		return dispensaDataService.dispensar({
			id : id,
			dispensado : dispensado,
			justificativa : justificativa,
			comissao : comissao
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
			return dispensaDataService.listaDispensaProva({
				id : idEdital,
				page : params.page() - 1,
				size : params.count(),
				nome : $scope.filtros.nome,
				statusDispensa : $scope.filtros.statusDispensa,
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