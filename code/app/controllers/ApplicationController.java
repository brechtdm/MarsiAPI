package controllers;

import play.mvc.Result;
import util.json.ResultCode;
import util.json.results.DataJsonResult;

import static play.mvc.Results.ok;

public class ApplicationController {

    public Result index() {
        DataJsonResult result = new DataJsonResult(ResultCode.OK, "APPLICATION_ONLINE");
        return ok(result.toJson());
    }
}
