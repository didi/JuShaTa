package com.didiglobal.sample.child1;

import com.didiglobal.sample.api.SampleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SampleChild1Controller {

    @Resource(name = "sampleChildService")
    private SampleService sampleService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloworld() {

        return sampleService.echo();
    }
}
