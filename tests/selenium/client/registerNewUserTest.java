package myPackages;

//Unit test checking user creation. The name accepts any characters, the username and email may not be used. Password has no requirements.

import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class registerNewUserTest {
	private static  WebDriver driver;
	
	public static String URL_CHATLOCKER_MAIN = "http://localhost:3000";
	public static String ERROR_EMPTY_NAME = "The name must not be empty";
	public static String ERROR_EMPTY_PASS = "The password must not be empty";
	public static String ERROR_INVALID_EMAIL = "The email entered is invalid";
	public static String ERROR_INVALID_PASS;
	public static String ERROR_PASSWORDS_NO_MATCH = "The password confirmation does not match password";
	public static String TEXT_HOME_TITLE = "Home";
	public static String TEXT_REGISTER_DISPLAY_NAME_HEADER = "Register username";
	public static String ERROR_DISPLAY_NAME_EXISTS = "is already in use :(";

	
	private static By nameFieldLocator = By.id("name");
	private static By emailFieldLocator = By.id("email");
	private static By passwordFieldLocator = By.id("pass");
	private static By passwordConfirmFieldLocator = By.id("confirm-pass");
	private static By registerNewAccLinkLocator = By.className("register");
	private static By registerBtnLocator = By.xpath("//*[@id=\"login-card\"]/div[2]/button/span");
	private static By displayUsernameLocator = By.id("username");
	private static By useThisUsernameBtnLocator = By.xpath("//*[@id=\"login-card\"]/div[2]/button");
	private static By roomTitleLocator = By.className("room-title");
	private static By confirmUsernameTitlePageLocator = By.cssSelector("h2");
	private static By confirmDisplayNameErrorLocator = By.className("alert alert-danger");
	
	private static String accountName;							//this will be the name holder of the account, used Unit Test to identify which usernames are generated by this script
	private static String accountUsername;  					//this will be the same as the account name
	private static String accountEmail;							//without @gmail.com
	private static String accountEmailFull; 					//with @gmail.com 
	private static String accountPass = "adrian"; 				//for confirm pass failures well just use space
	private static String existingDisplayName = "rocket.cat";	//this is the default display name of the account thati s automatically created by Rocket Chat
	
	private By errorFieldLocator = By.className("input-error");
	private String nameError;
	private String emailError;
	private String passwordError;
	private String passwordConfirmError;
	
	private static void generateTestAccountCredentials(){
		accountEmail = UUID.randomUUID().toString().substring(5,15);	//we need to generate a unique email every test run
		accountUsername = accountEmail.substring(0,10);
		accountName = accountUsername;
		accountEmailFull = accountEmail + "@gmail.com";
	}
	
	private void autoFillForm(){
		clearForm();
		register();
		captureErrorMessages();
	}
	
	private void autoFillForm(String name, String email, String pass, String confirmPass){
		clearForm();
		driver.findElement(nameFieldLocator).sendKeys(name);
		driver.findElement(emailFieldLocator).sendKeys(email);
		driver.findElement(passwordFieldLocator).sendKeys(pass);
		driver.findElement(passwordConfirmFieldLocator).sendKeys(confirmPass);
		register();
		captureErrorMessages();
	}
	
	private void autoFillForOnly(String fieldType){
		
		switch(fieldType) {
		   case "name" :
			   	clearForm();
			   	driver.findElement(nameFieldLocator).sendKeys(accountName);
		   
		   case "shortemail" :
			   	clearForm();
			   	driver.findElement(emailFieldLocator).sendKeys(accountEmail);

		   case "fullEmail" :
			   	clearForm();
			   	driver.findElement(emailFieldLocator).sendKeys(accountEmailFull);
		      
		   case "firstPassword" :
			   	clearForm();
			   	driver.findElement(passwordFieldLocator).sendKeys(accountPass);
			   	
		   case "confirmPassword" :
			   	clearForm();
			   	driver.findElement(passwordConfirmFieldLocator).sendKeys(accountPass);
		   default : // Optional
			   register();
			   captureErrorMessages();
		}
		
	}

	
	private void clearForm(){
		driver.findElement(nameFieldLocator).clear();
		driver.findElement(emailFieldLocator).clear();
		driver.findElement(passwordFieldLocator).clear();
		driver.findElement(passwordConfirmFieldLocator).clear();
	}
	
	private void register(){
		driver.findElement(registerBtnLocator).click();
	}
	
	private void confirmUsername(){
		driver.findElement(useThisUsernameBtnLocator).click();
	}
	
	private void captureErrorMessages(){
		nameError = driver.findElements(errorFieldLocator).get(0).getText();
		emailError = driver.findElements(errorFieldLocator).get(1).getText();
		passwordError = driver.findElements(errorFieldLocator).get(2).getText();
		passwordConfirmError = driver.findElements(errorFieldLocator).get(3).getText();
	}
	
	private void logoutAndReturnToRegistration(){
		driver.get(URL_CHATLOCKER_MAIN);
		WebElement registerLink = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(registerNewAccLinkLocator));
		registerLink.click();
	}
	
	
	@BeforeClass
	public static void beforeClass(){
		driver = new SafariDriver();
		driver.get(URL_CHATLOCKER_MAIN);
		WebElement registerLink = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(registerNewAccLinkLocator));
		registerLink.click();
		generateTestAccountCredentials();
	
	}
	
	//this has been moved to AllTests.java
	@AfterClass
	public static void afterClass(){
		driver.close();
		driver.quit();			
	}
	
	@Before
	public void beforeTestsCheckIfAtRegistrationPage(){
		new WebDriverWait(driver, 3).until(ExpectedConditions.presenceOfElementLocated(nameFieldLocator));
	}
	
	@Test
	public void enterAllEmptyFieldsShouldFail(){
		autoFillForm();
		Assert.assertEquals(nameError, ERROR_EMPTY_NAME);
	}
	
	@Test
	public void enterOnlyValidNameShouldFail(){
		autoFillForOnly("name");
		Assert.assertEquals(emailError, ERROR_INVALID_EMAIL);
	}
	
	@Test
	public void enterOnlyAnIncompleteEmailShouldFail(){
		autoFillForOnly("shortemail");
		Assert.assertEquals(emailError, ERROR_INVALID_EMAIL);
	}
	
	@Test
	public void enterOnlyEmailShouldFail(){
		autoFillForOnly("fullEmail");
		Assert.assertEquals(nameError, ERROR_EMPTY_NAME);
	}
	
	@Test
	public void enterOnlyPassShouldFail(){
		autoFillForOnly("firstPassword");
		Assert.assertEquals(passwordConfirmError, ERROR_PASSWORDS_NO_MATCH);
	}
	
	@Test
	public void enterOnlyConfirmPassShouldFail(){
		
		autoFillForOnly("confirmPassword");
		Assert.assertEquals(passwordError, ERROR_EMPTY_PASS);
	}
	
	@Test
	public void enterAllWithInvalidEmailFieldShouldFail(){
		autoFillForm(accountName,accountEmail, accountPass, accountPass);
		Assert.assertEquals(emailError, ERROR_INVALID_EMAIL);
	}
	
	@Test
	public void enterAllValidFieldsShouldGoToConfirmUsernamePageShouldPass(){
		generateTestAccountCredentials();
		autoFillForm(accountName,accountEmailFull, accountPass, accountPass);
		WebElement h2Title = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(confirmUsernameTitlePageLocator));
		Assert.assertEquals(h2Title.getText(), TEXT_REGISTER_DISPLAY_NAME_HEADER);
		logoutAndReturnToRegistration();
	}
	
	@Test
	public void AfterSuccessAccCreationShouldGoToConfirmUsernameSelectValidDisplayNameShouldPass(){
		generateTestAccountCredentials();
		autoFillForm(accountName,accountEmailFull, accountPass, accountPass);
		new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(confirmUsernameTitlePageLocator));
		confirmUsername();
		String title = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(roomTitleLocator)).getText();
		Assert.assertEquals(title, TEXT_HOME_TITLE);
		logoutAndReturnToRegistration();
	}	
	
	@Test
	public void AfterSuccessAccCreationShouldGoToConfirmUsernameSelectInvalidDisplayNameShouldFail(){
		generateTestAccountCredentials();	
		autoFillForm(accountName,accountEmailFull, accountPass, accountPass);
		new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(confirmUsernameTitlePageLocator));
		driver.findElement(displayUsernameLocator).clear();
		driver.findElement(displayUsernameLocator).sendKeys(existingDisplayName);	//should be invalid as an account of displayname adrian already exists
		confirmUsername();
		new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(confirmDisplayNameErrorLocator));
		String errorMessage = driver.findElement(confirmDisplayNameErrorLocator).getText();
		Assert.assertThat(errorMessage, CoreMatchers.containsString(ERROR_DISPLAY_NAME_EXISTS));
		logoutAndReturnToRegistration();
	}	
	

}
