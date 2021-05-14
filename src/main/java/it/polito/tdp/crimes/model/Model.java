package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> percorsoMigliore;
	
	public Model() {
		dao=new EventsDao();
		
	}
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	public void creaGrafo( String categoria, int mese) {
		grafo = new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//Aggiunge i vertici
		Graphs.addAllVertices(grafo, dao.getVertice(categoria, mese));
		//aggiunge i archi
		for(Adiacenza a : dao.getAdiacenze(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdge(grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		
		System.out.println("#Vertici: "+this.grafo.vertexSet().size());
		System.out.println("#Archi: "+this.grafo.edgeSet().size());
	}
	
	
	public List<Adiacenza> getArchi() {
		//calcolo il peso medio degli archi presenti nel grafo
		
		double pesoMedio =0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			pesoMedio+=this.grafo.getEdgeWeight(e);
		}
		pesoMedio= pesoMedio/this.grafo.edgeSet().size();
		//filtro gli archi tenendo solo quelli che hanno il peso maggiore del peso medio
		List<Adiacenza> result = new LinkedList<>();
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pesoMedio) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),this.grafo.getEdgeWeight(e)));
			}
		}
		
		return result;
		
	}
	
	public List<String> trovaPercorso(String sorgente, String destinazione){
		//Vogliamo trovare il percorso piu lungo
		this.percorsoMigliore=new ArrayList<>();
		List<String> parziale = new ArrayList<>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		return this.percorsoMigliore;
		
		
	}
	
	private void cerca(List<String> parziale, String destinazione) {
		//caso terminale
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>this.percorsoMigliore.size()) {
				this.percorsoMigliore= new LinkedList<>(parziale);
			}
			return;
		}
		//Altrimenti, scorro i vicini dell'ultimo inserito e provo inserirli uno a uno
		for(String vicino : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(parziale, destinazione);
				parziale.remove(parziale.size()-1);//back-tracking
			}
		}
		
		
	}
	
	
}
