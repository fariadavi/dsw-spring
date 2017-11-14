package br.unirio.dsw.selecaoppgi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.StatusEdital;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;

@Controller
public class HomologacaoController {
	@Autowired
	private UsuarioDAO userDAO;

	@Autowired
	private EditalDAO editalDAO;

	@Autowired
	private InscricaoDAO inscricaoDAO;

	// /edital/homologacao/inscricao

	/**
	 * Ação que redireciona o usuário para a tela de homologação de inscrições
	 * de um edital
	 */
	@RequestMapping(value = "/edital/homologacao/inscricao", method = RequestMethod.GET)
	public ModelAndView mostraPaginaHomologacaoInscricao(HttpServletRequest request) {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}

		if (edital.getStatus() == StatusEdital.Homologacao) {

			for (Usuario u : edital.getComissaoSelecao()) {
				if (u.getId() == usuario.getId())
					return new ModelAndView("/edital/homologacao/inscricaoHomologadoOriginal");
			}

			for (Usuario u : edital.getComissaoRecursos()) {
				if (u.getId() == usuario.getId())
					return new ModelAndView("/edital/homologacao/inscricaoHomologadoRecurso");
			}
		}

		return new ModelAndView("redirect:/?message=edital.homologacao.acesso.negado");
	}

	/**
	 * Ação AJAX que lista todos os candidatos de um edital esperando homologacao da inscrição
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao/original", method = RequestMethod.GET, produces = "application/json")
	public String lista(@ModelAttribute("idEdital") int idEdital, @ModelAttribute("page") int pagina,
			@ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome,
			@ModelAttribute("status") String filtroStatus) {
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoHomologacao(idEdital, pagina, tamanho,
				filtroNome, filtroStatus);
		int total = inscricaoDAO.conta(idEdital, filtroNome, filtroStatus, "Inicial");

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
	
	/**
	 * Ação AJAX que lista todos os candidatos de um edital esperando homologacao da inscrição de recurso
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao/recurso", method = RequestMethod.GET, produces = "application/json")
	public String listaRecurso(@ModelAttribute("idEdital") int idEdital, @ModelAttribute("page") int pagina,
			@ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome,
			@ModelAttribute("status") String filtroStatus) {
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoHomologacaoRecurso(idEdital, pagina, tamanho,
				filtroNome, filtroStatus);
		int total = inscricaoDAO.conta(idEdital, filtroNome, filtroStatus, "Recurso");

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
	

	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao/original", method = RequestMethod.POST)
	public boolean homologaOriginal(@RequestParam("id") int id, @RequestParam("homologado") Boolean homologado,
			@RequestParam("justificativa") String justificativa) {
		if (homologado)
			return inscricaoDAO.homologacaoInicial(id);
		else
			return inscricaoDAO.recusaHomologacaoInicial(id, justificativa);
	}

	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao/recurso", method = RequestMethod.POST)
	public boolean homologaRecurso(@RequestParam("id") int id, @RequestParam("homologado") Boolean homologado,
			@RequestParam("justificativa") String justificativa) {
		if (homologado)
			return inscricaoDAO.homologacaoRecurso(id);
		else
			return inscricaoDAO.recusaHomologacaoRecurso(id, justificativa);
	}

	// /edital/homologacao/dispensa
	/**
	 * Ação que redireciona o usuário para a tela de homologação de dispensa de nota
	 */
	@RequestMapping(value = "/edital/homologacao/dispensa", method = RequestMethod.GET)
	public ModelAndView mostraPaginaDispensaProvaInicial(HttpServletRequest request) {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}

		if (edital.getStatus() == StatusEdital.Homologacao) {

			for (Usuario u : edital.getComissaoSelecao()) {
				if (u.getId() == usuario.getId())
					return new ModelAndView("/edital/homologacao/dispensaNotaOriginal");
			}

			for (Usuario u : edital.getComissaoRecursos()) {
				if (u.getId() == usuario.getId())
					return new ModelAndView("/edital/homologacao/dispensaNotaRecurso");
			}
		}

		return new ModelAndView("redirect:/?message=edital.homologacao.acesso.negado");
	}

	/**
	 * Ação AJAX que lista todos os candidatos de um edital esperando dispensa de nota
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/dispensa/original", method = RequestMethod.GET, produces = "application/json")
	public String listaDispensaProva(@ModelAttribute("idEdital") int idEdital, @ModelAttribute("page") int pagina,
			@ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome,
			@ModelAttribute("status") String filtroStatus) {
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoDispensaProvaInicial(idEdital, pagina, tamanho,
				filtroNome, filtroStatus);
		int total = inscricaoDAO.conta(idEdital, filtroNome, filtroStatus, "Inicial");

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
	
	/**
	 * Ação AJAX que lista todos os candidatos de um edital esperando homologacao de recurso da dispensa de prova 
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/dispensa/recurso", method = RequestMethod.GET, produces = "application/json")
	public String listaDispensaProvaRecurso(@ModelAttribute("idEdital") int idEdital, @ModelAttribute("page") int pagina,
			@ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome,
			@ModelAttribute("status") String filtroStatus) {
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoDispensaProvaRecurso(idEdital, pagina, tamanho,
				filtroNome, filtroStatus);
		int total = inscricaoDAO.conta(idEdital, filtroNome, filtroStatus, "Recurso");

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
	

	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/dispensa/original", method = RequestMethod.POST)
	public boolean dispensarInicial(@RequestParam("id") int id, @RequestParam("dispensado") Boolean dispensado,
			@RequestParam("justificativa") String justificativa) {
		if (dispensado)
			return inscricaoDAO.dispensaProvaInicial(id);
		else
			return inscricaoDAO.recusaDispensaProvaInicial(id, justificativa);
	}

	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/dispensa/recurso", method = RequestMethod.POST)
	public boolean dispensarRecurso(@RequestParam("id") int id, @RequestParam("dispensado") Boolean dispensado,
			@RequestParam("justificativa") String justificativa) {
		if (dispensado)
			return inscricaoDAO.dispensaProvaRecurso(id);
		else
			return inscricaoDAO.recusaDispensaProvaRecurso(id, justificativa);
	}

	// /edital/homologacao/encerramento

}