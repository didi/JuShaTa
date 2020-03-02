package com.didiglobal.sample.standard;

import com.didiglobal.sample.api.SampleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SampleStandardController {

    @Resource(name = "sampleStandardService")
    private SampleService sampleService;

    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    public String helloworld() {

        return sampleService.echo();
    }

}
