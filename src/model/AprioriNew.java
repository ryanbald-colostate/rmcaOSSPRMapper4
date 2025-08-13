package model;

import java.util.ArrayList;

public class AprioriNew {
	
	private String pr; //set back to int
	private String title;
	private String body;
	private String author;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	private ArrayList<String> generals;
	
	public AprioriNew() {
		super();
		generals = new ArrayList<String>();
	}
	public String getPr() { //set back to int
		return pr;
	}
	public void setPr(String pr) { //set back to int
		this.pr = pr;
	}
	public ArrayList<String> getGenerals() {
		return generals;
	}
	public void setGenerals(ArrayList<String> generals) {
		this.generals = generals;
	}
	public void insertGeneral(String general) {
		generals.add(general);
	}
	public void setAuthor(String author) {
		// TODO Auto-generated method stub
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}

}
