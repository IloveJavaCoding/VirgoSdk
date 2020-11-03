package com.nepalese.virgosdk.Example.ParseXml1;

public class Student {
    public static final String NAME = "name";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String GRADE = "grade";
    
	private String name;
    private String gender;
    private int age;
    private int grade;

    public Student(){}

    public Student(String name, String gender, int age, int grade) {
		super();
		this.name = name;
		this.gender = gender;
		this.age = age;
		this.grade = grade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGrade() {
		return grade;
	}
	
	public void setGrade(int grade) {
		this.grade = grade;
	}

	public void setValue(String field, String value){
        switch (field){
	        case NAME:
	            name = value;
	            break;
	        case GENDER:
	            gender = value;
	            break;
	        case AGE:
	            age = Integer.parseInt(value);
	            break;
	        case GRADE:
	            grade = Integer.parseInt(value);
	            break;
        }
    }
    
    
    @Override
	public String toString() {
		return "Student [name=" + name + ", gender=" + gender + ", age=" + age + ", grade=" + grade + "]";
	}
}
