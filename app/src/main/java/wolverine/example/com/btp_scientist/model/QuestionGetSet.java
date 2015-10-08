package wolverine.example.com.btp_scientist.model;

public class QuestionGetSet {
    private String question;
    private String date;
    private String number;
    private Integer imaId;
 
    public QuestionGetSet() {
	}
 
    public QuestionGetSet(String question, String date, String number,Integer imaId) {
        this.question = question;
        this.date = date;
        this.number = number;
        this.imaId = imaId;
    }
 
    public String getQues() {
        return question;
    }
 
    public void setQues(String name) {
        this.question = name;
    }
 
    public String getDate() {
        return date;
    }
 
    public void setDate(String date) {
        this.date = date;
    }
 
    public String getNumber() {
        return number;
    }
 
    public void setNumber(String number) {
        this.number = number;
    }
 
}