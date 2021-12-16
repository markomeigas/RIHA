package ee.ria.riha.controllers;

import ee.ria.riha.enums.ExceptionEnum;
import ee.ria.riha.models.Infosystem;
import ee.ria.riha.services.DateTimeService;
import ee.ria.riha.services.InfosystemStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InfosystemControllerTest {

  @Mock InfosystemStorageService infosystemStorageService;
  @Mock DateTimeService dateTimeService;

  @Spy @InjectMocks
  private InfosystemController controller = new InfosystemController();

  @Test
  public void save() {
    doReturn(ZonedDateTime.of(2016, 1, 1, 10, 11, 12, 0, ZoneId.of("Europe/Tallinn"))).when(dateTimeService).now();
    controller.owner = "123";
    controller.baseUrl = "http://base.url";
    doReturn(true).when(controller).isValid(any(Infosystem.class));

    controller.save(null, "name", "shortName", "docUrl", "purpose");

    ArgumentCaptor<Infosystem> infosystemArgument = ArgumentCaptor.forClass(Infosystem.class);
    verify(infosystemStorageService).save(infosystemArgument.capture());
    Infosystem infosystem = infosystemArgument.getValue();
    assertEquals("name", infosystem.getName());
    assertEquals("shortName", infosystem.getShortname());
    assertEquals("docUrl", infosystem.getDocumentation());
    assertEquals("purpose", infosystem.getPurpose());
    assertEquals("123", infosystem.getOwner().getCode());
    assertEquals("2016-01-01T08:11:12", infosystem.getMeta().getSystem_status().getTimestamp());
    assertEquals("http://base.url/shortName", infosystem.getUri());
  }

  @Test
  public void save_updatesExisting() {
    doReturn(ZonedDateTime.of(2016, 1, 1, 10, 11, 12, 0, ZoneId.of("Europe/Tallinn"))).when(dateTimeService).now();
    controller.owner = "123";
    controller.baseUrl = "http://base.url";
    doReturn(true).when(controller).isValid(any(Infosystem.class));

    controller.save("existing-shortName", "name", "new-shortName", "docUrl", "abc");

    ArgumentCaptor<Infosystem> infosystemArgument = ArgumentCaptor.forClass(Infosystem.class);
    verify(infosystemStorageService).save(eq("existing-shortName"), infosystemArgument.capture());
    Infosystem infosystem = infosystemArgument.getValue();
    assertEquals("name", infosystem.getName());
    assertEquals("new-shortName", infosystem.getShortname());
    assertEquals("docUrl", infosystem.getDocumentation());
    assertEquals("abc", infosystem.getPurpose());
    assertEquals("123", infosystem.getOwner().getCode());
    assertEquals("2016-01-01T08:11:12", infosystem.getMeta().getSystem_status().getTimestamp());
    assertEquals("http://base.url/new-shortName", infosystem.getUri());
  }

  @Test
  public void edit() {
    Infosystem infosystem = mock(Infosystem.class);
    doReturn(infosystem).when(infosystemStorageService).find("shortname");
    Model model = mock(Model.class);
    
    controller.edit(model, "shortname");

    verify(model).addAttribute("infosystem", infosystem);
  }

  @Test(expected = BadRequest.class)
  public void save_doesNotSaveInvalidInfosystem() {
    doReturn(ZonedDateTime.of(2016, 1, 1, 10, 11, 12, 0, ZoneId.of("Europe/Tallinn"))).when(dateTimeService).now();
    doReturn(false).when(controller).isValid(any(Infosystem.class));

    controller.save(null, "", "", "", "");

    verify(infosystemStorageService, never()).save(any(Infosystem.class));
  }

  @Test
  public void badRequest() {
    assertEquals("<404 Not Found,{\"message\":\"error.bad.request\"},{Content-Type=[application/json;charset=UTF-8]}>", controller.handleAppException(new BadRequest(ExceptionEnum.ERROR_BAD_REQUEST)).toString());
    assertEquals("<409 Conflict,{\"message\":\"error.already.exists\"},{Content-Type=[application/json;charset=UTF-8]}>", controller.handleAppException(new BadRequest(ExceptionEnum.ERROR_INFOSYSTEM_ALREADY_EXISTS)).toString());
  }

  @Test
  public void json() {
    doReturn("[{\"name\":\"infosystem name\"}]").when(infosystemStorageService).load();

    JSONAssert.assertEquals("[{\"name\":\"infosystem name\"}]", controller.json(), true);
  }

  @Test
  public void isValid() {
    assertTrue(controller.isValid(new Infosystem("name", "shortName", "docUrl", "abc" , "12345", "2016-12-10T01:00:00", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("", "shortName", "docUrl", "abc", "12345", "2016-12-10T01:00:00", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("name", "", "docUrl", "abc", "12345", "2016-12-10T01:00:00", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("name", "shortName", "", "abc", "12345", "2016-12-10T01:00:00", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("name", "shortName", "docUrl", "", "12345", "2016-12-10T01:00:00", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("name", "shortName", "docUrl", "abc", "12345", "", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem("    ", "   ", "   ", "   ", "   ", "  ", "http://base.url")));
    assertFalse(controller.isValid(new Infosystem(null, null, null, null, null, null, "http://base.url")));
  }

  @Test
  public void delete() {
    controller.delete("shortname");

    verify(infosystemStorageService).delete("shortname");
  }
}