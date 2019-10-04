package TestNGPackage;

import java.io.File;
import java.util.ArrayList;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mami.base.GetDataFromExcel;

import jxl.Workbook;
import jxl.Cell;
import jxl.Sheet;

public class UtilTestNG {
	static WebDriver driver;

	public static void launchBrowser() throws Exception {
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
		//System.out.println("Title of Page: " + driver.getTitle());

	}
	public static void GetExcelDataTest() throws Throwable {
		String username, password;
		String actualTitle;
		String actualBoxtitle;

		String[][] testData = GetDataFromExcel.getDataFromExcel(GetDataFromExcel.FILE_PATH, GetDataFromExcel.SHEET_NAME,
				GetDataFromExcel.TABLE_NAME);

		for (int i = 0; i < testData.length; i++) {
			username = testData[i][0]; // get username
			password = testData[i][1]; // get password

			launchBrowser();//Tarayýcý kapandýktan sonra tekrar açýlýrki for döngüsü ile sürec devam etsin.

			driver.findElement(By.name("uid")).clear();
			driver.findElement(By.name("uid")).sendKeys(username);
			driver.findElement(By.name("password")).clear();
			driver.findElement(By.name("password")).sendKeys(password);
			driver.findElement(By.name("btnLogin")).click();

			try {

				Alert alt = driver.switchTo().alert();
				actualBoxtitle = alt.getText(); // get content of the Alter Message
				alt.accept();
				if (actualBoxtitle.contains(GetDataFromExcel.EXPECT_ERROR)) { // Compare Error Text with Expected Error
																			// Value
					System.out.println("Test case SS[" + i + "]: Passed with expected Error");
				} else {
					System.out.println("Test case SS[" + i + "]: Failed");
				}
			} catch (NoAlertPresentException Ex) {
				actualTitle = driver.getTitle();
				// On Successful login compare Actual Page Title with Expected Title
				if (actualTitle.contains(GetDataFromExcel.EXPECT_TITLE)) {
					System.out.println("Test case SS[" + i + "]: Passed");
				} else {
					System.out.println("Test case SS[" + i + "]: Failed");
				}

			}
			driver.close();
		}

	}
}
