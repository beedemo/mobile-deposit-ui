package com.cloudbees.examples.bank.demo;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Demonstrates how to write a JUnit test that runs tests against Selenium
 * using multiple browsers in parallel.
 * <p/>
 * 
 * @author Andy Pemberton, Kurt Madel
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MobileDepositFunctionalTests  {

	@LocalServerPort
	int port;

    @Value("${test.host}")
    private String testHost;

    @Value("${test.browser.name}")
    private String testBrowserName;

    @Value("${test.browser.version}")
    private String testBrowserVersion;


	/**
	 * Represents the browser to be used as part of the test run.
	 */
	private String browser;
	/**
	 * Represents the operating system to be used as part of the test run.
	 */
	private String os;
	/**
	 * Represents the version of the browser to be used as part of the test run.
	 */
	private String version;

	/**
	 * The {@link WebDriver} instance which is used to perform browser
	 * interactions with.
	 */
	private RemoteWebDriver driver;


	/**
	 * Constructs a new {@link RemoteWebDriver} instance which is configured to
	 * use the capabilities defined by the {@link #browser}, {@link #version}
	 * and {@link #os} instance variables, and which is configured to run
	 * against a standalone selenium docker container
	 * 
	 * @throws Exception
	 *             if an error occurs during the creation of the
	 *             {@link RemoteWebDriver} instance.
	 */
	@Before
	public void setUp() throws Exception {
        System.out.println("testHost: " + testHost);
		DesiredCapabilities browser = new DesiredCapabilities();
		browser.setBrowserName(testBrowserName);
		browser.setVersion(testBrowserVersion);
		driver = new RemoteWebDriver(
				URI.create("http://" + testHost + ":4444/wd/hub").toURL(),
				browser
		);
	}

	/**
	 * Make sure the page has an account number
	 * 
	 * @throws Exception
	 */
	@Test
	public void hasAnAccountNumber() throws Exception {
		String depositUrl = "http://" + testHost + ":" + port + "/deposit/";
		driver.get(depositUrl);

		//get screenshot to allow manual check for image
		File screenshot = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenshot, new File("./screenshot-hasAnAccountNumber-" + testBrowserName + "-" + testBrowserVersion + ".png"));

		assertNotNull(driver.findElement(By.className("account-number")));
	}

	/*
	@Test
	public void checkBannerImage() throws Exception {
		String depositUrl = "http://" + testHost + ":" + port + "/deposit/";
		driver.get(depositUrl);

		//get screenshot to allow manual check for image
		File screenshot = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenshot, new File("./screenshot-checkBannerImage-" + testBrowserName + "-" + testBrowserVersion + ".png"));

		Boolean ImagePresent = Boolean.TRUE;
		//currently must manually test for firefox
		if(testBrowserName != "firefox") {
			try {
				WebElement ImageFile = driver.findElement(By.xpath("//img[@id='jenkins-logo']"));
				ImagePresent = (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", ImageFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		assertTrue(ImagePresent);
	}
	*/

	// @Test
	// public void hasMaskedAccountNumber() throws Exception {
	// driver.get("http://localhost:" + port + "deposit"); // TODO parameterize
	// WebElement accountNumber = driver.findElement(By
	// .className("account-number"));
	// assertTrue("Account Number must end and only contain 4 digits!",
	// Pattern.matches("([^\\d]*)([\\d]{4})", accountNumber.getText()));
	// }

	/**
	 * Closes the {@link WebDriver} session.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

}
