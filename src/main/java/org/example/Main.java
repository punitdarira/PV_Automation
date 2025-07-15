package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.util.Iterator;
import java.time.Duration;

import org.apache.commons.lang3.StringUtils;

// run chrome on localhost first
//chrome.exe --remote-debugging-port=9222 --user-data-dir=C:\Users\Owner\Desktop\chrome-user-data-dir1
public class Main {
    public static void main(String[] args) throws Exception {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Administrator\\Desktop\\PV_Automation\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("debuggerAddress", "localhost:9222");

        FileInputStream file = new FileInputStream("C:\\Users\\Administrator\\Desktop\\PV_Automation\\PV.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();

        WebDriver driver = new ChromeDriver(chromeOptions);

        while (rowIterator.hasNext()) {
            Row excelRow = rowIterator.next();
            if (itemNotAlreadyFilled(driver, excelRow)) {
                //selecting item
                selectInspectionItem(driver, excelRow);

                //setting description text
                setDescriptionText(driver, excelRow);

                //getting condition dropbox
                selectDropBox(driver, excelRow);

                //save inspection
                saveInspection(driver);
            }
        }
        file.close();
    }

    private static boolean itemNotAlreadyFilled(WebDriver driver, Row row) {
        waitForAjaxToComplete(driver);
        String divToCheck = String.format("//*[@id=\"gwt-debug-dataTable-row-%s\"]/td[4]/div/div",
                (int) row.getCell(0).getNumericCellValue());
        WebElement divWebElement = driver.findElement(By.xpath(divToCheck));
        return StringUtils.isEmpty(divWebElement.getText());
    }

    private static void selectInspectionItem(WebDriver driver, Row row) {
        waitForAjaxToComplete(driver);

        Cell RowIdCell = row.getCell(0);
        String rowId = String.format("gwt-debug-dataTable-row-%s", (int) RowIdCell.getNumericCellValue());
        WebElement webElement = driver.findElement(By.id(rowId));
        webElement.click();
    }

    private static void selectDropBox(WebDriver driver, Row excelRow) {
        waitForAjaxToComplete(driver);

        WebElement webElement = driver.findElement(By.id("gwt-debug-InspectionItemObjectDTO-inspectionConditionValue"));
        Select selectWebElement = new Select(webElement);
        selectWebElement.selectByVisibleText(excelRow.getCell(3).getStringCellValue());
    }

    private static void setDescriptionText(WebDriver driver, Row excelRow) {
        waitForAjaxToComplete(driver);

        WebElement commentsWebElement = driver.findElement(By.id("gwt-debug-InspectionItemObjectDTO-areaComments"));
        String currentVal = commentsWebElement.getAttribute("value");
        if (StringUtils.isEmpty(currentVal) && !StringUtils.isEmpty(excelRow.getCell(4).getStringCellValue())) {
            commentsWebElement.click();
            commentsWebElement.sendKeys(excelRow.getCell(4).getStringCellValue());
            commentsWebElement.sendKeys(Keys.TAB);
        }
    }

    private static void saveInspection(WebDriver driver) {
        waitForAjaxToComplete(driver);

        WebElement saveButtonwebElement = driver.findElement(By.xpath("//*[text()='Save']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", saveButtonwebElement);

    }

    public static void waitForAjaxToComplete(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(1));

        wait.until(driver1 -> {
            WebElement secondDiv = driver1.findElement(By.xpath("//body/div[2]"));
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver1;
            String displayProperty = (String) jsExecutor.executeScript(
                    "return window.getComputedStyle(arguments[0], null).getPropertyValue('display');", secondDiv);
            return "none".equals(displayProperty);
        });
        //Thread.sleep(500);
    }
}