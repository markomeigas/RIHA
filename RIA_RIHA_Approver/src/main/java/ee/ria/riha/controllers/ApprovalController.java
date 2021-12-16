package ee.ria.riha.controllers;

import ee.ria.riha.models.Approval;
import ee.ria.riha.models.Status;
import ee.ria.riha.services.ApprovalStorageService;
import ee.ria.riha.services.DateTimeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static ee.ria.riha.services.DateTimeService.format;
import static ee.ria.riha.services.DateTimeService.toUTC;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ApprovalController {

  @Autowired ApprovalStorageService approvalStorageService;
  @Autowired DateTimeService dateTimeService;

  @Value("${infosystems.url}")
  private String infosystemsUrl;

  @RequestMapping(value = "/", method = GET)
  public String index(Model model) {
    model.addAttribute("approvedStatus", Status.APPROVED.getValue());
    model.addAttribute("notApprovedStatus", Status.NOT_APPROVED.getValue());
    model.addAttribute("infosystemsUrl", infosystemsUrl);
    return "index";
  }

  @RequestMapping(value = "/approvals", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String approvals() {
    return new JSONArray(approvalStorageService.allApprovals()).toString();
  }

  @RequestMapping(value = "/approvals/approved/", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String approvedApprovals() {
    return new JSONArray(approvalStorageService.approvedApprovals()).toString();
  }

  @RequestMapping(value = "/approve/", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String updateApprovalStatus(@RequestParam String id, String status){
    Approval approval = new Approval(id, format(toUTC(dateTimeService.now())), status);
    approvalStorageService.saveInfosystemApproval(approval);
    return new JSONObject(approval).toString();
  }
}