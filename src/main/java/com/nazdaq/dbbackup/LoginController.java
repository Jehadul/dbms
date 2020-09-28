package com.nazdaq.dbbackup;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nazdaq.dbbackup.model.DbBackupHistory;
import com.nazdaq.dbbackup.model.User;
import com.nazdaq.dbbackup.service.BackupHistoryService;
import com.nazdaq.dbbackup.service.UserService;



@Controller
public class LoginController extends SavedRequestAwareAuthenticationSuccessHandler{
		
	@Autowired
	UserService userService;
	
	@Autowired
	BackupHistoryService backupHistoryService;
	
	
	@RequestMapping(value="/success", method = RequestMethod.GET)
	public String success(ModelMap model) {
	return "success"; 
	}
	
	@RequestMapping(value={"/","/index"}, method = RequestMethod.GET)
	public ModelAndView printWelcome1(ModelMap model, Principal principal, HttpSession session, HttpServletRequest request) {
		String pageLocation=null;
		User user=null;
	String name = principal.getName();
	user=userService.getUser(name);
		
	
	session.setAttribute("userr", name);
	session.setAttribute("uid", 1);
	Short userId=user.getEmpId();
	session.setAttribute("userrId", userId);
	model.addAttribute("userName", session.getAttribute("userr"));
	model.addAttribute("userId", session.getAttribute("userrId"));
	
	// added by taleb
	if(user.getLoginCounter() == null || user.getLoginCounter().equals("")){		
		model.addAttribute("loginUser", user);
		user.setLoginCounter("0");
		userService.addUser(user);
	}else{
		Integer counter = Integer.parseInt(user.getLoginCounter());
		user.setLoginCounter((counter+1)+"");
		userService.addUser(user);
	}
	
	
	Map<String, Object> modelStr = new HashMap<String, Object>();

	pageLocation="backupHistory";
	
	
	
	Date endDate = new Date();
	Date startDate = new DateTime(endDate).minusDays(30).toDate();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String fromDate = sdf.format(startDate);
	String toDate = sdf.format(endDate);
	
	List<DbBackupHistory> dbBackupHistoryList = backupHistoryService.getDbBackupHistoryListByDateRange(fromDate, toDate);
	model.put("backupHistory", dbBackupHistoryList);
	return new ModelAndView(pageLocation, modelStr);
	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login() {
 		return "login";
	}
 
	@RequestMapping(value="/loginfailed", method = RequestMethod.GET)
	public String loginerror(Model model) {
 
		model.addAttribute("error", "true");
		return "login";
 
	}
 
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.invalidate();
 		return "login";
 	}
	
	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	protected String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}
	
	
}
