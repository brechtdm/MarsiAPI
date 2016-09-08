package controllers;

import play.mvc.Result;

import static play.mvc.Results.ok;

public class ApplicationController {

    public Result index() {
        return ok("APPLICATION_ONLINE");
    }
}
