package model;

public class PrIssue {
	private String pr = "";
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
	public String getPrComments() {
		return prComments;
	}
	public void setPrComments(String prComments) {
		this.prComments = prComments;
	}
	public PrIssue() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PrIssue(String pr, String issue, String issueTitle, String issueBody, String issueComments,
			String issueTitleLink, String issueBodyLink, String issueCommentsLink, int isPR, int isTrain,
			String commitMessage) {
		super();
		this.pr = pr;
		this.issue = issue;
		this.issueTitle = issueTitle;
		this.issueBody = issueBody;
		this.issueComments = issueComments;
		this.issueTitleLink = issueTitleLink;
		this.issueBodyLink = issueBodyLink;
		this.issueCommentsLink = issueCommentsLink;
		this.isPR = isPR;
		this.isTrain = isTrain;
		this.commitMessage = commitMessage;
	}
	public String getPr() {
		return pr;
	}
	public void setPr(String pr) {
		this.pr = pr;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getIssueTitle() {
		return issueTitle;
	}
	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}
	public String getIssueBody() {
		return issueBody;
	}
	public void setIssueBody(String issueBody) {
		this.issueBody = issueBody;
	}
	public String getIssueComments() {
		return issueComments;
	}
	public void setIssueComments(String issueComments) {
		this.issueComments = issueComments;
	}
	public String getIssueTitleLink() {
		return issueTitleLink;
	}
	public void setIssueTitleLink(String issueTitleLink) {
		this.issueTitleLink = issueTitleLink;
	}
	public String getIssueBodyLink() {
		return issueBodyLink;
	}
	public void setIssueBodyLink(String issueBodyLink) {
		this.issueBodyLink = issueBodyLink;
	}
	public String getIssueCommentsLink() {
		return issueCommentsLink;
	}
	public void setIssueCommentsLink(String issueCommentsLink) {
		this.issueCommentsLink = issueCommentsLink;
	}
	public int getIsPR() {
		return isPR;
	}
	public void setIsPR(int isPR) {
		this.isPR = isPR;
	}
	public int getIsTrain() {
		return isTrain;
	}
	public void setIsTrain(int isTrain) {
		this.isTrain = isTrain;
	}
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

}
