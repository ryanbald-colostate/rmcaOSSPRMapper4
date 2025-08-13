package model;

public class Author {

	private String author;
	private String title;
	private String body;
	private String pr; //set back to int
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
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
	public String getPr() { //set back to int
		return pr;
	}
	public void setPr(String pr) { //set back to int
		this.pr = pr;
	}
}
