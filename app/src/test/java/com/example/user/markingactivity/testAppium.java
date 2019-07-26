package com.example.user.markingactivity;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;

import static org.junit.Assert.assertEquals;


public class testAppium {
    public AndroidDriver driver;
    public WebDriverWait wait;

    @Before
    public void setUp () throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "Alps Z28S");
        caps.setCapability("udid", "0123456789ABCDEF"); //DeviceId from "adb devices" command
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "7.1.1");
        caps.setCapability("skipUnlock","true");
        caps.setCapability("appPackage", "com.example.user.markingactivity");
        caps.setCapability("appActivity","com.example.user.markingactivity.activity.ProjectSelectActivity");
        caps.setCapability("noReset","true");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),caps);
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testUsernameChanged () throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.user.markingactivity:id/action_login"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).clear();
        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).sendKeys("fakeName");
        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout[2]/android.view.ViewGroup/android.widget.RelativeLayout/android.widget.Button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.user.markingactivity:id/action_login"))).click();

        String username = wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).getText();


        assertEquals("fakeName", username);

        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).clear();
        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).sendKeys("admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout[2]/android.view.ViewGroup/android.widget.RelativeLayout/android.widget.Button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.example.user.markingactivity:id/action_login"))).click();

        username = wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.id("com.example.user.markingactivity:id/et_username"))).getText();

        assertEquals("admin", username);
    }

    @After
    public void tearDown(){
        driver.quit();
    }
}


