package org.openmrs.module.chits.web.controller.admin;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.openmrs.module.chits.Constants;
import org.openmrs.module.chits.UploadFileForm;
import org.openmrs.module.chits.web.LocalMultipartZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload note templates from a ZIP file.
 */
@Controller
@RequestMapping(value = "/module/chits/admin/templates/installPackagedTemplates.form")
public class InstallPackagedTemplatesController extends UploadTemplatesController implements Constants {
	/** Version used to check against the 'chits.templates.version' global property to determine if this controller should be run during startup. */
	public static String VERSION = "1.1.2";

	/** The servlet context */
	private ServletContext servletContext;

	/**
	 * This method will start the patient's consult.
	 * 
	 * @param httpSession
	 *            current browser session
	 * @return the view to be rendered
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(HttpSession httpSession, //
			@ModelAttribute("form") UploadFileForm form, //
			BindingResult errors) {
		// get context root
		final File contextRootPath = new File(servletContext.getRealPath("/"));
		final File templatesFile = new File(contextRootPath, "WEB-INF/view/module/chits/Templates.zip");

		// prepare a multipart file that can be used by the upload templates method
		final MultipartFile mpf = new LocalMultipartZipFile(templatesFile);

		// prepare the form
		form.setFile(mpf);

		// perform upload templates logic
		super.uploadTemplates(httpSession, form, errors);

		// send to the admin page
		return "redirect:/admin/index.htm";
	}

	@Autowired
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
