package br.unirio.dsw.selecaoppgi.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;

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
	 * Ação AJAX que mostra relatório de homologação original
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/relatorio/homologacao/homologacao/original", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> mostraRelatorioOriginal(HttpServletRequest request) throws IOException {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		Edital edital = (Edital) request.getSession().getAttribute("edital");
		String filePath = new File("C:/Users/Daniel/Desktop/dsw-spring-master/dsw-spring/src/main/java/br/unirio/dsw/selecaoppgi/PdfOriginal.pdf").getAbsolutePath();	 
		File file = new File(filePath);
		Path path = Paths.get(filePath);

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoHomologacao(edital.getId(), 0, 10,
				"", "Homologados");
		
		Document document = new Document();
		try {            
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            if (edital.getStatus() == StatusEdital.Homologacao) {
    			for (Usuario u : edital.getComissaoSelecao()) {
    				if (u.getId() == usuario.getId())
    					inscricaoDAO.mostraRelatorioHomologacaoInicial(edital,document,inscricoes);			
    			}
    		}            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }		
				
		byte[] contents = Files.readAllBytes(path);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "PDFHomologadoOriginal.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
	    return response;
	
		//JsonObject root = new JsonObject();
		//root.addProperty("Result", "OK");		
	}
	
	/**
	 * Ação AJAX que mostra relatório de homologação de recurso
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/relatorio/homologacao/homologacao/recurso", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> mostraRelatorioRecurso(HttpServletRequest request) throws IOException {
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
		Edital edital = (Edital) request.getSession().getAttribute("edital");
	    String filePath = new File("C:/Users/Daniel/Desktop/dsw-spring-master/dsw-spring/src/main/java/br/unirio/dsw/selecaoppgi/PDFRecurso.pdf").getAbsolutePath();	    			     
	    File file = new File(filePath);
	    Path path = Paths.get(filePath);
	    
		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		List<InscricaoEdital> inscricoes = inscricaoDAO.carregaAvaliacaoHomologacaoRecurso(edital.getId(), 0, 10,
				"", "Homologados");
		
		try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            if (edital.getStatus() == StatusEdital.Homologacao) {    			
    			for (Usuario u : edital.getComissaoRecursos()) {
    				if (u.getId() == usuario.getId())
    					inscricaoDAO.mostraRelatorioHomologacaoRecurso(edital,document,inscricoes);
    			}
    		}            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }	
		
		byte[] contents = Files.readAllBytes(path);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/pdf"));
	    String filename = "PDFHomologadoRecurso.pdf";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
	    return response;
	}
	
	/**
	 * Ação AJAX que lista todos os candidatos de um edital esperando homologacao da inscrição
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/homologacao/inscricao/original", method = RequestMethod.GET, produces = "application/json")
	public String lista(HttpServletRequest request, @ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, 
			@ModelAttribute("nome") String filtroNome, @ModelAttribute("status") String filtroStatus) {
		
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		int idEdital = edital.getId();
		
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
	public String listaRecurso(HttpServletRequest request, @ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, 
			@ModelAttribute("nome") String filtroNome, @ModelAttribute("status") String filtroStatus) {

		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		int idEdital = edital.getId();
		
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
	public String listaDispensaProva(HttpServletRequest request, @ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, 
			@ModelAttribute("nome") String filtroNome, @ModelAttribute("status") String filtroStatus) {

		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		int idEdital = edital.getId();
		
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
	public String listaDispensaProvaRecurso(HttpServletRequest request, @ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, 
			@ModelAttribute("nome") String filtroNome, @ModelAttribute("status") String filtroStatus) {

		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Edital edital = (Edital) request.getSession().getAttribute("edital");

		if ((edital == null || edital.getId() != usuario.getIdEdital()) && usuario.getIdEdital() > 0) {
			edital = editalDAO.carregaEditalId(usuario.getIdEdital(), userDAO);
			request.getSession().setAttribute("edital", edital);
		}
		
		int idEdital = edital.getId();
		
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
}