package cfranking.model;

public class TeamResult {
	
	private String name;
	private String result;
	private int margin;
	private String opponent;
	
	public TeamResult(String name, String result, int margin, String opponent){
		this.name = name;
		this.result = result;
		this.margin = margin;
		this.opponent = opponent;
	}
	
	public String getName(){
		return name;
	}
	
	public String getResult(){
		return result;
	}
	
	public int getMargin(){
		return margin;
	}
	
	public String getOpponent(){
		return opponent;
	}
	
	public void setName(String name){
		this.name = name.toLowerCase();
	}
	
	public void setResult(String result){
		this.result = result.toLowerCase();
	}
	
	public void setMargin(int margin){
		this.margin = margin;
	}
	
	public void setOpponent(String opponent){
		this.opponent = opponent.toLowerCase();
	}

}
