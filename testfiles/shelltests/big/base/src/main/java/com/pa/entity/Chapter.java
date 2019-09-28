package com.pa.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Chapter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String titulo;
	
	@Column 
	private String ano;
	
	@Column
	private String tituloDoLivro;
	
	@Column
    @ElementCollection(targetClass=String.class)
	private List<String> autores;

	public Chapter(){}
	
	public Chapter(String titulo, String ano, String tituloDoLivro, List<String> autores) {
		super();
		this.titulo = titulo;
		this.ano = ano;
		this.tituloDoLivro = tituloDoLivro;
		this.autores = autores;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}
	
	public String getTituloDoLivro() {
		return tituloDoLivro;
	}

	public void setTituloDoLivro(String tituloDoLivro) {
		this.tituloDoLivro = tituloDoLivro;
	}

	public List<String> getAutores() {
		return autores;
	}

	public void setAutores(List<String> autores) {
		this.autores = autores;
	}

}
