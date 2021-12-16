package ee.ria.riha.controllers;

import ee.ria.riha.enums.ExceptionEnum;
import ee.ria.riha.models.Infosystem;
import ee.ria.riha.services.DateTimeService;
import ee.ria.riha.services.InfosystemStorageService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import static ee.ria.riha.services.DateTimeService.format;
import static ee.ria.riha.services.DateTimeService.toUTC;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
public class InfosystemController {

  @Autowired InfosystemStorageService infosystemStorageService;

  @Autowired DateTimeService dateTimeService;

  @Value("${owner}")
  String owner;

  @Value("${base.url}")
  String baseUrl;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {
    return "index";
  }

  @RequestMapping(value = "/form/", method = RequestMethod.GET)
  public String form() {
    return "form";
  }

  @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
  public String edit(Model model, @PathVariable("id") String id) {
    Infosystem infosystem = infosystemStorageService.find(id);
    model.addAttribute("infosystem", infosystem);
    return "form";
  }

  @RequestMapping(value = "/save/", method = RequestMethod.POST)
  public String save(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("shortName") String shortName,
                     @RequestParam("documentation") String documentation, @RequestParam("purpose") String purpose) {
    Infosystem infosystem = new Infosystem(name, shortName, documentation, purpose, owner, format(toUTC(dateTimeService.now())), baseUrl);
    if (!isValid(infosystem)) throw new BadRequest(ExceptionEnum.ERROR_BAD_REQUEST);
    if ((!isEmpty(id) && !id.equals(shortName) || isEmpty(id)) && infosystemStorageService.find(shortName) != null)
      throw new BadRequest(ExceptionEnum.ERROR_INFOSYSTEM_ALREADY_EXISTS);

    if (isEmpty(id)) {
      infosystemStorageService.save(infosystem);
    } else {
      infosystemStorageService.save(id, infosystem);
    }
    return "redirect:/";
  }

  @RequestMapping(value = "/delete/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  public void delete(@RequestParam("id") String shortName) {
    infosystemStorageService.delete(shortName);
  }

  @RequestMapping(value = "/systems.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String json() {
    return infosystemStorageService.load();
  }

  boolean isValid(Infosystem infosystem) {
    return isNotBlank(infosystem.getName())
      && isNotBlank(infosystem.getShortname())
      && isNotBlank(infosystem.getDocumentation())
      && isNotBlank(infosystem.getPurpose())
      && isNotBlank(infosystem.getOwner().getCode())
      && isNotBlank(infosystem.getMeta().getSystem_status().getTimestamp());
  }

  @ExceptionHandler(BadRequest.class)
  public ResponseEntity handleAppException(BadRequest e) {
    JSONObject messages = new JSONObject();
    messages.put("message", e.getErrorCode());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    if (e.getErrorCode() == ExceptionEnum.ERROR_INFOSYSTEM_ALREADY_EXISTS.getErrorCode()) {
      return new ResponseEntity(messages.toString(), headers, HttpStatus.CONFLICT);
    } else {
      return new ResponseEntity(messages.toString(), headers, HttpStatus.NOT_FOUND);
    }
  }
}
