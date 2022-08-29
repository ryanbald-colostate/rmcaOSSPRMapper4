package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Apriori;
import model.Author;
import model.PrIssue;
import util.DBUtil;

public class FileDAO {
	private static FileDAO instancia;
	private String dbcon;
	private String user;
	private String pswd;
	
	private FileDAO(String dbcon, String user, String pswd){
		this.dbcon = dbcon;
		this.user = user;
		this.pswd = pswd;
	}
	
	public static FileDAO getInstancia(String dbcon, String user, String pswd){
		if (instancia == null){
			instancia = new FileDAO(dbcon, user, pswd);
		}
		return instancia;
	}
	
	public ArrayList<String> buscaAPI(String pr,String java, String projectName)
	{
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<String> es = new ArrayList<String>();
		boolean found = false;
		
		try 
		{
			Statement comandoSql = con.createStatement();
			
			String sql = "select expert from file a, \"file_API\" b, \"API_specific\" c where a.full_name = b.file_name and c.api_name_fk = b.api_name and a.project = '"+ projectName +"' and a.file_name like '%"+ java + "%' and expert is not null GROUP BY c.expert";			
			
			//System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			String expert = null;
			
			while(rs.next())
			{
				expert = rs.getString("expert");
				//System.out.println("expert string: " + expert);

				if( expert != "Trash" )
				{
					es.add(expert);
					///System.out.println("expert array: " + es);
					//System.out.println("\n");
					found = true;
				}

			}
		} 
		catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}

		if (found)
			return es;
		else
			return null;
		
	}

	public boolean insertApriori(String pr, String java, String expert, String project, String author) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		
		try 
		{
			Statement comandoSql = con.createStatement();
			
			String sql = "insert into apriori (pr,java,expert,project, author) values ("+pr+",'"+java+"'"+",'"+expert+"', '"+project+"', '"+author+"')";

			//System.out.println(sql);
			
			comandoSql.executeUpdate(sql);
			
			
		} catch (SQLException e) {

			System.out.println(e.getMessage());
			return false;
		}
		return true;

	}
	
	public boolean insertPr(String pr, String title, String body, String project, String author) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		
		try {
			Statement comandoSql = con.createStatement();
			
			String sql = "insert into pr values ("+pr+",'"+title+"'"+",'"+body+"', '"+project+"', '" + author+ "')";

			System.out.println(sql);
			
			comandoSql.executeUpdate(sql);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
			return false;
		}
		return true;

	}

	public ArrayList getAprioris( String project ) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<Apriori> prs = new ArrayList<Apriori>();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
//<<<<<<< HEAD
			String sql = "select pr, a.expert from apriori a where project = \'" + project + "\' GROUP BY pr, a.expert ORDER BY pr";
//======= select pr, a.expert from apriori a where project = 'jabref' GROUP BY pr, a.expert ORDER BY pr

			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
//			String sql = "select pr, a.expert from apriori a GROUP BY pr, a.expert order by pr";
//>>>>>>> 18981556b016fe3128de310029f1f97ffb4ded49
			
			System.out.println( sql + "\n" );
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			String expert = null;
			int pr = 0;
			
			
			while(rs.next()){
				expert = rs.getString("expert");
				pr = rs.getInt("pr");
				Apriori ap = new Apriori();
				ap.setGeneral(expert); // using the old general field to hold experts
				ap.setPr(pr);
				prs.add(ap);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
		}
		if (found)
			return prs;
		else
			return null;
	
	}
	
	public ArrayList getApriorisAuthor( String project ) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<Apriori> authors = new ArrayList<Apriori>();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
//<<<<<<< HEAD
			String sql = "select author, a.expert from apriori a where project = \'" + project + "\' GROUP BY author, a.expert ORDER BY author";
//======= select pr, a.expert from apriori a where project = 'jabref' GROUP BY pr, a.expert ORDER BY pr

			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
//			String sql = "select pr, a.expert from apriori a GROUP BY pr, a.expert order by pr";
//>>>>>>> 18981556b016fe3128de310029f1f97ffb4ded49
			
			System.out.println( sql + "\n" );
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			String expert = null;
			String author = null;
			
			
			while(rs.next()){
				expert = rs.getString("expert");
				author = rs.getString("author");
				Apriori ap = new Apriori();
				ap.setGeneral(expert); // using the old general field to hold experts
				ap.setAuthor(author);
				authors.add(ap);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
		}
		if (found)
			return authors;
		else
			return null;
	
	}
	public ArrayList<String> getTitleBodyAuthor(int pr, String project){
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String title = null;
		String body = null;
		String author = null;
		ArrayList<String> result = new ArrayList();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select title, body, author from pr where pr = "+ pr + " and project = '" + project + "'";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
						
			if(rs.next()){
				title = rs.getString("title");
				body = rs.getString("body");
				author = rs.getString("author");
				result.add(title);
				result.add(body);
				result.add(author);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (found)
			return result;
		else
			return null;
		
	}
	
	public ArrayList<Author> getTitleBodyByAuthor(String author, String project){
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String title = "";
		String body = "";
		//ArrayList<Integer> pr = new ArrayList();
		int pr;
		ArrayList <Author>result = new ArrayList<Author>();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select title, body, author, pr from pr where author = '"+ author + "' and project = '" + project + "'";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
						
			while(rs.next()){
				
				Author aut = new Author();
				title = rs.getString("title");
				body = rs.getString("body");
				author = rs.getString("author");
				//pr.add(rs.getInt("pr"));
				pr = rs.getInt("pr");
				aut.setTitle(title);
				aut.setBody(body);
				aut.setAuthor(author);
				//result.add(pr);
				aut.setPr(pr); 
				result.add(aut);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (found)
			return result;
		else
			return null;
		
	}
	
	public ArrayList getTitleBodyExitAuthor(String author, String project){
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String title = "";
		String body = "";
		
		ArrayList<Integer> pr = new ArrayList();
		int prAux;
		ArrayList result = new ArrayList();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select title, body, author, pr from pr where author = '"+ author + "' and project = '" + project + "'";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
						
			while(rs.next()){
				
				//Author aut = new Author();
				title = title + " " + rs.getString("title");
				body = body + " " + rs.getString("body");
				result.add(title);
				result.add(body);
				//author = rs.getString("author");
				result.add(author);
				//pr.add(rs.getInt("pr"));
				prAux = rs.getInt("pr");
				pr.add(prAux);
				result.add(pr);
				//aut.setTitle(title);
		//		aut.setBody(body);
				//aut.setAuthor(author);
				//result.add(pr);
				//aut.setPr(pr); 
				//result.add(aut);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (found)
			return result;
		else
			return null;
		
	}
	
	public ArrayList<String> getDistinctGenerals() {
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String general = null;
		String col = null;
		ArrayList<String> result = new ArrayList();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			String sql = "select distinct expert from apriori ";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()){
				col = rsmd.getColumnName(1);
				general = rs.getString(1);
				result.add(general);
				
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (found)
			return result;
		else
			return null;
		
	}

	public ArrayList<PrIssue> getIssues(int pr, String project) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		 String prRes = "";
		 String issue = "";
		 String issueTitle = "";
		 String issueBody = "";
		 String issueComments = ""; 
		 String issueTitleLink = "";
		 String issueBodyLink = "";
		 String issueCommentsLink = ""; 
		 int isPR = 0; 
		 int isTrain = 0; 
		 String commitMessage = ""; 
		 String prComments = "";
		String col = null;
		ArrayList<PrIssue> result = new ArrayList();
		
		try {
			Statement comandoSql = con.createStatement();
			
			String sql   = "select pr,issue,issue_title,issue_body,issue_comments,issue_title_linked,issue_body_linked,issue_comments_linked,is_train,commit_message,is_pr, pr_comments "
					+ "from pr_issue where pr = '"+pr+"' and project = '" + project + "'" ;
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()){
				col = rsmd.getColumnName(1);
				//general = rs.getString(1);
				 prRes = rs.getString(1);
				 issue = rs.getString(2);
				 issueTitle = rs.getString(3);
				 issueBody = rs.getString(4);
				 issueComments  = rs.getString(5);
				 issueTitleLink = rs.getString(6);
				 issueBodyLink = rs.getString(7);
				 issueCommentsLink  = rs.getString(8);
				 
				 isTrain   = Integer.parseInt(rs.getString(9));
				 commitMessage  = rs.getString(10);
				 isPR   = Integer.parseInt(rs.getString(11));
				 prComments  = rs.getString(12);
				 
				 PrIssue pri = new PrIssue();
				 pri.setCommitMessage(commitMessage);
				 pri.setIsPR(isPR);
				 pri.setIssue(issue);
				 pri.setIssueBody(issueBody);
				 pri.setIssueBodyLink(issueBodyLink);
				 pri.setIssueComments(issueComments);
				 pri.setIssueCommentsLink(issueCommentsLink);
				 pri.setIssueTitle(issueTitle);
				 pri.setIssueTitleLink(issueTitleLink);
				 pri.setIsTrain(isTrain);
				 pri.setPrComments(prComments);
				 result.add(pri);
				
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		return result;
			
	}
	
}
