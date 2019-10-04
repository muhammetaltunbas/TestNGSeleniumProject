package TestNGPackage;

import org.testng.annotations.Test;
import com.mami.base.GetDataFromExcel;
import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

public class SeleniumTestNG {

	private WebDriver driver;

	@DataProvider(name = "MamiTest")
	public Object[][] testData() throws Exception {
		return GetDataFromExcel.getDataFromExcel(GetDataFromExcel.FILE_PATH, GetDataFromExcel.SHEET_NAME,
				GetDataFromExcel.TABLE_NAME);
	}

	@BeforeMethod
	public void launchBrowser() throws Exception {
		String exePath = "d:\\users\\918351\\Downloads\\SeleniumCrome\\chromedriver.exe";// D:\CromeDriver(ForSelenium)
		ChromeOptions options = new ChromeOptions();// Ýzin sorunu Ýçin eklendi.
		options.setExperimentalOption("useAutomationExtension", false);// Ýzin sorunu için eklendi.

		options.addArguments("-incognito");// Bu satýr ve sýradaki 2 satýr tarayýcýnýn gizli modda açýlmasýný saðlar.
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();// Gizli Mod
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);// Gizli Mod

		System.setProperty("webdriver.chrome.driver", exePath);
		driver = new ChromeDriver(options);// Normalde burada options olmayacak. Fakat pc izin ile ilgili

		String URL = "http://demo.guru99.com/V4/";
		driver.get(URL);
		driver.manage().window().maximize();
	}
	// System.out.println("Title of Page: " + driver.getTitle());

	@Test(dataProvider = "MamiTest")
	public void runapp(String username, String password) throws Throwable {

		//UtilTestNG.GetExcelDataTest();
		String actualTitle;
		String actualBoxMsg;
		driver.findElement(By.name("uid")).clear();
		driver.findElement(By.name("uid")).sendKeys(username);
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.name("btnLogin")).click();

	    try{ 
	    
	       	Alert alt = driver.switchTo().alert();
			actualBoxMsg = alt.getText(); // get content of the Alter Message
			alt.accept();
			 // Compare Error Text with Expected Error Value					
			assertEquals(actualBoxMsg,GetDataFromExcel.EXPECT_ERROR);
			
		}    
	    catch (NoAlertPresentException Ex){ 
	    	actualTitle = driver.getTitle();
			// On Successful login compare Actual Page Title with Expected Title
	    	assertEquals(actualTitle,GetDataFromExcel.EXPECT_TITLE);
        } 

	}
	@AfterMethod
	public void closeBrowser() throws Exception {
		driver.quit();
	}

}
