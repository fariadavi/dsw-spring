package br.unirio.dsw.selecaoppgi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;

@Controller
public class HomologacaoController
{
	@Autowired
	private UsuarioDAO userDAO;

	@Autowired
	private EditalDAO editalDAO;
	
	@Autowired
	private InscricaoDAO inscricaoDAO;
	
//	/edital/homologacao/inscricao

	/**
	 * Ação que redireciona o usuário para a tela de homologação de inscrições de um edital
	 */
    @RequestMapping(value = "/edital/homologacao/inscricao/{id}", method = RequestMethod.GET)
    public ModelAndView mostraPaginaHomologacaoInscricao(@PathVariable("id") int idEdital)
    {
    	ModelAndView model = new ModelAndView("/edital/homologacao/inscricao");
    	
    	Edital edital = editalDAO.carregaEditalId(idEdital, userDAO);
    	
    	model.getModel().put("edital", edital);
    	return model;
    }
    
    /**
	 * Ação AJAX que lista todos os editais
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao", method = RequestMethod.GET, produces = "application/json")
	public String lista(@ModelAttribute("idEdital") int idEdital, @ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome)
	{
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoHomologacao(idEdital);
//				editalDAO.lista(pagina, tamanho, filtroNome);
//		int total = editalDAO.conta(filtroNome);
		int total = inscricoes.size();
		
		Gson gson = new Gson();
		JsonArray jsonInscricoes = new JsonArray();
		
		for (InscricaoEdital inscricao : inscricoes)
			jsonInscricoes.add(gson.toJsonTree(inscricao));
		
		JsonObject root = new JsonObject();
		root.addProperty("Result", "OK");
		root.addProperty("TotalRecordCount", total);
		root.add("Records", jsonInscricoes);
		return root.toString();
	}
	
//	/edital/homologacao/dispensa
//	/edital/homologacao/encerramento
}