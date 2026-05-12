package com.booking.bus.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

public class BookingSystemTest extends SeleniumBaseTest {

    private void goHome() {
        driver.get(baseUrl + "/");
    }

    private void fillStop(String fieldName, String value) {
        WebElement input = driver.findElement(By.name(fieldName));
        input.clear();
        input.sendKeys(value);
    }

    // 1. Поиск рейсов
    @Test
    public void testSearchTripsSuccess() {
        goHome();

        fillStop("fromStop", "Москва (автовокзал)");
        fillStop("toStop", "Казань");
        driver.findElement(By.name("date")).clear();
        driver.findElement(By.name("date")).sendKeys("5-20-2026");
        System.out.println("Date value: " + driver.findElement(By.name("date")).getAttribute("value"));

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
        boolean hasRows = driver.findElements(By.cssSelector("tbody tr")).size() > 0;
        Assert.assertTrue(hasRows, "Не найдено ни одного маршрута");
    }
    // Нету
    @Test
    public void testSearchNoTrips() {
        goHome();
        fillStop("fromStop", "Москва (автовокзал)");
        fillStop("toStop", "Казань");
        driver.findElement(By.name("date")).clear();
        driver.findElement(By.name("date")).sendKeys("5-20-2027");
        System.out.println("Date value: " + driver.findElement(By.name("date")).getAttribute("value"));

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Попробуйте')]"))).getText();
        Assert.assertTrue(message.contains("Попробуйте"), "Сообщение об отсутствии рейсов не появилось");
    }

    // 2. Регистрация
    @Test
    public void testRegisterClientSuccess() {
        goHome();
        driver.findElement(By.linkText("Регистрация")).click();
        driver.findElement(By.name("fullName")).sendKeys("Тестов Тест Тестович");
        driver.findElement(By.name("email")).sendKeys("selenium@test.ru");
        driver.findElement(By.name("phone")).sendKeys("+7(999)999-99-99");
        driver.findElement(By.name("address")).sendKeys("г. Тест, ул. Тестовая, д.1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/clients"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/clients"));
        boolean found = driver.findElements(By.xpath("//td[contains(text(),'selenium@test.ru')]")).size() > 0;
        Assert.assertTrue(found, "Клиент не отображается в списке");
    }
    // Дубликат email
    @Test(dependsOnMethods = "testRegisterClientSuccess")
    public void testRegisterClientDuplicateEmail() {
        goHome();
        driver.findElement(By.linkText("Регистрация")).click();
        driver.findElement(By.name("fullName")).sendKeys("Дубликат");
        driver.findElement(By.name("email")).sendKeys("selenium@test.ru");
        driver.findElement(By.name("phone")).sendKeys("+7(111)111-11-11");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/clients"));
        int count = driver.findElements(By.xpath("//td[contains(text(),'selenium@test.ru')]")).size();
        Assert.assertTrue(count >= 1, "Дубликат не создан или пропал клиент");
    }
    // Редактирование профиля
    @Test
    public void testEditClientProfile() {
        goHome();
        driver.findElement(By.linkText("Клиенты")).click();
        driver.findElement(By.xpath("//td[contains(text(),'selenium@test.ru')]/following-sibling::td/a")).click();

        WebElement phoneField = driver.findElement(By.name("phone"));
        phoneField.clear();
        phoneField.sendKeys("+7(555)555-55-55");
        driver.findElement(By.xpath("//button[contains(text(),'Сохранить изменения')]")).click();

        driver.navigate().refresh();
        String updatedPhone = driver.findElement(By.name("phone")).getAttribute("value");
        Assert.assertEquals(updatedPhone, "+7(555)555-55-55", "Телефон не обновился");
    }

    // 3. Бронирование
    @Test(dependsOnMethods = "testSearchTripsSuccess")
    public void testBookingSuccess() {
        goHome();
        fillStop("fromStop", "Москва (автовокзал)");
        fillStop("toStop", "Казань");
        driver.findElement(By.name("date")).sendKeys("5-20-2026");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        List<WebElement> links = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//a[contains(text(),'Выбрать рейс')]")));
        if (links.size() > 1) {
            links.get(1).click();
        } else {
            links.get(0).click();
        }

        Select fromStopSelect = new Select(driver.findElement(By.name("fromStopId")));
        fromStopSelect.selectByVisibleText("Москва (автовокзал)");
        Select toStopSelect = new Select(driver.findElement(By.name("toStopId")));
        toStopSelect.selectByVisibleText("Казань");
        driver.findElement(By.name("date")).sendKeys("5-20-2026");
        driver.findElement(By.xpath("//button[contains(text(),'Показать рейсы')]")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table/tbody/tr[1]/td[2]/a"))).click();

        driver.findElement(By.name("clientName")).sendKeys("Тестов Бронировщик");
        driver.findElement(By.name("clientPhone")).sendKeys("+7(111)222-33-44");
        driver.findElement(By.name("clientEmail")).sendKeys("booking@test.ru");
        Select seatSelect = new Select(driver.findElement(By.name("seatNumber")));
        seatSelect.selectByIndex(1);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'забронирован')]"))).getText();
        Assert.assertTrue(successMsg.contains("забронирован"), "Не отобразилось сообщение об успешном бронировании");
    }

    // 4. Добавление маршрута
    @Test
    public void testAddRoute() {
        goHome();
        driver.findElement(By.linkText("Все рейсы")).click();
        driver.findElement(By.linkText("Добавить маршрут")).click();

        driver.findElement(By.name("routeNumber")).sendKeys("TEST-117");
        driver.findElement(By.name("routeDescription")).sendKeys("Тестовый маршрут");
        driver.findElement(By.name("busCapacity")).sendKeys("30");

        Select companySelect = new Select(driver.findElement(By.name("companyId")));
        companySelect.selectByIndex(1);

        WebElement firstTime = driver.findElement(By.name("departureTimes"));
        firstTime.sendKeys("13:00");

        driver.findElement(By.xpath("//button[contains(text(),'Добавить время')]")).click();
        List<WebElement> timeInputs = driver.findElements(By.name("departureTimes"));
        Assert.assertTrue(timeInputs.size() >= 2, "Второе поле времени не добавилось");
        timeInputs.get(1).sendKeys("18:00");

        WebElement firstStopName = driver.findElement(By.name("stopName"));
        WebElement firstStopOffset = driver.findElement(By.name("stopOffset"));
        firstStopName.sendKeys("Тверь");
        firstStopOffset.sendKeys("1");

        driver.findElement(By.xpath("//button[contains(text(),'Добавить остановку')]")).click();
        List<WebElement> stopNames = driver.findElements(By.name("stopName"));
        List<WebElement> stopOffsets = driver.findElements(By.name("stopOffset"));
        Assert.assertTrue(stopNames.size() >= 2, "Вторая остановка не добавилась");
        stopNames.get(1).sendKeys("Казань");
        stopOffsets.get(1).sendKeys("600");

        driver.findElement(By.xpath("//button[contains(text(),'Сохранить маршрут')]")).click();

        wait.until(ExpectedConditions.urlContains("/routes"));
        boolean found = driver.findElements(By.xpath("//td[contains(text(),'TEST-117')]")).size() > 0;
        Assert.assertTrue(found, "Добавленный маршрут не появился в списке");
    }

    // 5. Автодополнение
    @Test
    public void testAutocomplete() {
        goHome();
        WebElement fromStop = driver.findElement(By.name("fromStop"));
        fromStop.sendKeys("М");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ui-menu-item")));

        driver.findElement(By.xpath("//li[contains(.,'Москва (автовокзал)')]")).click();

        String value = fromStop.getAttribute("value");
        Assert.assertEquals(value, "Москва (автовокзал)", "Автодополнение не сработало");
    }

}
