package main;

public class Token {
	protected String word;
	protected String base;
	protected String POS;
	
	public Token (String word, String base, String POS) {
		this.word = word;
		this.base = base;
		this.POS = POS;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getPOS() {
		return POS;
	}

	public void setPOS(String pOS) {
		POS = pOS;
	}
}
