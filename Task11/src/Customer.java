
public class Customer {
	private int type;
	private int lifeStyle;
	private double vacation;
	private double eCredit;
	private double salary;
	private double property;
	private int product;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLifeStyle() {
		return lifeStyle;
	}
	public void setLifeStyle(int lifeStyle) {
		this.lifeStyle = lifeStyle;
	}
	public double getVacation() {
		return vacation;
	}
	public void setVacation(double vacation) {
		this.vacation = vacation;
	}
	public double geteCredit() {
		return eCredit;
	}
	public void seteCredit(double eCredit) {
		this.eCredit = eCredit;
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public double getProperty() {
		return property;
	}
	public void setProperty(double property) {
		this.property = property;
	}
	public int getProduct() {
		return product;
	}
	public void setProduct(int product) {
		this.product = product;
	}
	public String toString() {
		return "type:" + type + ",lifestyle:" + lifeStyle + ",vacation:" + vacation + ",eCredit:" + eCredit + ",Salary:" + salary + "Property:" + property + ",Product:" + product;
	}
}
