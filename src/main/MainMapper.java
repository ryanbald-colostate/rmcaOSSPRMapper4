package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DAO.FileDAO;
import model.Apriori;
import model.AprioriNew;
import model.Author;
import model.PrIssue;

public class MainMapper {
	private String user = "postgres";
	private String pswd = "admin";
	private String project = "jabref";
	private String db = "dev";
	private String file = "dat.txt";
	private String pr = null;
	private String java = null;
	private String csv = null;
	private int isOnlyCSV = 0;
	private String separator = ",";
	private String title = null;
	private String body = null;
	private String author = null;
	private String bin = null;
	private ArrayList<AprioriNew> apns = new ArrayList<AprioriNew>();
	private ArrayList<AprioriNew> apna = new ArrayList<AprioriNew>();
	private String classes = null;
	
	private String prRes = "";
	private String issue = "";
	private String issueTitle = "";
	private String issueBody = "";
	private String issueComments = ""; 
	private String issueTitleLink = "";
	private String issueBodyLink = "";
	private String issueCommentsLink = ""; 
	private int isPR = 0; 
	private int isTrain = 0; 
	private String commitMessage = "";
	private String prComments = ""; 
	private String inDir = "";
	private String outDir = "";
	private String cases = "AS IS"; // "LOWER" - every text is lower case (good for TF-IDF). "AS IS" cases as is. Templates are not removed. Any other value: removes templates comparing the characters without transform in lower cases.


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainMapper mp = new MainMapper();
		mp.execute(args);
		
		
	}


	private void execute(String[] args) {
		// TODO Auto-generated method stub
		user 		= args[0];
		pswd 		= args[1];
		project 	= args[2];
		db 			= args[3];
		file 		= args[4];
		csv 		= args[5];
		isOnlyCSV 	= Integer.parseInt(args[6]);
		separator 	= args[7];
		bin 		= args[8];
		classes 	= args[9];
		inDir		= args[10];
		outDir		= args[11];
		cases 		= args[12];
		
		System.out.println("file: "+file);
		System.out.println("db: "+db);
		System.out.println("project: "+project);
		System.out.println("csv: "+csv);
		System.out.println("isOnlyCSV: "+isOnlyCSV);
		System.out.println("separator: "+separator);
		System.out.println("bin: "+bin);
		System.out.println("classes: "+classes);
		System.out.println("input dir: "+inDir);
		System.out.println("output dir: "+outDir);
		
		if (isOnlyCSV==1)
		{
			getPrs(); // apriori body title
			genBinaryExit(); //binary body title
			getAuthors();
			genBinaryExitAuthors();
			genBinarybyAuthors();
		}
		else 
		{
			readData();
		}
	}


	private void genBinaryExit() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = new FileOutputStream(outDir+project+"4_"+bin);
			System.out.println("Writing " + outDir+project+"4_"+bin);

			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
	    	//bw.write("header \n");
			String line = "";
			String beginning = "";
			FileDAO dao = FileDAO.getInstancia(db, user, pswd);
			// write header
			line = line + "pr";
			// all general classifications possible
			ArrayList<String> dbGenerals = dao.getDistinctGenerals();
			for (int k=0; k<dbGenerals.size(); k++) {
				line = line + ";"+dbGenerals.get(k);
			}
			line = line + ";Title;Body;Author;prIssue;issue;issueTitle;issueBody;issueComments;issueTitleLink;issueBodyLink;issueCommentsLink;isPR;isTrain;commitMessage;Comments\n";
			line = removeUtf8Mb4(line);				
			bw.write(line);
			
			// end header
			boolean found = false;
			int pr = 0;
			// find classification for each PR
			for (int i=0; i<apns.size(); i++) {
				AprioriNew apnAux = apns.get(i);
				ArrayList<String> gs = apnAux.getGenerals();
				pr = apnAux.getPr();
				if (pr == 6450) {
					System.out.println("debug");
				}
							
				// order line in order of generals generals
				
				ArrayList<String> printLine = new ArrayList();			
				
				// Search for labels to after use zeros and ones
				for (int t=0; t<dbGenerals.size(); t++) {
					for (int j=0; j<gs.size(); j++) {
						if (gs.get(j).equals(dbGenerals.get(t))){
							found = true;
						}
						
					}
					if (found){
						printLine.add(t,"1" );
						found = false;
					}
					else {
						printLine.add(t, "0");
					}
				}
				
				line = "";
				line = line + pr;
				
				if(apnAux.getPr()==18)
				{
					System.out.println("Debug");
				}
				// add zeros and ones to line to print
				for (int j=0; j<printLine.size(); j++) {
					line = line + ";"+printLine.get(j);
				}
				beginning = line;
				// fill PT title and body
				ArrayList<String> result = dao.getTitleBodyAuthor(pr, project);
				String title = result.get(0);
				String body = result.get(1);
				String author = result.get(2);
				if(title.equals("nan")) {
					title="";
				}
				if(body.equals("nan")) {
					body="";
				}
				if(author==null||author.equals("nan")) {
					author="";
				}
				//line = line + ";"+title+ ";"+body;// title and body
				
				// get issues
				ArrayList<PrIssue> linkedIssues = dao.getIssues(pr, project);
				
				if (linkedIssues.size()==1) { 
					PrIssue pri = new PrIssue();
					pri = linkedIssues.get(0);
					 
					prRes = pri.getPr();
					 issue = pri.getIssue();
					 issueTitle = pri.getIssueTitle();
					 issueBody = pri.getIssueBody();
					 issueComments  = pri.getIssueComments();
					 issueTitleLink = pri.getIssueTitleLink();
					 issueBodyLink  = pri.getIssueBodyLink();
					 issueCommentsLink  = pri.getIssueCommentsLink();
					 isPR   = pri.getIsPR();
				 
					 isTrain = pri.getIsTrain();   
					 commitMessage  = pri.getCommitMessage();
					 prComments = pri.getPrComments();
					 line = line + ";"+title+ ";"+body + ";" + author;
					 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
					 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
					line = line + "\n";
					line = removeUtf8Mb4(line);			
					bw.write(line);
					line = "";

				} else { // to generate one pr line with all issues together
					// initialize to accumulate
					if (linkedIssues.size()==0) { 
						  prRes = "";
						  issue = "";
						  issueTitle = "";
						  issueBody = "";
						  issueComments = ""; 
						  issueTitleLink = "";
						  issueBodyLink = "";
						  issueCommentsLink = ""; 
						  isPR = 1; 
						  isTrain = 0; 
						  commitMessage = ""; 
						  prComments = ""; 
							 line = line + ";"+title+ ";"+body + ";" + author;
							 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
							 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
							line = line + "\n";
							bw.write(line);
							line = "";
	
					}
					else {
						  if (pr==452) {
							  System.out.println("debug");
						  }
						  
						 for (int t=0; t<linkedIssues.size(); t++) { //// to generate one pr line with all issues together
								PrIssue pri = new PrIssue();
								pri = linkedIssues.get(t);
		
								 prRes = pri.getPr(); // pr do not acc
								 
								 issue =  pri.getIssue();
								 issueTitle =  pri.getIssueTitle();// do not acc
								 issueBody = pri.getIssueBody();// do not acc
								 issueComments  = pri.getIssueComments();// do not acc
								 
								 issueTitleLink = pri.getIssueTitleLink();
								 issueBodyLink  =  pri.getIssueBodyLink();
								 issueCommentsLink  = pri.getIssueCommentsLink();
								 
								 isPR   = pri.getIsPR(); // do not acc
								 
								 isTrain = pri.getIsTrain();   // do not acc
								 
								 commitMessage  = pri.getCommitMessage(); // do not acc
								 prComments = pri.getPrComments(); // do not acc
								 line = line + ";"+title+ ";"+body + ";" + author;// title and body
								 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
								line = line + "\n";
								bw.write(line);
								line = "";

						 }
					}
				}
				// concatenate issue data in line
				//line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;

				//line = line + "\n";
				//bw.write(line);
				//line = "";
	    	}
	    	bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void readData() {
		InputStream is = null;

		try 
		{
			is = new FileInputStream(inDir+project+"/"+file+project+".txt");
			System.out.println("Reading " + inDir+project+"/"+file+project+".txt");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String s = null;

	    // prime loop by gathering first string
		try 
		{
			s = br.readLine();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}// primeira linha do arquivo

		ArrayList<String> api = null;

		while (s != null)
		{
			System.out.println("\nLine: " + s);
			splitLine(s);
			api = findAPI(pr, java, project);

			if (api==null)
				System.out.println("not found in " + project + ": " + pr + " - " + java);
			else 
			{
				insertApriori(api);
				insertPr();
			}

			// try to 
			try 
			{
				s = br.readLine();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		//generateFile(); //to generate apriori without the expert when isOnlyCSV = 0
		
		try 
		{
			br.close();
			isr.close();
			is.close();

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}


	private void generateFile() {
		// TODO Auto-generated method stub
		getPrs();
	}


	private void getPrs() {
		
		// get FileDAO obj
		FileDAO fd = FileDAO.getInstancia( db, user, pswd );

		// get PRs from database
		ArrayList<Apriori> aps = fd.getAprioris( project );
		
		// if no PRs found in PR array
		if (aps==null)
		{
			System.out.println("No apriori found!!!");
		}
		else 
		{
			int prAux 		= 0;
			int pr 		 	= 0;
			
			// create new Apriori obj
			AprioriNew apn  = new AprioriNew();
		
			// loop through array of PRs
			for( int i=0; i<aps.size(); i++ ) 
			{	
				// get PR object and PR num member variable (int) 
				Apriori ap = aps.get(i);
				pr = ap.getPr();
				
				// on first iteration, set new Apriori obj and prAux to 
				// pr info from above
				if( i==0 ) 
				{ 
					// first case treatment
					apn.setPr(pr);
					apn.insertGeneral(ap.getGeneral()); // using the general field to store expert 
					prAux = pr;
				}
				else 
				{
					// if pr num is same as during last iteration 
					if (pr==prAux) 
					{
						apn.insertGeneral(ap.getGeneral());
						if (i+1==aps.size()) 
						{ 
							// last case treatment
							apns.add(apn);
						}
					} 
					else 
					{
						apns.add(apn);
						
						prAux = pr;
						
						apn = new AprioriNew();
						apn.setPr(pr);
						apn.insertGeneral(ap.getGeneral());
					}
				}
				
			}
			
			// write output
			try 
			{
				FileOutputStream os = new FileOutputStream(outDir+project+"4_"+csv);
				System.out.println("Writing: "+outDir+project+"4_"+csv);
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				
				FileOutputStream osc = new FileOutputStream("4_"+classes);
				OutputStreamWriter oswc = new OutputStreamWriter(osc);
				BufferedWriter bwc = new BufferedWriter(oswc);
		    	//bw.write("header \n");
				String line = "";
				String lineClasses = "";
				
				
				for (int i=0; i<apns.size(); i++) 
				{
					AprioriNew apnAux = apns.get(i);
					ArrayList<String> gs = apnAux.getGenerals();
					pr = apnAux.getPr();
					FileDAO dao = FileDAO.getInstancia(db, user, pswd);
					ArrayList<String> result = dao.getTitleBodyAuthor(pr, project);
					String title = result.get(0);
					String body = result.get(1);
					String author = result.get(2);
					if (title!=null&&!title.contentEquals("nan")&&!title.equals("NaN")&&!title.isEmpty()){
						if (body!=null&&!body.contentEquals("nan")&&!body.equals("NaN")&&!body.isEmpty()){
							
							title = filter_text(title, cases);
							body = filter_text(body, cases);
							line = line + pr;
							lineClasses = lineClasses + pr +";";
							//line = line + ","+result.get(0)+ ","+result.get(1);// title and body
							line = line + ","+title+ ","+body;// title and body
							
							if(apnAux.getPr()==18)
							{
								System.out.println("Debug");
							}
							
							for (int j=0; j<gs.size(); j++) 
							{
								line = line + ","+gs.get(j);
								if (j==(gs.size()-1))
									
									lineClasses = lineClasses + gs.get(j);
								else
									lineClasses = lineClasses + gs.get(j)+"-";
							}
							
							line = line + "\n";
							lineClasses = lineClasses + "\n";
							bw.write(line);
							bwc.write(lineClasses);
						}
					}
					
					line = "";
					lineClasses = "";
		    	}
		    	
				bw.close();
		    	bwc.close();
		    	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
			
	}

	private String removeUrl(String commentstr)
	{
		int tam = commentstr.length();
	    String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	    Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(commentstr);
	    StringBuffer sb = new StringBuffer(tam);
	    while (m.find()) {
	        m.appendReplacement(sb, "");
	    }
	    return sb.toString();
	}
	
	private String filter_text(String str, String cases) {
		// TODO Auto-generated method stub
		String newstr = str;
		if (cases.equals("LOWER")) {
			newstr = str.toLowerCase();
		} 
		newstr = newstr.trim();
		
		if (!cases.equals("AS IS")) { //skip templates removal

			newstr = newstr.replaceAll("http.*?\\s", " ");
			//newstr = removeUrl(newstr);
	
			// guava
			newstr = newstr.replaceAll( "\\w+\\*+\\son.*_-+", " " );
			newstr = newstr.replaceAll( "\\s*\\*+\\w+:\\*+\\s*`*.*\\|+\\s*\"*", " " );
			newstr = newstr.replaceAll( "\"*thanks.*google\\s*open\\s*source.*git\\s*commits\\].*need_sender_cla\\s*-->\\s*\\|+", " " );
			newstr = newstr.replaceAll( "\\s+\\(\\[login\\s*here.*author_cla\\s*-->\\s*\\|+", " " );
			newstr = newstr.replaceAll( "\\s*\\*+\\s*<!--\\sneed_author_consent.*cla_yes\\s*-->\\s*\\|*", " " );
			newstr = newstr.replaceAll( "\\s*\\*+\\s*<!--\\sneed_author_consent\\s*-->\\s*\\|+", " " );
			newstr = newstr.replaceAll( "thanks\\s*for\\s*your\\s*pull\\s*request.*google\\s*open\\ssource\\s*project.*contributor.*\\(cla\\)", " " );
			newstr = newstr.replaceAll( "once\\s*youve\\s*signed.*verify\\s* it>", " " );
			newstr = newstr.replaceAll( "####\\s*what\\s*to\\s*do.*\\]\\(", " " );
			newstr = newstr.replaceAll( "@googlebot", " " );
			
			
			// mockito
			newstr = newstr.replaceAll( "by\\s`\\s*\"?", " " );
			newstr = newstr.replaceAll( "\\s*\\d+%.*diff.*>.*merging", " " );
			newstr = newstr.replaceAll( "\\s*\\*+\\d+%\\*+.*\\].\\s\\|+", " " );
			newstr = newstr.replaceAll( "`.*merging.*stmts.*hit.*```.*uncovered\\s+suggestions.*", " " );
			newstr = newstr.replaceAll( "\\*+.*on.*into\\s+\\*+.*on\\s+mockito:master\\*+\\s+\\|+", " " );
			newstr = newstr.replaceAll( "\\s*\\d+%.*diff.*update.*\\|+", " " );
			newstr = newstr.replaceAll( "\"?\\s*\\d+.*methods.*messages.*hits.*misses.*partials.*```.*update\\s*\\[\\w+\\]\\(\\s+\\|+", " " );
			newstr = newstr.replaceAll( "\\s*merging.*coverage.*complexity.*partials.*\\]\\(\\s*\\|+", " " );
			newstr = newstr.replaceAll( "`\\d+%`>", " " );
			newstr = newstr.replaceAll( "\\]\\(\\s*(\\(\\w+\\)\\s*)?into\\s*\\[.*\\]\\(\\s*(\\(\\w+\\))?\\s*", " " );
			newstr = newstr.replaceAll( "@dependabot.*\\|+", " " );
			newstr = newstr.replaceAll( "\\(`release.*\\`\\)\\s*.*\\[comment\\s*docs\\]\\(\\s*\\|+\\s*", " " );
			newstr = newstr.replaceAll( "\"?:\\w+:", " " );
			newstr = newstr.replaceAll( "\\(`release\\/.*`\\).*what\\s*that\\s*means\\]\\(\\s*`\\s*", " " );
			newstr = newstr.replaceAll( "\\s*merging\\s\\d{4}\\s", " " );
			
			
			// presto
			newstr = newstr.replaceAll( "\\s*\\*+\\d+%\\*+>\\s*merging.*into.*lines.*methods.*messages.*hits.*misses.*partials.*by\\s*\\[\\w+\\]", " " );
			newstr = newstr.replaceAll( "\\s*merging.*coverage.*complexity.*partials.*complexity.*", " " );
			newstr = newstr.replaceAll( "\\s*\\d+%.*diff.*merging.*into.*lines.*hits.*partials.*update\\s*\\[\\w+\\]\\(\\s*\\|*", " " );
	
			ArrayList <String> yourList = new ArrayList();
			yourList.add("[WIP]");
			yourList.add("[wip]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			yourList.add("[ ]");
			yourList.add("[ x ]");
			yourList.add("[x]");
			yourList.add("[x ]");
			yourList.add("[ x]");
			yourList.add("[]");
			
			yourList.add("Tests created for changes (if applicable)");
			yourList.add("Tests created for changes");
			
			yourList.add("Screenshots added in PR description");
			yourList.add("screenshots added");
			
			yourList.add("Ensured that [the git commit message is a good one]");
			
			yourList.add("Check documentation status");	
			yourList.add("Checked documentation");
			
			yourList.add("tests green");
	
			yourList.add("changes in pull request outlined? (what  why  ...)"); 
			yourList.add("what why");
	
			yourList.add("Changes in pull request outlined");
	
			yourList.add("Commits squashed");	
			
			yourList.add("<!--  Describe the changes you have made here: what  why  ...  Link issues by using the following pattern: #");
			yourList.add("<!-- describe the changes you have made here: what  why");
			yourList.add("<!-- describe the changes you have made here:");
			yourList.add("what  why");
			yourList.add("...");       
	
	
			yourList.add("Link issues by using the following pattern: [#333]");
			yourList.add("link issues by using the following pattern:");
			yourList.add("[koppor#47](https://github.com/koppor/jabref/issues/47");
			
			yourList.add("or [koppor#49]");
			yourList.add("or [koppor#");
			yourList.add("[koppor#");
			yourList.add("https://github.com/JabRef/jabref/issues/333");
			yourList.add("https://github.com/koppor/jabref/issues/47");      
			yourList.add("https://github.com/jabref/jabref/issues/");
			yourList.add("https://github.com/koppor/jabref/issues/"); 
			yourList.add("https://github.com/jabref/jabref/pull/");
			yourList.add("https://github.com/jabref/jabref/pull/");
	
			yourList.add("[#");
	
			
			yourList.add("The title of the PR must not reference an issue  because GitHub does not support autolinking there. -->");
			yourList.add("The title of the PR must not reference an issue");  
			yourList.add("because GitHub does not support autolinking there. -->");
	
			
			yourList.add("If you fixed a koppor issue  link it with following pattern");
			yourList.add("If you fixed a koppor issue");
			yourList.add("link it with following pattern");
	
			yourList.add("fixes https://github.com/jabref/jabref/issues/");
			yourList.add("fixes https://github.com/koppor/jabref/issues/");
			yourList.add("Fixes #");
			yourList.add("Fixes  #");
			yourList.add("Fix #");
			yourList.add("fix issue");
			yourList.add("resolve #");
			yourList.add("resolves #");
			yourList.add("followup from #");
			yourList.add("localizationupd");
			yourList.add("githubusercont");
	
			yourList.add("![image](https://user-images.githubusercontent.com/");
			yourList.add("![image](https://user-images.githubusercontent.com/");
			yourList.add("![modification](https://user-images.githubusercontent.com/");
			yourList.add("![modification](https://user-images.githubusercontent.com/");
			yourList.add("![grafik](https://user-images.githubusercontent.com/");
			yourList.add("![grafik](https://user-images.githubusercontent.com/");
			yourList.add("![littlebefore](https://user-images.githubusercontent.com/");
			yourList.add("![preferences](https://user-images.githubusercontent.com/");
			yourList.add("![preferences](https://user-images.githubusercontent.com/");
			yourList.add("![image]");
			yourList.add("![modification]");
			yourList.add("![grafik]");
			yourList.add("![preferences](https://user-images.githubusercontent.com/");
			yourList.add("![preferences]");
			
			yourList.add("https://user-images.githubusercontent.com/");
					
			yourList.add("<!--  - All items with  [ ]  are still a TODO. - All items checked with  [x]  are done. - Remove items not applicable -->");
			yourList.add("<!--  - All items with  [ ]  are still a TODO.");
			yourList.add("<!--  - All items with");
			yourList.add("are still a TODO.");
			yourList.add("All items checked with");
			yourList.add("are done");
			yourList.add("Remove items not applicable -->");
			
			yourList.add("Change in CHANGELOG.md described (if applicable)");
			yourList.add("Change in CHANGELOG.md described");
			yourList.add("for bigger UI changes");
	
	
			
			yourList.add("Manually tested changed features in running JabRef (always required)");
			yourList.add("Manually tested changed features in running JabRef ");
			
			
			yourList.add("Is the information available and up to date?"); 
			
			yourList.add("If not: Issue created at"); 
			yourList.add("Issue created for outdated help page at");
			yourList.add("Internal SQ");
			yourList.add("If you changed the localization: Did you run  gradle localizationUpdate");
			yourList.add("Internal quality assurance");
			
			
			yourList.add("Replace copy and rename");
			
			yourList.add("expandFileName "); 
			yourList.add("shortenFileName"); 
			
			yourList.add("Aux File listener? - [ ] introduce new paper folder?"); 
			yourList.add("Look for all aux files in paper folder"); 
			yourList.add("create icon inside groups menu/groups sidepane or under tools");
			yourList.add("Introduce an EventBus object being passed around. This enables better testing");
			yourList.add("Make  DatabaseChangeEvent  abstract and add subclasses according to  DatbaseChangeEvent");  
			yourList.add("Rewrite the currently existing code to use that event bus instead of");  
			yourList.add("net.sf.jabref.model.database.BibDatabase.addDatabaseChangeListener(DatabaseChangeListener)");  
			yourList.add("net.sf.jabref.model.database.BibDatabase.removeDatabaseChangeListener(DatabaseChangeListener)");
			
			yourList.add("Mostly GUI changes  testing makes not that much sense here");
			yourList.add("Mostly GUI changes testing makes not that much sense here");
			
			
	
			 
			yourList.add("[x] Change in CHANGELOG.md described");
			yourList.add("[x] Tests created for changes");
			yourList.add("[x] Manually tested changed features in running JabRef ");
			yourList.add("[x] Screenshots added in PR description");		
			yourList.add("[x] Ensured that [the git commit message is a good one]");
			yourList.add("[x] Check documentation status");
			yourList.add("[x] tests green?");
			yourList.add("[x] commits squashed?");
			yourList.add("[x] changes in pull request outlined? (what  why  ...)"); 
			
			yourList.add("[ x ] Change in CHANGELOG.md described");
			yourList.add("[ x ] Tests created for changes");
			yourList.add("[ x ] Manually tested changed features in running JabRef ");
			yourList.add("[ x ] Screenshots added in PR description");		
			yourList.add("[ x ] Ensured that [the git commit message is a good one]");
			yourList.add("[ x ] Check documentation status");
			yourList.add("[ x ] tests green?");
			yourList.add("[ x ] commits squashed?");
			yourList.add("[ x ] changes in pull request outlined? (what  why  ...)");
			
			yourList.add("https://docs.jabref.org/");		
			yourList.add("https://github.com/joelparkerhenderson/git_commit_message");
			yourList.add("help.jabref.org");
			yourList.add("https://github.com/JabRef/help.jabref.org/issues");
			yourList.add("https://github.com/joelparkerhenderson/git_commit_message");
			yourList.add("https://github.com/JabRef/help.jabref.org/issues");
			yourList.add("<https://github.com/JabRef/user-documentation/issues>"); 
			yourList.add("https://github.com/JabRef/user-documentation/issues"); 		
			
			// Jacob Penney additions
			// Guava
			yourList.add( "This code has been reviewed and submitted internally. Feel free to discuss on the PR and we can submit follow-up changes as necessary." );
			yourList.add( "this code has been reviewed and submitted internally feel free to discuss onthe pr and we can submit follow-up changes as necessarycommits");		yourList.add( "filler for non-existent google code issue ");
			yourList.add( "this code has been reviewed and submitted internally feel free to discuss on the pr and we can submit follow-up changes as necessarycommits");
			yourList.add( "this code has been reviewed and submitted internally. feel free to discuss on the pr  and we can submit follow-up changes as necessary.  commits:");
			yourList.add( "moe sync");
			yourList.add( "commits: ===== <p>");		
			yourList.add( "filler for non-existent google code issue ");
			yourList.add( "filler for non-existent issue ");
			yourList.add( "_this issue only exists to ensure that github issues have the same ids they had on google code please ignore it_" );
			yourList.add( "\"_[original comment]( posted by **");
			yourList.add( "_[original comment]( posted by **");
			yourList.add( "\"_[original issue]( created by **");
			yourList.add( "_[original issue]( created by **");
			
			// Mockito
			yourList.add( "### problem" );
			yourList.add( "### bugfix" );
			yourList.add( "##### reconnect" );
			yourList.add("> hey > > thanks for the contribution this is awesome> as you may have read project members have somehow an opinionated view on what and how should be> mockito eg we dont want mockito to be a feature bloat> there may be a thorough review with feedback -> code change loop> > which branch : > - on mockito 2x make your pull request target `release/2x`> - on next mockito version make your pull request target `master`>> _this block can be removed_> _something wrong in the template fix it here `github/pull_request_templatemd`check list -  read the [contributing guide]( -  pr should be motivated ie what does it fix why and if relevant how -  if possible / relevant include an example in the description that could help all readers       including project members to get a better picture of the change -  avoid other runtime dependencies -  meaningful commit history  intention is important please rebase your commit history so that each       commit is meaningful and help the people that will explore a change in 2 years -  the pull request follows coding style -  mention `<issue number>` in the description _if relevant_ -  at least one commit should mention `fixes #<issue number>` _if relevant_");
			yourList.add("> hey> > first thanks for reporting in order to help us to classify issue can you make sure the following check boxes are checked ?> > if this is about mockito usage the better way is to reach out to> >  - stackoverflow :   - the mailing-list  :  / mockito@googlegroupscom>    (note mailing-list is moderated to avoid spam)>> _this block can be removed_> _something wrong in the template fix it here `github/issue_templatemd`check that -  the mockito message in the stacktrace have useful information but it didnt help -  the problematic code (if thats possible) is copied here       note that some configuration are impossible to mock via mockito -  provide versions (mockito / jdk / os / any other relevant information) -  provide a [short self contained correct (compilable) example]( of the issue       (same as any question on stackoverflowcom) -  read the [contributing guide](https://githubcom/mockito/mockito/blob/master/github/contributingmd)");	
			yourList.add( "> hey> > " );
			yourList.add( "> hey > > " );
			yourList.add( "\"> hey> > " );
			yourList.add( "\"```" );
			yourList.add( "## [current coverage]( is" );
			yourList.add( "powered by [codecov]" );
			yourList.add( "# [codecov]( report>" );
			yourList.add( "]( into [release/2x](" );
			yourList.add( "]( into [master](" );
			yourList.add( "into **master** will change coverage by" );
			yourList.add( "[![coverage status]( unknown when pulling" );
			yourList.add( "[![coverage status]( remained the same when pulling" );
			yourList.add( "[![sunburst]( no coverage report found for" );
			yourList.add( "** into **master** will increase coverage by" );
			yourList.add( "** into **master** will not affect coverage as of [`" );
			yourList.add( "** into **master** will not effect coverage as of [`" );
			yourList.add( "** into **master** will decrease coverage by" );
			yourList.add( "will not change coverage``` diff@@" );
			yourList.add( "will increase coverage by" );
			yourList.add( "will decrease coverage by" );
			yourList.add( "`]( coverage diff``` diff@@" );
			yourList.add( "> > ( last updated by [" );
			yourList.add( " diff@@            master    " );
			yourList.add( "            master   " );
			yourList.add( "         branches      " );
			yourList.add( " diff @@  files          " );
			yourList.add( "            master    " );
			yourList.add( "```[![sunburst]( ( last updated by [" );
			yourList.add( " via [" );
			yourList.add( "**master** at" );
			yourList.add( "( updated on successful ci builds ||" );
			yourList.add( "[![coverage status]( remained the same when pulling " );
			yourList.add( "![coverage  remained the same when pulling" );
			yourList.add( "[![coverage  unknown when pulling" );
			yourList.add( "[![ no coverage report found for" );
			yourList.add( "## [current  is" );
			yourList.add( "the diff coverage is " );
			yourList.add( "will **not change** coverage> " );
			yourList.add( "will **decrease** coverage " );
			yourList.add( "will **increase** coverage " );
			yourList.add( "no coverage report found for **release/2x** at " );
			yourList.add( "|| one of your ci runs failed on this pull request so dependabot wont merge itdependabot will still automatically merge this pull request if you amend it and your tests pass ||" );
			yourList.add( "               ( updated on successful ci builds ||" );
			yourList.add( "diff@@" );
			yourList.add( "           lines          " );
			yourList.add( "![sunburst]( no coverage report found for" );
			
	
			// Presto
			yourList.add( "looks good ||" );
			yourList.add( " looks good ||" );
			yourList.add( "looks good to me||" );
			yourList.add( "looks good to me ||" );
			yourList.add( "look good ||" );
			yourList.add( "fixed" );
			yourList.add( "merged thanks! ||" );
			yourList.add( "good stuff ||" );
			yourList.add( "merged ||" );
			yourList.add( "do not work on this pull request until  was applied!" );
			yourList.add( "|| this issue has been automatically marked as stale because it has not had recent activity it will be closed if no further activity occurs ||" );
			yourList.add( "this issue has been automatically marked as stale because it has not had any activity in the last 2 years if you feel that this issue is important just comment and the stale tag will be removed otherwise it will be closed in 7 days this is an attempt to ensure that our open issues remain valuable and relevant so that we can keep track of what needs to be done and prioritize the right things ||" );
	
	
			//RxJava
			yourList.add( "based on votes" );
			yourList.add( "based on unanimous votes" );
			yourList.add( "based on the majority of votes" );
			yourList.add( "implemented ||" );
			yourList.add( "[debug] [testeventlogger]" );
			yourList.add( "thanks ||" );
			yourList.add( "thanks! ||" );
			yourList.add( "lgtm ||" );
			yourList.add( "]( into [1x]( by" );
			yourList.add( "]( into [2x]( by" );
			yourList.add( "coverage diff" );
			yourList.add( "@@##" );
			yourList.add( "[![impacted file tree graph]" );
			
			// powertoys templates (fabio)
			yourList.add("Summary of the Pull Request");
			yourList.add("What is this about:");
			yourList.add("This will help us diagnose current and future issues like the one thats linked");
			yourList.add("What is include in the PR");
			yourList.add("###");		yourList.add("##");
			yourList.add("**");
			yourList.add("Microsoft PowerToys version");
			yourList.add("Running as admin");
			yourList.add("Area(s) with issue");
			yourList.add("Steps to reproduce");
			yourList.add("Actual Behavior");
			yourList.add("Description of the new feature / enhancement");
			yourList.add("Scenario when this would be used");
			yourList.add("Supporting information");
			yourList.add("Expected Behavior");
			yourList.add("Utility with translation issue");
			yourList.add("Language affected");
			yourList.add("Actual phrase(s)");
			yourList.add("Other Software");
			yourList.add("How does someone test / validate");
			yourList.add("Testing export functionality");
			yourList.add("Contributor License Agreement (CLA)");
			yourList.add("A CLA must be signed. If not, go over here and sign the CLA");
			yourList.add("Linked issue:");
			yourList.add("Communication:");
			yourList.add("Tests:");
			yourList.add("Installer:");
			yourList.add("Localization:");
			yourList.add("Docs:");
			yourList.add("Binaries:");
			yourList.add("No new binaries");
			yourList.add("YML for signing for new binaries");
			yourList.add("WXS for installer for new binaries");
			yourList.add("Quality Checklist");
			yourList.add("<!-- Enter a brief description/summary of your PR here What does it fix/what does it change/how was it tested (even manually if necessary)? -->");
			yourList.add("<!-- enter a brief description/summary of your pr here. what does it fix/what does it change/how was it tested (even manually  if necessary)? -->");
			yourList.add("<!-- Enter a brief description/summary of your PR here. What does it fix/what does it change/how was it tested (even manually  if necessary)? -->");
			
			yourList.add("<!-- Other than the issue solved is this relevant to any other issues/existing PRs? -->");
			yourList.add("<!-- other than the issue solved  is this relevant to any other issues/existing prs? -->");
			yourList.add("<!-- Other than the issue solved  is this relevant to any other issues/existing PRs? -->");
	
			yourList.add("<!-- please review the items on the pr checklist before submitting-->");
			yourList.add("<!-- please review the items on the pr checklist before submitting-->");
			yourList.add("<!-- Please review the items on the PR checklist before submitting-->");
	
			yourList.add("<!-- provide a more detailed description of the pr other things   or any additional comments/features here -->");
			yourList.add("<!-- Provide a more detailed description of the PR  other things fixed or any additional comments/features here -->");
			yourList.add("<!-- provide a more detailed description of the pr  other things  or any additional comments/features here -->");
			yourList.add("<!-- provide a more detailed description of the pr other things  or any additional comments/features here -->");
			yourList.add("<!-- provide a more detailed description of the pr other things or any additional comments/features here -->");		yourList.add("<!-- Provide a more detailed description of the PR other things fixed or any additional comments/features here -->");
			yourList.add("<!-- Provide a more detailed description of the PR  other things fixed or any additional comments/features here -->");
			yourList.add("<!-- provide a more detailed description of the pr other things   or any additional comments/features here -->");
			yourList.add("detailed description of the pull request / additional comments");
			yourList.add("validation steps performed");		
			
			yourList.add("<!-- Describe how you validated the behavior Add automated tests wherever possible but list manual validation steps taken as well -->");
			yourList.add("<!-- describe how you validated the behavior. add automated tests wherever possible  but list manual validation steps taken as well -->");
			yourList.add("<!-- Describe how you validated the behavior. Add automated tests wherever possible  but list manual validation steps taken as well -->");
			
			yourList.add("summary of the new feature/enhancement");
			yourList.add("summary of the pull request");
			yourList.add("<!--**important: when reporting bsods or security issues do not attach memory dumps logs or traces to github issues");
			yourList.add("instead send dumps/traces to secure@microsoftcom referencing this github issue-->");
			yourList.add("# environment");
			yourList.add("powertoy module for which you are reporting the bug (if applicable)");		
			yourList.add("<!-- whats actually happening? -->");
			yourList.add("screenshots!"); 
			yourList.add("## references");
			yourList.add("## references");
			yourList.add("## pr checklist *");
			yourList.add("## pr checklist*");
			yourList.add("applies to #");
			yourList.add("xxx *  cla signed");
			yourList.add("*  cla signed");
			yourList.add("*  tests added/passed");
			yourList.add("*  requires documentation to be updated");
			yourList.add("*  i ve discussed this with core contributors already");
			yourList.add("xxx");
			yourList.add("##");
			yourList.add("##");
			yourList.add("##");
			yourList.add("**image was set to remote loading**");
			yourList.add("**");
			yourList.add("**");
			yourList.add("**");
			yourList.add("**");
			yourList.add("**");
			yourList.add("**");
			yourList.add("ive discussed this with core contributors in the issue");
			yourList.add("added/updated and all pass");
			yourList.add("added/updated and all pass");
			yourList.add("all end user facing strings can be localized");
			yourList.add("added/ updated");
			yourList.add("any new files are added to wxs / yml");
			yourList.add("[yml for signing]");
			yourList.add("for new binaries");
			yourList.add("[wxs for installer]");
			yourList.add("for new binaries");
			yourList.add("a cla must be signed if not go over [here]");
			yourList.add("and sign the cla");			
			yourList.add("<!-- important: when reporting bsods or security issues do not attach memory dumps logs or traces to github issues");
			yourList.add("instead send dumps/traces to secure@microsoftcom referencing this github issue-->");
			yourList.add("<!-- if applicable add screenshots to help explain your problem -->");
			yourList.add("instead send dumps/traces to secure@microsoftcom referencing this github issue-->");
			yourList.add("cla signed"); 
			yourList.add("if not go over [here]");
			yourList.add("tests added/passed"); 
			yourList.add("requires documentation to be updated");
			yourList.add("ive discussed this with core contributors already");
			yourList.add("issue number where discussion took place");
			yourList.add("info on pull request_what does this include");
			yourList.add("how does someone test & validate");
			yourList.add("what is this about");
			yourList.add("how does someone test & validate");
			yourList.add("what is this about?");
			yourList.add("cla signed if not go over [here]");
			yourList.add("what does this include?");
			
			// repeat to eliminate again
			yourList.add( "[rxjava-pull-requests " );
			yourList.add( "[rxjava-pull-requests " );
			yourList.add( "[rxjava-pull-requests " );
			
			yourList.add( "]( failurelooks like theres a problem with this pull request ||" );
			yourList.add( "]( failurelooks like theres a problem with this pull request ||" );
			yourList.add( "]( failurelooks like theres a problem with this pull request ||" );
	
			yourList.add( "]( successthis pull request" );
			yourList.add( "]( successthis pull request" );
			yourList.add( "]( successthis pull request" );
			
			yourList.add( "( aborted ||" );
			yourList.add( "( aborted ||" );
			yourList.add( "( aborted ||" );
			
			yourList.add( ":+1: ||" );
			yourList.add( ":+1: ||" );
			yourList.add( ":+1: ||" );
			yourList.add( ":+1: ||" );
			yourList.add( ":+1: ||" );
	
			yourList.add( ":+1:  ||" );
			yourList.add( ":+1:  ||" );
			yourList.add( ":+1:  ||" );
			yourList.add( ":+1:  ||" );
			
	
			yourList.add( "👍 ||" );
			yourList.add( "👍 ||" );
			
			yourList.add( "👍  ||" );
			yourList.add( "👍  ||" );
			yourList.add( "👍  ||" );
			
			
			//guava
			yourList.add( "<!-- ok -->" );
			yourList.add( "kevinb@" );
			yourList.add( "boppenheim@" );
			yourList.add( "cgdecker@" );
			yourList.add( "lowasser@" );
			yourList.add( "@lowasser" );
			yourList.add( "fry@" );
			yourList.add( "jlevy@" );
			yourList.add( "cpovirk@" );
			yourList.add( "@cpovirk" );
			yourList.add( "em@" );
			yourList.add( "ek@" );
			yourList.add( "kak@" );
			yourList.add( "we found a contributor license agreement for you (the sender of this pull request) but were unable to find agreements for the commit author(s)  if you authored these maybe you used a different email address in the git commits than was used to sign the cla" );
			yourList.add( "you are receiving this because you were mentioned> > reply to this email directly view it on github>  or mute the> thread>" );
			yourList.add( "bulk closing all pull requests that are listed as needing cla signing if youd like us to look at your pull request youll need to sign the cla and report back hereif this is a false positive i apologize please reopen the pull request and well have a look" );
			yourList.add( "all (the pull request submitter and all commit authors) clas are signed **but** one or more commits were authored or co-authored by someone other than the pull request submitter" );
			yourList.add( "we need to confirm that all authors are ok with their commits being contributed to this project  please have them confirm that by leaving a comment that contains only `@googlebot i consent` in this pull request" );
			yourList.add( "note to project maintainer:* there may be cases where the author cannot leave a comment or the comment is not properly detected as consent  in those cases you can manually confirm consent of the commit author(s) and set the `cla` label to `yes` (if enabled on your project)" );
			yourList.add( "a googler has manually verified that the clas look good(googler please make sure the reason for overriding the cla status is clearly documented in these comments)" );
			yourList.add( "**googlers: [go here]( for more info**" );
			yourList.add( "ℹ️" );
			yourList.add( "**please visit  to sign**> >" );
			yourList.add( "i signed it! ||" );
			yourList.add( "clas look good thanks! ||" );
			yourList.add( "clas look good thanks!ℹ️  ||" );
			yourList.add( "so theres good news and bad news  the good news is that everyone that needs to sign a cla (the pull request submitter and all commit authors) have done so  everything is all good there  the bad news is that it appears that one or more commits were authored or co-authored by someone other than the pull request submitter  we need to confirm that all authors are ok with their commits being contributed to this project  please have them confirm that here in the pull request*note to project maintainer: this is a terminal state meaning the `cla/google` commit status will not change from this state its up to you to confirm consent of all the commit author(s) set the `cla` label to `yes` (if enabled on your project) and then merge this pull request when appropriate* <!-- need_author_consent --> ||" );
			yourList.add( "* **googlers: [go here]( for more info " );
			yourList.add( "if you authored these maybe you used a different email address in the git commits than was used to sign the cla" );
			yourList.add( "we found a contributor license agreement for you (the sender of this pull request) but were unable to find agreements for all the commit author(s) or co-authors" );
			yourList.add( "ok i wont notify you again about this release but will get in touch when a new version is available if youd rather skip all updates until the next major or minor version let me know by commenting `" );
			yourList.add( "so theres good news and bad news  the good news is that everyone that needs to sign a cla (the pull request submitter and all commit authors) have done so  everything is all good there  the bad news is that it appears that one or more commits were authored or co-authored by someone other than the pull request submitter  we need to confirm that all authors are ok with their commits being contributed to this project  please have them confirm that here in the pull request*note to project maintainer: this is a terminal state meaning the `cla/google` commit status will not change from this state its up to you to confirm consent of all the commit author(s) set the `cla` label to `yes` (if enabled on your project) and then merge this pull request when appropriate" );
			yourList.add( "we need to confirm that all authors are ok with their commits being contributed to this project  please have them confirm that by leaving a comment that contains only `  i consent` in this pull request" );		
					
					
			//- [ ] Change in CHANGELOG.md described
			//- [ ] Tests created for changes
			//- [ ] Manually tested changed features in running JabRef
			//- [ ] Screenshots added in PR description (for bigger UI changes)
			//- [ ] Ensured that [the git commit message is a good one](https://github.com/joelparkerhenderson/git_commit_message)
			//- [ ] Check documentation status (Issue created for outdated help page at [help.jabref.org](https://github.com/JabRef/help.jabref.org/issues)?)
			//"[ ]", "[ x ]", "[x]","change changelogmd described", "tests created changes", "manually tested changed features running jabref", "screenshots added pr description bigger ui changes", "ensured [ git commit message good one ]", "httpsgithubcomjoelparkerhendersongitcommitmessage", "check documentation status issue created outdated help page", "httpsgithubcomjabrefhelpjabreforgissues"]
			//<!-- describe the changes you have made here: what  why  ...       Link issues by using the following pattern: [#333](https://github.com/JabRef/jabref/issues/333) or [koppor#49](https://github.com/koppor/jabref/issues/47).      The title of the PR must not reference an issue  because GitHub does not support autolinking there. -->  Fixes the delete action in the maintable branch and the  do you really want to delete the entry -dialog is converted to JavaFX. Moreover  a few lines of JavaFX-Swing-interaction code in  FXDialog  are deleted since it is no longer needed.  ----  - 
				//[ ] Change in CHANGELOG.md described - [ ] Tests created for changes - [x] Manually tested changed features in running JabRef - [ ] Screenshots added in PR description (for bigger UI changes) - 
				//[ ] Ensured that [the git commit message is a good one](https://github.com/joelparkerhenderson/git_commit_message) - [ ] Check documentation status (Issue created for outdated help page at [help.jabref.org](https://github.com/JabRef/help.jabref.org/issues)?)	    
	
			//boolean isFirst = true;
			System.out.println( "Filtering Strings..." );
			
			for (int i =0; i<yourList.size(); i++) {
				String text = "";
				if (cases.equals("LOWER"))
					text = yourList.get(i).toLowerCase();
				else
					text = yourList.get(i);
				int pos = newstr.indexOf(text) ;
				
				if (newstr.indexOf("this code has been reviewed")!= -1) {
					//System.out.println("Debug");
					//System.out.println("comparing: "+text+" - "+newstr);
	
				}
				if (newstr.indexOf("<!-- enter")!= -1) {
					//System.out.println("Debug");
					//System.out.println("comparing: "+text+" - "+newstr);
	
				}
				if (newstr.indexOf("<!-- Other")!= -1) {
					//System.out.println("Debug");
					//System.out.println("comparing: "+text+" - "+newstr);
	
				}
				if (newstr.indexOf("<!-- provide")!= -1) {
					//System.out.println("Debug");
					//System.out.println("comparing: "+text+" - "+newstr);
	
				}
				if (pos != -1) {
					String temp1 = "";
					int tam = text.length();
					//if (isFirst) {
						//temp1 = str.substring(0, pos+tam-1);
						//isFirst = false;
					//} else {
						temp1 = newstr.substring(0, pos);
					//}
					
					newstr = temp1 + " " + newstr.substring(pos+tam, newstr.length());
				}
			}
		}
		return newstr;
	}


	private void insertApriori(ArrayList<String> api) 
	{
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);

		for(int i = 0; i<api.size(); i++) 
		{
			boolean result = fd.insertApriori(pr, java, api.get(i), project, author);
			
			if (!result) 
			{
				System.out.println("Insert apriori failed: "+project +" - "+ pr + " - "+ java + " - "+ api.get(i) + " - " + author);
			}
		}
		
	}
	
	private void insertPr() {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		
		boolean result = fd.insertPr(pr, title, body, project, author);
		if (!result) {
				System.out.println("Insert pr failed: "+ pr + " - "+ title + " - "+ body + " - "+ author);
		}
		
		
	}


	private ArrayList<String> findAPI(String pr2, String java2, String projectName) {

		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		ArrayList<String> gs = fd.buscaAPI(pr2, java2, projectName);

		if (gs==null) {
			System.out.println("pr: "+pr+" - "+java+" not found in database!!!");
		}
		return gs;
	}


	private boolean splitLine(String s) {

		boolean isOk = false;
		int comma = s.indexOf(separator);
		
		if (comma == -1) 
		{
			System.out.println(" line with problems:  first separator missing...");
			return isOk;
		}
		
		pr = s.substring(0, comma);
		int comma1 = s.indexOf(separator, comma+1);
		
		if (comma1 == -1) 
		{
			System.out.println(" line with problems:  second separator missing...");
			return isOk;
		}
		
		java = s.substring(comma+1, comma1);
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		int slash = java.lastIndexOf("/");
		
		if (slash == -1) 
		{
			System.out.println(" line with problems:  path slash missing...");
			return isOk;
		}
		
		java = java.substring(slash+1, java.length());
		int comma2 = s.indexOf(separator, comma1+1);
		
		if (comma2 == -1) 
		{
			System.out.println(" line with problems:  third separator missing...");
			return isOk;
		}
		
		title = s.substring(comma1+1, comma2);
		
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		//int slash = s.lastIndexOf("/");
		//java = s.substring(slash+1, s.length());
		int comma3 = s.indexOf(separator, comma2+1);
		
		if (comma3 == -1) 
		{
			System.out.println(" line with problems:  third separator missing...");
			return isOk;
		}
		
		body = s.substring(comma2+1, comma3);
		author = s.substring(comma3+1, s.length());
		
		pr = pr.trim();
		author = author.trim();
		
		if (pr.equals("884")||pr.contentEquals("96")) 
		{
			System.out.println("debug");
		}
		
		java = java.replace("'","");
		java = java.trim();
		title = filter_text(title, cases);
		
		body = filter_text(body, cases);
		System.out.println("pr: "+pr+" , java: "+java + " title: "+ title + "author: "+ author);
		
		isOk = true;
		
		return isOk;
		
	}
	public String removeUtf8Mb4(String text) {
	    StringBuilder result = new StringBuilder();
	    StringTokenizer st = new StringTokenizer(text, text, true);
	    while (st.hasMoreTokens()) {
	        String current = st.nextToken();
	        if(current.getBytes().length <= 3){
	            result.append(current);
	        }
	    }
	    return result.toString();
	}
	
	private void getAuthors() {
		
		// get FileDAO obj
		FileDAO fd = FileDAO.getInstancia( db, user, pswd );

		// get PRs from database
		ArrayList<Apriori> aps = fd.getApriorisAuthor( project );
		
		// if no PRs found in PR array
		if (aps==null)
		{
			System.out.println("No apriori found!!!");
		}
		else 
		{
			String authorAux 		= null;
			String author 		 	= null;
			
			// create new Apriori obj
			AprioriNew apn  = new AprioriNew();
		
			// loop through array of PRs
			for( int i=0; i<aps.size(); i++ ) 
			{	
				// get PR object and PR num member variable (int) 
				Apriori ap = aps.get(i);
				author = ap.getAuthor();
				
				// on first iteration, set new Apriori obj and prAux to 
				// pr info from above
				if( i==0 ) 
				{ 
					// first case treatment
					apn.setAuthor(author);
					apn.insertGeneral(ap.getGeneral()); // using the general field to store expert 
					authorAux = author;
				}
				else 
				{
					// if pr num is same as during last iteration 
					if (author.equals(authorAux)) 
					{
						apn.insertGeneral(ap.getGeneral());
						if (i+1==aps.size()) 
						{ 
							// last case treatment
							apna.add(apn);
						}
					} 
					else 
					{
						apna.add(apn);
						
						authorAux = author;
						
						apn = new AprioriNew();
						apn.setAuthor(author);
						apn.insertGeneral(ap.getGeneral());
					}
				}
				
			}
			
			// write output
			try 
			{
				FileOutputStream os = new FileOutputStream(outDir+project+"Author_"+csv);
				System.out.println("Writing: "+outDir+project+"Author_"+csv);
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				
				FileOutputStream osc = new FileOutputStream("author"+classes);
				OutputStreamWriter oswc = new OutputStreamWriter(osc);
				BufferedWriter bwc = new BufferedWriter(oswc);
		    	//bw.write("header \n");
				String line = "";
				String lineClasses = "";
				
				
				for (int i=0; i<apna.size(); i++) 
				{
					AprioriNew apnAux = apna.get(i);
					ArrayList<String> gs = apnAux.getGenerals();
					author = apnAux.getAuthor();
					FileDAO dao = FileDAO.getInstancia(db, user, pswd);
					ArrayList <Author> result = dao.getTitleBodyByAuthor(author, project);
					String title = "";
					String body = "";
					int pr;
					String pras = "";
					if (result != null) {
						for (int x = 0; x<result.size(); x++) {
							Author autAux = new Author();
							autAux = result.get(x);
							title = title + " " + autAux.getTitle();
							body = body + " " + autAux.getBody();
							//String author = result.get(2); // I made the query by author, so I don,t need to get author again
							pr =  autAux.getPr();
							pras = pras + " " +  pr;
							/*for (int h=0; h<pr.size(); h++) 
							{
								pras = pras + pr.get(h) + " ";
							}*/
						}
					}
					if (title!=null&&!title.contentEquals("nan")&&!title.equals("NaN")&&!title.isEmpty()){
						if (body!=null&&!body.contentEquals("nan")&&!body.equals("NaN")&&!body.isEmpty()){
							
							title = filter_text(title, cases);
							body = filter_text(body, cases);
							line = line + author;
							lineClasses = lineClasses + author +";";
							//line = line + ","+result.get(0)+ ","+result.get(1);// title and body
							line = line + ","+title+ ","+body+ ","+pras;// title and body + pr
							
							for (int j=0; j<gs.size(); j++) 
							{
								line = line + ","+gs.get(j);
								if (j==(gs.size()-1))
									
									lineClasses = lineClasses + gs.get(j);
								else
									lineClasses = lineClasses + gs.get(j)+"-";
							}
							
							line = line + "\n";
							lineClasses = lineClasses + "\n";
							bw.write(line);
							bwc.write(lineClasses);
						}
					}
					
					line = "";
					lineClasses = "";
		    	}
		    	
				bw.close();
		    	bwc.close();
		    	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
			
	}
	private void genBinarybyAuthors() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = new FileOutputStream(outDir+project+"byAuthor_"+bin);
			System.out.println("Writing " + outDir+project+"byAuthor_"+bin);

			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
	    	//bw.write("header \n");
			String line = "";
			String beginning = "";
			FileDAO dao = FileDAO.getInstancia(db, user, pswd);
			// write header
			line = line + "Author";
			// all general classifications possible
			ArrayList<String> dbGenerals = dao.getDistinctGenerals();
			for (int k=0; k<dbGenerals.size(); k++) {
				line = line + ";"+dbGenerals.get(k);
			}
			line = line + ";pr;Title;Body;prIssue;issue;issueTitle;issueBody;issueComments;issueTitleLink;issueBodyLink;issueCommentsLink;isPR;isTrain;commitMessage;Comments\n";
			line = removeUtf8Mb4(line);				
			bw.write(line);
			
			// end header
			boolean found = false;
			int pr = 0;
			// find classification for each PR
			for (int i=0; i<apna.size(); i++) {
				AprioriNew apnAux = apna.get(i);
				ArrayList<String> gs = apnAux.getGenerals();
				//pr = apnAux.getPr();
				
				author = apnAux.getAuthor();
				//clean data for the next author
				prRes = "";
				issue = "";
				issueTitle = "";
				issueBody = "";
				issueComments = ""; 
				issueTitleLink = "";
				issueBodyLink = "";
				issueCommentsLink = ""; 
				commitMessage = ""; 
				prComments = ""; 
							
				// order line in order of generals generals
				
				ArrayList<String> printLine = new ArrayList();			
				
				// Search for labels to after use zeros and ones
				for (int t=0; t<dbGenerals.size(); t++) {
					for (int j=0; j<gs.size(); j++) {
						if (gs.get(j).equals(dbGenerals.get(t))){
							found = true;
						}
						
					}
					if (found){
						printLine.add(t,"1" );
						found = false;
					}
					else {
						printLine.add(t, "0");
					}
				}
				
				line = "";
				line = line + author;
				
				// add zeros and ones to line to print
				for (int j=0; j<printLine.size(); j++) {
					line = line + ";"+printLine.get(j);
				}
				beginning = line;
				// fill PT title and body
				/*ArrayList result = dao.getTitleBodyByAuthor(author, project);
				String title = (String) result.get(0);
				String body = (String) result.get(1);
				//String author = result.get(2);
				ArrayList prs = (ArrayList) result.get(3);
				if(title.equals("nan")) {
					title="";
				}
				if(body.equals("nan")) {
					body="";
				}
				if(author.equals("nan")) {
					author="";
				}*/
				ArrayList <Author> result = dao.getTitleBodyByAuthor(author, project);
				String title = "";
				String body = "";
				ArrayList prs = new ArrayList();
				//int pr;
				String pras = "";
				if (result != null) {
					for (int x = 0; x<result.size(); x++) {
						Author autAux = new Author();
						autAux = result.get(x);
						title = title + " " + autAux.getTitle();
						body = body + " " + autAux.getBody();
						//String author = result.get(2); // I made the query by author, so I don,t need to get author again
						pr =  autAux.getPr();
						pras = pras + " " +  pr;
						/*for (int h=0; h<pr.size(); h++) 
						{
							pras = pras + pr.get(h) + " ";
						}*/
					}
				}
				//line = line + ";"+title+ ";"+body;// title and body
				
				// get issues
				ArrayList<PrIssue> linkedIssues = new ArrayList();
				ArrayList<PrIssue> linkedIssuesAux = new ArrayList();
				
				for (int j=0; j<prs.size(); j++) {
					int pri = (int) prs.get(j);
					linkedIssuesAux = (dao.getIssues(pri, project));
					for (int v=0; v<linkedIssuesAux.size(); v++) {
						PrIssue aux = linkedIssuesAux.get(v);
						linkedIssues.add(aux);
					}
				}
				
				if (linkedIssues.size()==1) { 
					PrIssue pri = new PrIssue();
					pri = linkedIssues.get(0);
					 
					prRes = pri.getPr();
					 issue = pri.getIssue();
					 issueTitle = pri.getIssueTitle();
					 issueBody = pri.getIssueBody();
					 issueComments  = pri.getIssueComments();
					 issueTitleLink = pri.getIssueTitleLink();
					 issueBodyLink  = pri.getIssueBodyLink();
					 issueCommentsLink  = pri.getIssueCommentsLink();
					 isPR   = pri.getIsPR();
				 
					 isTrain = pri.getIsTrain();   
					 commitMessage  = pri.getCommitMessage();
					 prComments = pri.getPrComments();
					 line = line + ";"+pras +";"+title+ ";"+body ;
					 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
					 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
					line = line + "\n";
					line = removeUtf8Mb4(line);			
					bw.write(line);
					line = "";

				} else { // to generate one pr line with all issues together
					// initialize to accumulate
					if (linkedIssues.size()==0) { 
						  prRes = "";
						  issue = "";
						  issueTitle = "";
						  issueBody = "";
						  issueComments = ""; 
						  issueTitleLink = "";
						  issueBodyLink = "";
						  issueCommentsLink = ""; 
						  isPR = 1; 
						  isTrain = 0; 
						  commitMessage = ""; 
						  prComments = ""; 
							 line = line + ";"+pras +";"+title+ ";"+body ;
							 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
							 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
							line = line + "\n";
							bw.write(line);
							line = "";
	
					}
					else {
						  
						 for (int t=0; t<linkedIssues.size(); t++) { //// to generate one pr line with all issues together
								PrIssue pri = new PrIssue();
								pri = linkedIssues.get(t);
								 isTrain = pri.getIsTrain();   // do not acc
								 if (isTrain == 0) { // if isTrain != 0  we don't need it<------------- we don't need to link with isTrain 0 and isTrain 1! 

									 prRes = prRes= " "+ pri.getPr(); // pr do  acc
									 
									 issue = issue + " "+  pri.getIssue();
									 issueTitle =  issueTitle + " "+ pri.getIssueTitle();// do  acc
									 issueBody = issueBody + " "+ pri.getIssueBody();// do  acc
									 issueComments  = issueComments + " "+ pri.getIssueComments();// do  acc
									 
									 issueTitleLink = issueTitleLink + " "+ pri.getIssueTitleLink();
									 issueBodyLink  =  issueBodyLink + " "+ pri.getIssueBodyLink();
									 issueCommentsLink  = issueCommentsLink + " "+ pri.getIssueCommentsLink();
									 
									 isPR   = pri.getIsPR(); // do not acc
									 	 
									 commitMessage  = commitMessage + " "+ pri.getCommitMessage(); // do  acc
									 prComments = prComments + " "+ pri.getPrComments(); // do nt acc

								 }
						 }
						 line = line + ";"+pras +";"+title+ ";"+body ;// title and body
						 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
						 line = line + "\n";
						 bw.write(line);
						 line = "";
						 
					}
				}
				// concatenate issue data in line
				//line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;

				//line = line + "\n";
				//bw.write(line);
				//line = "";
	    	}
	    	bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void genBinaryExitAuthors() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = new FileOutputStream(outDir+project+"Author_"+bin);
			System.out.println("Writing " + outDir+project+"Author_"+bin);

			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
	    	//bw.write("header \n");
			String line = "";
			String beginning = "";
			FileDAO dao = FileDAO.getInstancia(db, user, pswd);
			// write header
			line = line + "Author";
			// all general classifications possible
			ArrayList<String> dbGenerals = dao.getDistinctGenerals();
			for (int k=0; k<dbGenerals.size(); k++) {
				line = line + ";"+dbGenerals.get(k);
			}
			line = line + ";pr;Title;Body;prIssue;issue;issueTitle;issueBody;issueComments;issueTitleLink;issueBodyLink;issueCommentsLink;isPR;isTrain;commitMessage;Comments\n";
			line = removeUtf8Mb4(line);				
			bw.write(line);
			
			// end header
			boolean found = false;
			int pr = 0;
			// find classification for each PR
			for (int i=0; i<apna.size(); i++) {
				AprioriNew apnAux = apna.get(i);
				ArrayList<String> gs = apnAux.getGenerals();
				//pr = apnAux.getPr();
				
				author = apnAux.getAuthor();
				//clean data for the next author
				prRes = "";
				issue = "";
				issueTitle = "";
				issueBody = "";
				issueComments = ""; 
				issueTitleLink = "";
				issueBodyLink = "";
				issueCommentsLink = ""; 
				commitMessage = ""; 
				prComments = ""; 
							
				// order line in order of generals generals
				
				ArrayList<String> printLine = new ArrayList();			
				
				// Search for labels to after use zeros and ones
				for (int t=0; t<dbGenerals.size(); t++) {
					for (int j=0; j<gs.size(); j++) {
						if (gs.get(j).equals(dbGenerals.get(t))){
							found = true;
						}
						
					}
					if (found){
						printLine.add(t,"1" );
						found = false;
					}
					else {
						printLine.add(t, "0");
					}
				}
				
				line = "";
				line = line + author;
				
				// add zeros and ones to line to print
				for (int j=0; j<printLine.size(); j++) {
					line = line + ";"+printLine.get(j);
				}
				beginning = line;
				// fill PT title and body
				ArrayList <Author> result = dao.getTitleBodyByAuthor(author, project);
				ArrayList prs = new ArrayList();
				String title = "";
				String body = "";
				//int pr;
				String pras = "";
				if (result == null) {
					continue;
				}
				for (int x = 0; x<result.size(); x++) {
					Author autAux = new Author();
					autAux = result.get(x);
					title = title + " " + autAux.getTitle();
					body = body + " " + autAux.getBody();
					//String author = result.get(2); // I made the query by author, so I don,t need to get author again
					pr =  autAux.getPr();
					prs.add(pr);
					pras = pras + " " +  pr;
					/*for (int h=0; h<pr.size(); h++) 
					{
						pras = pras + pr.get(h) + " ";
					}*/
				}//				ArrayList result = dao.getTitleBodyExitAuthor(author, project);
				//String title = null;
				//String body = null; 
				
				//if (title=="" || body== "") { 
				//	continue;
				//}
				//title = (String) result.get(0);
				//body = (String) result.get(1);
				//String author = result.get(2);
				//ArrayList prs = (ArrayList) result.get(3);
				if(title.equals("nan")) {
					title="";
				}
				if(body.equals("nan")) {
					body="";
				}
				if(author.equals("nan")) {
					author="";
				}
				//line = line + ";"+title+ ";"+body;// title and body
				
				// get issues
				ArrayList<PrIssue> linkedIssues = new ArrayList();
				ArrayList<PrIssue> linkedIssuesAux = new ArrayList();
				
				for (int j=0; j<prs.size(); j++) {
					int pri = (int) prs.get(j);
					linkedIssuesAux = (dao.getIssues(pri, project));
					for (int v=0; v<linkedIssuesAux.size(); v++) {
						PrIssue aux = linkedIssuesAux.get(v);
						linkedIssues.add(aux);
					}
				}
				
				if (linkedIssues.size()==1) { 
					PrIssue pri = new PrIssue();
					pri = linkedIssues.get(0);
					 
					prRes = pri.getPr();
					 issue = pri.getIssue();
					 issueTitle = pri.getIssueTitle();
					 issueBody = pri.getIssueBody();
					 issueComments  = pri.getIssueComments();
					 issueTitleLink = pri.getIssueTitleLink();
					 issueBodyLink  = pri.getIssueBodyLink();
					 issueCommentsLink  = pri.getIssueCommentsLink();
					 isPR   = pri.getIsPR();
				 
					 isTrain = pri.getIsTrain();   
					 commitMessage  = pri.getCommitMessage();
					 prComments = pri.getPrComments();
					 line = line + ";"+pras +";"+title+ ";"+body;
					 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
					 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
					line = line + "\n";
					line = removeUtf8Mb4(line);			
					bw.write(line);
					line = "";

				} else { // to generate one pr line with all issues together
					// initialize to accumulate
					if (linkedIssues.size()==0) { 
						  prRes = "";
						  issue = "";
						  issueTitle = "";
						  issueBody = "";
						  issueComments = ""; 
						  issueTitleLink = "";
						  issueBodyLink = "";
						  issueCommentsLink = ""; 
						  isPR = 1; 
						  isTrain = 0; 
						  commitMessage = ""; 
						  prComments = ""; 
							 line = line + ";"+pras + ";"+title+ ";"+body ;
							 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  
							 issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
							line = line + "\n";
							bw.write(line);
							line = "";
	
					}
					else {
						  
						 for (int t=0; t<linkedIssues.size(); t++) { //// to generate one pr line with all issues together
								PrIssue pri = new PrIssue();
								pri = linkedIssues.get(t);
								 isTrain = pri.getIsTrain();   // do not acc
								 if (isTrain == 0) { // if isTrain != 0  we don't need it<------------- we don't need to link with isTrain 0 and isTrain 1! 

									 prRes = prRes= " "+ pri.getPr(); // pr do  acc
									 
									 issue = issue + " "+  pri.getIssue();
									 issueTitle =  issueTitle + " "+ pri.getIssueTitle();// do  acc
									 issueBody = issueBody + " "+ pri.getIssueBody();// do  acc
									 issueComments  = issueComments + " "+ pri.getIssueComments();// do  acc
									 
									 issueTitleLink = issueTitleLink + " "+ pri.getIssueTitleLink();
									 issueBodyLink  =  issueBodyLink + " "+ pri.getIssueBodyLink();
									 issueCommentsLink  = issueCommentsLink + " "+ pri.getIssueCommentsLink();
									 
									 isPR   = pri.getIsPR(); // do not acc
									 	 
									 commitMessage  = commitMessage + " "+ pri.getCommitMessage(); // do  acc
									 prComments = prComments + " "+ pri.getPrComments(); // do nt acc

								 }
						 }
						 line = line + ";" + pras + ";"+title+ ";"+body ;// title and body
						 line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;
						 line = line + "\n";
						 bw.write(line);
						 line = "";
						 
					}
				}
				// concatenate issue data in line
				//line = line + ";" +prRes+";"+ issue+";"+  issueTitle+";"+  issueBody+";"+  issueComments+";"+   issueTitleLink+";"+  issueBodyLink+";"+  issueCommentsLink+";"+   isPR+";"+    isTrain+";"+    commitMessage +";"+ prComments ;

				//line = line + "\n";
				//bw.write(line);
				//line = "";
	    	}
	    	bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
