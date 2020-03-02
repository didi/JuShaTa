package com.didiglobal.sample.standard;

import com.didiglobal.sample.api.SampleBean;
import com.didiglobal.sample.api.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("sampleStandardService")
public class SampleStandardServiceImpl implements SampleService {

    @Autowired
    @Qualifier("javaSampleBean")
    private SampleBean javaSampleBean;

    @Autowired
    @Qualifier("xmlSampleBean")
    private SampleBean xmlSampleBean;

    @Override
    public String echo() {

        return "helloworld, <br/>" //
               + "" + javaSampleBean + "<br/>" //
               + "" + xmlSampleBean + "<br/>" //
               + "" + xmlSampleBean.getClass().getClassLoader() + "<br/>" //
               + "" + this;
    }
}
