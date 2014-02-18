package controllers;


import models.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.Result;
import services.SlektService;
import views.html.person;

@org.springframework.stereotype.Controller
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    private SlektService slektService;

    public Result index() {
        return play.mvc.Controller.ok();
    }

    public Result person(String id) {
        return play.mvc.Controller.ok(person.render(id));
    }

    public Result hentPerson(String id) {
        Person person = slektService.getPerson(id);

        return play.mvc.Controller.ok(Json.toJson(slektService.getPerson(id)));
    }

    public Result listFamilier(String id) {
        return play.mvc.Controller.ok(Json.toJson(slektService.getFamilier(id)));
    }

    public Result listForeldre(String id, Integer nivaa) {
        return play.mvc.Controller.ok(Json.toJson(slektService.getForeldre(id, nivaa)));
    }

    public Result listBarn(String id) {
        return play.mvc.Controller.ok(Json.toJson(slektService.getBarn(id)));
    }

    public Result listSosken(String id) {
        return play.mvc.Controller.ok(Json.toJson(slektService.getSosken(id)));
    }

    public Result listMenninger(String id, Integer nivaa) {
        return play.mvc.Controller.ok(Json.toJson(slektService.getMenninger(id, nivaa)));
    }
    
}