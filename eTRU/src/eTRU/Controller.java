package eTRU;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
//import org.omg.CORBA.ServerRequest;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Controller extends AbstractHandler {
	
	/** instance variables */
	Server jettyServer;
	View view;
	Model model;
	File file;
	String currentBookID;
	
	/**
	 * Constructor
	 * 
	 * @param baseURL
	 * @param port
	 * @throws IOException
	 */
	public Controller(String baseURL, int port) throws IOException {
		// view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();
		view = new View();

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join();
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println(jettyReq);

		ServerRequest request = new ServerRequest(req, resp);
		System.out.println(request);
	}

	/**
	 * When a user submits (enter key) or pressed the "Write!" button, we'll get
	 * their request in here. This is called explicitly from handle, above.
	 * 
	 * @param req
	 *            -- we'll grab the form parameters from here.
	 * @param resp
	 *            -- where to write their "success" page.
	 * @throws IOException
	 *             again, real life happens.
	 */
	private void handleSearchForm(HttpServletRequest req, HttpServletResponse resp, ServerRequest request)
			throws IOException {
		Map<String, String[]> parameterMap = req.getParameterMap();

		// if for some reason, we have multiple "message" fields in our form,
		// just put a space between them, see Util.join.
		// Note that message comes from the name="message" parameter in our
		// <input> elements on our form.
		String search = Util.join(parameterMap.get("q"));
		System.out.print("Search: " + search);

		// will turn this into an empty string if no p
		String page = Util.join(parameterMap.get("p"));
		// if page number is null, set it to be the first page
		if (page == null) {
			page = "1";
		}
		// split the letters in the string to find books similar to the letters
		String[] searches = search.split(" ");
		// if form is valid
		if (searches != null) {
			// Good, got new message from form.
			resp.setStatus(HttpServletResponse.SC_ACCEPTED);
			
			// do shit here
			
			return;
		}
		// user submitted something weird.
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
	}
}
