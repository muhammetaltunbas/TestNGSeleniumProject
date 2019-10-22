package chrome;

import org.testng.annotations.Test;
import util.GetDataFromExcel;
import util.Screenshot;

import org.testng.annotations.BeforeTest;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

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

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test; // JUnit ile çalıştırmak istersek =>  import org.junit.annotations.Test; i kullanmamız gerekir.
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.testng.ITestResult;

public class SeleniumTestngExtentReport {

	private WebDriver driver;
	static ExtentHtmlReporter reporter = new ExtentHtmlReporter("./Reports/extentreport.html");
	static ExtentReports extent = new ExtentReports();
	ExtentTest logger,logger2;//logger2 screenshot icin

	@DataProvider(name = "MamiTest")
	public Object[][] testData() throws Exception {
		return GetDataFromExcel.getDataFromExcel(GetDataFromExcel.FILE_PATH, GetDataFromExcel.SHEET_NAME,
				GetDataFromExcel.TABLE_NAME);
	}

	@BeforeMethod
	public void launchBrowser() throws Exception {
		String exePath = "D:\\SeleniumChromeDriver\\chromedriver.exe";
		ChromeOptions options = new ChromeOptions();// İzin sorunu İçin eklendi.
		options.setExperimentalOption("useAutomationExtension", false);// İzin sorunu için eklendi.

		options.addArguments("-incognito");// Bu satır ve sıradaki 2 satır tarayıcının gizli modda açılmasını sağlar.
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();// Gizli Mod
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);// Gizli Mod

		System.setProperty("webdriver.chrome.driver", exePath);
		driver = new ChromeDriver(options);// Normalde burada options olmayacak. Fakat pc izin ile ilgili.

		String URL = "http://demo.guru99.com/V4/";
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@Test(dataProvider = "MamiTest")
	public void loginTest(String username, String password) throws Throwable {

		String actualBoxMsg;
		driver.findElement(By.name("uid")).clear();
		driver.findElement(By.name("uid")).sendKeys(username);
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.name("btnLogin")).click();
		extent.attachReporter(reporter);
		ExtentTest logger = extent.createTest("Login Testi");
		try {

			logger.log(Status.INFO, "Login to Guru99");
			Alert alt = driver.switchTo().alert();
			actualBoxMsg = alt.getText(); // get content of the Alter Message
			alt.accept();

			// Compare Error Text with Expected Error Value
			if (!actualBoxMsg.equalsIgnoreCase(GetDataFromExcel.EXPECT_ERROR)) {
				logger.log(Status.FAIL,
						"User could not login because of username and password is wrong. In addition, Expected error is also wrong.");
			}
			assertEquals(actualBoxMsg, GetDataFromExcel.EXPECT_ERROR);// Tesng
			logger.log(Status.PASS,
					"User COULD not login.Because username and password is wrong. But expected error is wright.");
			extent.flush();
		} catch (NoAlertPresentException Ex) {
			logger.log(Status.PASS, "User could login.");
			extent.flush();
		}

	}

	@Test
	public void checkTitle() throws Throwable {

		driver.findElement(By.name("uid")).clear();
		driver.findElement(By.name("uid")).sendKeys("mngr228299");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("jebynEt");
		driver.findElement(By.name("btnLogin")).click();

		extent.attachReporter(reporter);
		logger = extent.createTest("Check Title");
		logger.log(Status.INFO, "Check Title of Guru99");
		String realTitle = "Guru99 Bank Manager HomePage";

		try {

			assertEquals(realTitle, GetDataFromExcel.EXPECT_TITLE);// Testng icin
			logger.log(Status.PASS, "Title Is Correct");
			extent.flush();
		} catch (AssertionError Ex) {
			logger.log(Status.FAIL, "Title Is Not Correct");
			extent.flush();
		}
		assertEquals(realTitle, GetDataFromExcel.EXPECT_TITLE);// Try block içerisinde, title değerleri uyusmadiğinda,
																// TestNG failed olarak gösterilmediğinden buraya tekrar
																// ekledim.

	}

	@AfterMethod
	public void getResult(ITestResult result) throws IOException {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				String screenShotPath = Screenshot.capture(driver, "ScreenshotOfTest");
				logger2.log(Status.FAIL, result.getThrowable());
				logger2.log(Status.FAIL, "Screenshot Below: " + logger2.addScreenCaptureFromPath(screenShotPath));
			}
		} catch (Exception ex) {

		}

		driver.quit();
	}

	@AfterTest
	public void endReport() {
		extent.flush();
	}

}
