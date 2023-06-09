package itemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Date;

public class ItemTest {

    ChromeDriver chrome;

    String projectName;

    Actions action;

    @BeforeEach
    public void openBrowser() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/driver/chromedriver.exe");
        chrome = new ChromeDriver();
        action = new Actions(chrome);

        // implicit wait
        chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        chrome.manage().window().maximize();

        chrome.get("https://www.todo.ly/");

        // login
        chrome.findElement(By.xpath("//img[@src=\"/Images/design/pagelogin.png\"]")).click();
        chrome.findElement(By.id("ctl00_MainContent_LoginControl1_TextBoxEmail")).sendKeys("dylan@gmail.com");
        chrome.findElement(By.id("ctl00_MainContent_LoginControl1_TextBoxPassword")).sendKeys("dylan123");
        chrome.findElement(By.id("ctl00_MainContent_LoginControl1_ButtonLogin")).click();

        projectName = "Dylan Project - " + new Date().getTime();

        // create project
        chrome.findElement(By.xpath("//td[text()='Add New Project']")).click();
        chrome.findElement(By.id("NewProjNameInput")).sendKeys(projectName);
        chrome.findElement(By.id("NewProjNameButton")).click();

        chrome.findElement(By.xpath("//td[text()='" + projectName + "']")).click();
    }

    @AfterEach
    public void closeBrowser() {
        chrome.quit();
    }

    @Test
    public void verifyUpdateAndDeleteProject() throws InterruptedException {
        String itemName = "Dylan Item - " + new Date().getTime();
        // create item
        chrome.findElement(By.xpath("//textarea[@id='NewItemContentInput']"))
                .sendKeys(itemName);

        chrome.findElement(By.xpath("//input[@id='NewItemAddButton']"))
                .click();

        int numberOfItemsByItemName = chrome.findElements(By.xpath("//div[text()='" + itemName + "']")).size();
        Assertions.assertEquals(1, numberOfItemsByItemName, "ERROR: the item was not created");

        Thread.sleep(1000);
        // update item
        // do a hover over the item to be able to click on the img
        action.moveToElement(chrome.findElement(By.xpath("//div[text()='" + itemName + "']"))).perform();
        chrome.findElement(By.xpath("//div[text()='" + itemName + "']/parent::td/parent::tr/td/div/img")).click();
        chrome.findElement(By.xpath("//ul[@id='itemContextMenu']/li/a[text()='Edit']"))
                .click();

        String newItemName = "Dylan Item Updated - " + new Date().getTime();

        WebElement itemInput = chrome.findElement(By.xpath("//div[@class='ItemContentDiv UnderEditingItem']//div//textarea"));
        itemInput.clear();
        itemInput.sendKeys(newItemName);

        //TODO: no actualiza el nombre del item al usar el boton save
//        chrome.findElement(By.xpath("//div[@class='ItemContentDiv UnderEditingItem']//div//img[@id='ItemEditSubmit']"))
//                .click();
        // do click eveywhere outside the item input

        chrome.findElement(By.xpath("//body")).click();

        Thread.sleep(1000);

        int numberOfItemsByItemNameUpdated = chrome.findElements(By.xpath("//td//div[text()='" + newItemName + "']")).size();
        Assertions.assertEquals(1, numberOfItemsByItemNameUpdated, "ERROR: the item was not updated");

        // delete item
        action.moveToElement(chrome.findElement(By.xpath("//div[text()='" + newItemName + "']"))).perform();
        chrome.findElement(By.xpath("//div[text()='" + newItemName + "']/parent::td/parent::tr/td/div/img")).click();
        chrome.findElement(By.xpath("//ul[@id='itemContextMenu']/li/a[text()='Delete']"))
                .click();

        Thread.sleep(1000);
        int numberOfItemsByItemNameDeleted = chrome.findElements(By.xpath("//div[@class='ItemContentDiv DoneItem' and text()='" + newItemName + "']"))
                .size();

        Assertions.assertEquals(0, numberOfItemsByItemNameDeleted, "ERROR: the item was not deleted");
    }


}
